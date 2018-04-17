package cz.zcu.sdutends.kiwi.crosslining;

import cz.zcu.kiv.nlp.ir.preprocessing.*;

import java.util.HashMap;
import java.util.Map;

public class PreProcessingFactory {

    private Map<String, PreProcessing> preProcessingCache;

    PreProcessingFactory() {
        this.preProcessingCache = new HashMap<>();
    }

    public PreProcessing get(String language) {
        if (!preProcessingCache.containsKey(language)) {
            PreProcessing preProcessing = new BasicPreProcessing()
                    .addDocumentOperation(String::toLowerCase)
                    .addDocumentOperation(String::trim);

            Stemmer stemmer = getStemmer(language);
            if(stemmer != null) {
                ((BasicPreProcessing) preProcessing).addTokenOperation(stemmer);
            }

            preProcessingCache.put(language, preProcessing);
        }


        return preProcessingCache.get(language);
    }


    private Stemmer getStemmer(String language) {
        switch (language.toLowerCase()) {
            case "cs":
                return new CzechStemmerLight();
            case "en":
                return new EnglishStemmer();
        }

        return null;
    }
}
