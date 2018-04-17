package cz.zcu.sdutends.kiwi.weka;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class WekaJobSettings extends IrJobSettings {

    //file with word embedding
    // alternatively: "./data/embeddings/w2v.twitter.edinburgh10M.400d.csv.gz"
    public String wordEmbeddingsPath = "./data/embeddings/w2v.twitter.edinburgh.100d.csv.gz";

    //raw test and train data
    public String rawDataTest = "./data/raw/testdata.manual.2009.06.14.csv";
    public String rawDataTrain = "./data/raw/training.1600000.processed.noemoticon.csv";

    //files in arff format with test and train data
    public String testData = "./data/test-data.arff";
    public String trainData = "./data/train-data.arff";

    public String preprocessedDataTest = "./data/preprocessed-test-data.arff";
    public String preprocessedDataTrain = "./data/preprocessed-train-data.arff";

    public String extractedFeaturesTest = "./data/extracted-features-test.arff";
    public String extractedFeaturesTrain = "./data/extracted-features-train.arff";

    /** size of training data for positive and negative class */
    public long trainSize = 10000;

}
