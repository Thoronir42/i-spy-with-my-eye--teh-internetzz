package cz.zcu.kiv.nlp.ir.preprocessing;


import java.util.*;

/**
 * Created by Tigi on 29.2.2016.
 */
public class BasicPreprocessing implements PreProcessing {

    Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
    Stemmer stemmer;
    Tokenizer tokenizer;
    Set<String> stopwords;
    boolean removeAccentsBeforeStemming;
    boolean removeAccentsAfterStemming;
    boolean toLowercase;

    public BasicPreprocessing(Stemmer stemmer, Tokenizer tokenizer, Set<String> stopwords) {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.stopwords = stopwords;
    }

    public BasicPreprocessing setRemoveAccentsBeforeStemming(boolean removeAccentsBeforeStemming) {
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        return this;
    }

    public BasicPreprocessing setRemoveAccentsAfterStemming(boolean removeAccentsAfterStemming) {
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        return this;
    }

    public BasicPreprocessing setToLowercase(boolean toLowercase) {
        this.toLowercase = toLowercase;
        return this;
    }

    @Override
    public void index(String document) {
        if (toLowercase) {
            document = document.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            document = removeAccents(document);
        }
        for (String token : tokenizer.tokenize(document)) {
            if (stemmer != null) {
                token = stemmer.stem(token);
            }
            if (removeAccentsAfterStemming) {
                token = removeAccents(token);
            }
            if (!wordFrequencies.containsKey(token)) {
                wordFrequencies.put(token, 0);
            }

            wordFrequencies.put(token, wordFrequencies.get(token) + 1);
        }
    }

    @Override
    public String getProcessedForm(String text) {
        if (toLowercase) {
            text = text.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            text = removeAccents(text);
        }
        if (stemmer != null) {
            text = stemmer.stem(text);
        }
        if (removeAccentsAfterStemming) {
            text = removeAccents(text);
        }
        return text;
    }

    final String withDiacritics = "áÁčČďĎéÉěĚíÍňŇóÓřŘšŠťŤúÚůŮýÝžŽ";
    final String withoutDiacritics = "aAcCdDeEeEiInNoOrRsStTuUuUyYzZ";

    private String removeAccents(String text) {
        for (int i = 0; i < withDiacritics.length(); i++) {
            text = text.replaceAll("" + withDiacritics.charAt(i), "" + withoutDiacritics.charAt(i));
        }
        return text;
    }

    public Map<String, Integer> getWordFrequencies() {
        return wordFrequencies;
    }
}
