package cz.zcu.sdutends.kiwi.crosslining;

import cz.zcu.kiv.nlp.ir.preprocessing.PreProcessing;
import cz.zcu.kiv.nlp.jstein.crosslingcoref.domain.Article;
import cz.zcu.kiv.nlp.jstein.crosslingcoref.domain.Corpus;
import cz.zcu.kiv.nlp.jstein.crosslingcoref.domain.Mention;
import cz.zcu.kiv.nlp.jstein.crosslingcoref.io.DataHandler;
import cz.zcu.sdutends.kiwi.IrJob;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CrossLingCorefJob extends IrJob {

    private final String DATA_DIR = "data/crossLining/mentions";
    private final String OUT_DIR = "CorefEvaluatorDevelopment/data/kiwi";

    private Map<String, Integer> entityIDs;
    private final PreProcessingFactory preProcessingFactory;

    public CrossLingCorefJob() {
        this.preProcessingFactory = new PreProcessingFactory();
    }

    public boolean execute() throws IOException {
        DataHandler dataHandler = new DataHandler();

        Map<String, Corpus> corpora = new HashMap<>(dataHandler.loadAnnotations(DATA_DIR));

        entityIDs = new HashMap<>();

        for (Map.Entry<String, Corpus> entry : corpora.entrySet()) {
            processCorpus(entry.getKey(), entry.getValue());
        }

        return true;
    }

    private boolean processCorpus(String corpusName, Corpus corpus) {
        log.info("Processing corpus: " + corpusName);

        for (Map.Entry<String, Map<String, Article>> entry : corpus.getArticles().entrySet()) {
            String language = entry.getKey();
            log.info("- Processing language: " + language);

            Map<String, Article> articles = entry.getValue();

            processArticles(corpusName, language, articles);
        }

        return true;
    }

    private boolean processArticles(String corpus, String language, Map<String, Article> articles) {
        String outDir = OUT_DIR + File.separator + corpus + File.separator + language + File.separator;

        File dirFile = new File(outDir);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new IllegalStateException("Could not create directory " + dirFile.getPath());
            }
        }

        PreProcessing preProcessing = preProcessingFactory.get(language);


        for (Map.Entry<String, Article> articleEntry : articles.entrySet()) {
            log.info("-- Processing article: " + articleEntry.getKey());
            Article article = articleEntry.getValue();

            String outFilePath = outDir + article.getFileName();

            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilePath), "UTF8"))) {
                out.write(article.getId() + "\n");
                processMentions(out, preProcessing, article.getMentions());

            } catch (IOException ex) {
                log.error("Failed opening buffered writer: " + ex.toString());
            }

        }

        return true;
    }

    private void processMentions(BufferedWriter out, PreProcessing preProcessing, Collection<Mention> mentions) throws IOException {
        for (Mention mention : mentions) {
            String mentionText = mention.getText();

            if (entityIDs.containsKey(mentionText)) {
                mention.setId(String.valueOf(entityIDs.get(mentionText)));
            } else {
                int newId = entityIDs.size() + 1;
                entityIDs.put(mentionText, newId);
                mention.setId(String.valueOf(newId));
            }

            String[] parts = {mention.getText(), mention.getNormalized(), mention.getType().toString(), mention.getId()};

            out.write(String.join("\t", parts) + "\n");
        }
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        CrossLingCorefJob coref = new CrossLingCorefJob();
        coref.run();

    }
}
