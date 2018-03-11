package cz.zcu.sdutends.kiwi;


import cz.zcu.sdutends.kiwi.lucene.LuceneJob;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class IrMain {
    public static void main(String[] args) {
        //Initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        IrJob job = new LuceneJob(args);

        // todo: specify job, parameters...

        job.run();
    }
}
