package cz.zcu.kiv.nlp.ir.weka.filters;

import cz.zcu.kiv.nlp.ir.weka.core.TweetPreprocessing;
import weka.core.*;
import weka.filters.SimpleBatchFilter;

public class TweetPreprocessBatchFilter extends SimpleBatchFilter{

    /**
     * Index denoting tweet text, indexed from 0
     * default 0
     */
    private int textIndex = 0;

    /**
     * Class for preprocessing tweets
     */
    private TweetPreprocessing preprocess;

    public TweetPreprocessBatchFilter(TweetPreprocessing preprocess) {
        this.preprocess = preprocess;
    }

    @Override
    public String globalInfo() {
        return "Info about TweetPreprocessBatchFilter";
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        //specify new instances atribute structure - we do not add new attributes so create new from old
        return new Instances(inputFormat, inputFormat.size());
    }

    @Override
    protected Instances process(Instances instances) throws Exception {
        //new header instances for preprocessed text
        Instances result = getOutputFormat();

        //attribute with text
        Attribute attrCont = instances.attribute(this.textIndex);

        //through instances
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);

            String content = instance.stringValue(attrCont);
            content = preprocess.preprocess(content);

            //set new preprocessed text
            instance.setValue(this.textIndex, content);

            //get stored values
            double[] values = instance.toDoubleArray();

            //create new instance with new values and add it to new data
            Instance newInstance = new DenseInstance(1, values);

            // copy possible strings, relational values...
            copyValues(newInstance, false, instances, result);

            result.add(newInstance);

        }

        return result;
    }


    public int getTextIndex() {
        return textIndex;
    }

    public void setTextIndex(int textIndex) {
        this.textIndex = textIndex;
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = new Capabilities(this);
        result.disableAll();

        // attributes
        result.enableAllAttributes();
        result.enable(Capabilities.Capability.MISSING_VALUES);
        int i = 0;
        // class
        result.enableAllClasses();
        result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
        result.enable(Capabilities.Capability.NO_CLASS);
        result.setMinimumNumberInstances(0);

        return result;
    }
}
