package cz.zcu.sdutends.kiwi;


import cz.zcu.sdutends.kiwi.ted.TedJob;
import cz.zcu.sdutends.kiwi.ted.lucene.TedLuceneJob;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class IrMain {
    public static void main(String[] args) {
        //Initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        IrJob job = new TedLuceneJob(args);

        // todo: specify job, parameters...

        job.run();
    }
}
