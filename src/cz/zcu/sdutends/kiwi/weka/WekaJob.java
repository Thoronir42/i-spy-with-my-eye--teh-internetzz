package cz.zcu.sdutends.kiwi.weka;

import cz.zcu.kiv.nlp.ir.weka.filters.FilterBuilder;
import cz.zcu.kiv.nlp.ir.weka.filters.TweetPreprocessBatchFilter;
import cz.zcu.kiv.nlp.ir.weka.utils.IOUtils;
import cz.zcu.kiv.nlp.ir.weka.utils.Utils;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.utils.TimeMeasure;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cz.zcu.kiv.nlp.ir.weka.utils.IOUtils.loadInstancesFromArff;
import static cz.zcu.kiv.nlp.ir.weka.utils.IOUtils.saveAsArffFile;

public class WekaJob extends IrJob {

    private final WekaJobSettings settings;

    public WekaJob(String... args) {
        this.settings = new WekaJobSettings();
        this.settings.process(args);

        
    }

    @Override
    protected boolean execute() throws Exception {
        //load raw data and create arff file, can be run only once
        createArff();

        //preprocess Tweets and save
        preprocessTweets();

        System.out.println("Train data size:" + settings.trainData);
        MultiFilter usedFilters;

        //extract features
        FilterBuilder.FilterBuilderParams params =
                new FilterBuilder.FilterBuilderParams(false, false, false, settings.wordEmbeddingsPath);
        usedFilters = extractFeatures(params);

        //train and evaluate
        Classifier classifier = trainAndEvaluate();

        //using classifier
        String tweet = "@tomas www.seznam.cz Hope you have the most amazing day and a great year ahead... Looking forward to watch #Rangasthalam Good luck :)";
        classifyTweet(classifier, usedFilters, tweet);
        tweet = "taehee taking pictures while sihyun piggybacking her :( i hate how they're so cute please :(";
        classifyTweet(classifier, usedFilters, tweet);

        return true;
    }

    /**
     * Classifies given tweet with given classifier
     */
    private static void classifyTweet(Classifier classifier, MultiFilter multiFilter, String tweet) throws Exception {
        //create instance for tweet
        List<String[]> tweets = new ArrayList<>();
        tweets.add(new String[]{tweet, ""});
        //create instance from given tweet
        Instances twInst = Utils.createInstances(tweets, true);
        twInst.setClassIndex(1);


        //we need add prepreocess filter
        Filter preprocess = FilterBuilder.createPreprocessFilter(twInst);
        twInst = Filter.useFilter(twInst, preprocess);
        //saveAsArffFile("./data/classified.arff",twInst,false);

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(multiFilter);
        fc.setClassifier(classifier);

        double pred = fc.classifyInstance(twInst.instance(0));
        System.out.println("Tweet:" + tweet);
        System.out.println("Predicted:" + twInst.classAttribute().value((int) pred));
        System.out.println("------------");
    }

    /**
     * Trains Classifier on training data and run evaluation on testing data
     *
     * @return used Classifier
     */
    private Classifier trainAndEvaluate() throws Exception {
        //load extracted features
        Instances trainDataFeatures = loadInstancesFromArff(settings.extractedFeaturesTrain);
        Instances testDataFeatures = loadInstancesFromArff(settings.extractedFeaturesTest);
        trainDataFeatures.setClassIndex(0);
        testDataFeatures.setClassIndex(0);
        Classifier model;

        //train naive bayes model
        model = new NaiveBayes();
        runModel(model, trainDataFeatures, testDataFeatures);

        return model;
    }

