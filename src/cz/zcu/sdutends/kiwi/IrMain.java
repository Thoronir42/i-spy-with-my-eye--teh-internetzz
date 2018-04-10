package cz.zcu.sdutends.kiwi;


import cz.zcu.sdutends.kiwi.ted.TedClusteringJob;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class IrMain {
    public static void main(String[] args) {
        //Initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

//        IrJob job = new TedCrawlJob();
//        IrJob job = new LuceneJob<>(new TedLuceneModule());
//        IrJob job = new TedElasticJob();
        IrJob job = new TedClusteringJob();

        // todo: specify job, parameters...

        job.run();
    }
}
