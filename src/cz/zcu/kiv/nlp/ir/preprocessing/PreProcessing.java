package cz.zcu.kiv.nlp.ir.preprocessing;

import java.util.Map;

/**
 * Created by tigi on 29.2.2016.
 */
public interface PreProcessing {
    void index(String document);
    String getProcessedForm(String text);

    int getWordCount(String word);
    boolean contains(String word);

    Map<String, Integer> getWordFrequencies();
}