    private void runModel(Classifier model, Instances trainDataFeatures, Instances testDataFeatures) throws Exception {
        TimeMeasure tm = new TimeMeasure();

        log.info("Training model:" + model.getClass().getName());
        model.buildClassifier(trainDataFeatures);
        log.info("Model trained in: " + tm.duration() + "ms");

        Evaluation evaluation = new Evaluation(trainDataFeatures);
        evaluation.evaluateModel(model, testDataFeatures);
        String strSummary = evaluation.toSummaryString();
        log.info(strSummary);
        log.info("---------------");

        strSummary = evaluation.toClassDetailsString();
        log.info(strSummary);

        log.info(evaluation.toMatrixString());
        //double[][] confusionMatrix = evaluation.confusionMatrix();

        log.info("---------------------------" + model.getClass().getName() + "--------------------------------------");
    }

    /**
     * Extracts features and return used MultiFilter
     */
    private MultiFilter extractFeatures(FilterBuilder.FilterBuilderParams builderParams) throws Exception {
        log.info("Extracting features");
//        Instances trainData = loadInstancesFromArff(settings.trainData);
//        Instances testData = loadInstancesFromArff(settings.testData);
        Instances trainData = loadInstancesFromArff(settings.preprocessedDataTrain);
        Instances testData = loadInstancesFromArff(settings.preprocessedDataTest);

        //
        //
        //
        // important to set class index
        trainData.setClassIndex(1);
        testData.setClassIndex(1);


        log.info("  Extracting features");
        TimeMeasure tm = new TimeMeasure();

        //create filters
        Filter[] filters = FilterBuilder.createFilters(builderParams);

        //apply filters
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(filters);
        multiFilter.setInputFormat(trainData);

        Instances filTrainData = Filter.useFilter(trainData, multiFilter);
        Instances filTestData = Filter.useFilter(testData, multiFilter);

        //save extracted features
        saveAsArffFile(settings.extractedFeaturesTest, filTestData, false);
        saveAsArffFile(settings.extractedFeaturesTrain, filTrainData, false);

        log.info("  Features extracted and saved in: " + tm.duration() + "ms");

        //return used filters
        return multiFilter;
    }


    /**
     * Preprocess training and testing data
     */
    private void preprocessTweets() throws Exception {
        //load arff files with data
        Instances trainData = loadInstancesFromArff(settings.trainData);
        Instances testData = loadInstancesFromArff(settings.testData);

        //preprocess data
        TweetPreprocessBatchFilter tweetFilter = FilterBuilder.createPreprocessFilter(trainData);

        trainData = Filter.useFilter(trainData, tweetFilter);
        testData = Filter.useFilter(testData, tweetFilter);

        //save preprocessed data
        saveAsArffFile(settings.preprocessedDataTest, testData, false);
        saveAsArffFile(settings.preprocessedDataTrain, trainData, false);

        log.info("Data preprocessed and saved");
    }

    /**
     * Create arff files from raw testing and training data
     */
    private void createArff() throws IOException {
        log.info("Creating ARFF...");
        //load raw data
        log.debug("  Loading raw data");

        TimeMeasure tm = new TimeMeasure();

        List<String> trainData = IOUtils.loadAllLines(settings.rawDataTrain);
        List<String> testData = IOUtils.loadAllLines(settings.rawDataTest);
        log.info("  Raw data loaded in: " + tm.duration() + "ms");


        //could be done online
        log.debug("  Extracting tweets");

        tm.reset();
        List<String[]> testTweets = Utils.extractTweet(testData, settings.trainSize);
        List<String[]> trainTweets = Utils.extractTweet(trainData, settings.trainSize);
        log.info("  Tweets extracted in: " + tm.duration() + "ms");

        log.debug("  Creating instances");

        tm.reset();
        Instances testInstances = Utils.createInstances(testTweets, false);
        Instances trainInstances = Utils.createInstances(trainTweets, false);
        log.info("  Instances created  in: " + tm.duration() + "ms");

        log.debug("  Saving instances");

        tm.reset();
        saveAsArffFile(settings.testData, testInstances, false);
        saveAsArffFile(settings.trainData, trainInstances, false);
        log.info("  Instances saved  in: " + tm.duration() + "ms");

        log.info("ARFF created");
    }

}
