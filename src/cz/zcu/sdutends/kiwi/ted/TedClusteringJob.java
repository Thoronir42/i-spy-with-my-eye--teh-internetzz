package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.ir.preprocessing.*;
import cz.zcu.kiv.nlp.tigi.clustering.WordClustering;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.ted.model.TalkStructured;
import cz.zcu.sdutends.kiwi.ted.serdes.TalkStructuredSerDes;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;
import edu.ucla.sspace.mains.CoalsMain;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

public class TedClusteringJob extends IrJob {

    /**
     * Logger configuration.
     */
    protected static void configureLogger(String outputDirectory) throws IllegalAccessException {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        File results = new File(outputDirectory);
        if (!results.exists()) {
            if (!results.mkdirs()) {
                throw new IllegalAccessException("Could not create output directory");
            }
        }

        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @Override
    protected boolean execute() throws Exception {
        TedClusteringJobSettings settings = new TedClusteringJobSettings();

        if (!this.ensureDirectoriesExist(settings.getOutputDirectory())) {
            log.warn("Failed to create output directory");
            return false;
        }

        log.info("Formatting starting");
        if (!formatDocuments(settings)) {
            return false;
        }
        log.info("Formatting finished");

        log.info("Clustering starting");
        if (!runClustering(settings)) {
            log.warn("Failed to run the clustering part");
            return false;
        }
        log.info("Clustering finished");

        return true;
    }

    private boolean formatDocuments(TedClusteringJobSettings settings) {
        AdvancedIO<TalkStructured> taio = new AdvancedIO<>(new TalkStructuredSerDes());
        List<TalkStructured> talks = taio.loadFromDirectory(settings.getInputDirectory());

        Collection<String> stopWords = StopwordsLoader.load("cz.txt");

        BasicPreProcessing preProcessing = new BasicPreProcessing(new AdvancedTokenizer(), stopWords)
                .addDocumentOperation(String::toLowerCase)
                .applyDocumentOperationsOnStopWords();

        File clusteringFile = new File(settings.getClusteringFile());
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(clusteringFile))) {
            int n = 0;
            for (TalkStructured talk : talks) {
                String[] tokens = preProcessing.tokenizeAndPreprocess(talk.getIntroduction());
                String words = String.join(" ", tokens);

                n++;
//                System.out.println(n + "\t" + "X" + "\t" + words);
                pw.println(n + "\t" + "X" + "\t" + words);
            }
        } catch (IOException ex) {
            log.error("Failed opening cluster file for writing");
            return false;
        }

        return true;
    }

    private boolean runClustering(TedClusteringJobSettings settings) throws Exception {
        final String sspace = settings.getOutputDirectory() + "/model_cs.sspace";
        final String matrix = sspace + ".forcluto.mat";


        File matrixFile = new File(matrix);
        if (matrixFile.exists()) {
            matrixFile.delete();
        }

        CoalsMain.main(new String[]{"--docFile", settings.getClusteringFile(), "-n", "14000", "-t", "8", sspace});
        for (int NUM_CLUSTERS : new int[]{100, 500, 1000}) {

            File sspaceFile = new File(sspace);
            log.info("SSpace file '" + sspace + "' exists: " + sspaceFile.exists());
            new WordClustering().cluster(sspaceFile, NUM_CLUSTERS);
        }

        return true;
    }
}
