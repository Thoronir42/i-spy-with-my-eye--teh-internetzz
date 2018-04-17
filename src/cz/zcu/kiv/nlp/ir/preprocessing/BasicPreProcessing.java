package cz.zcu.kiv.nlp.ir.preprocessing;


import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Tigi on 29.2.2016.
 */
public class BasicPreProcessing implements PreProcessing {

    private Map<String, Integer> wordFrequencies = new HashMap<>();
    private Tokenizer tokenizer;
    private Collection<String> stopwords;

    private final List<PreProcessingOperation<String>> documentOperations;
    private final List<PreProcessingOperation<String>> tokenOperations;

    public BasicPreProcessing() {
        this(new BasicTokenizer(" "));
    }

    public BasicPreProcessing(Tokenizer tokenizer) {
        this(tokenizer, new LinkedList<>());
    }

    public BasicPreProcessing(Tokenizer tokenizer, Collection<String> stopwords) {
        this.tokenizer = tokenizer;
        this.stopwords = stopwords;

        this.documentOperations = new ArrayList<>();
        this.tokenOperations = new ArrayList<>();

    }

    public BasicPreProcessing addTokenOperation(PreProcessingOperation<String> operation) {
        this.tokenOperations.add(operation);
        return this;
    }

    public BasicPreProcessing addDocumentOperation(PreProcessingOperation<String> operation) {
        this.documentOperations.add(operation);
        return this;
    }

    public BasicPreProcessing applyDocumentOperationsOnStopWords() {
        this.stopwords = stopwords.stream().map(word -> {
            for (PreProcessingOperation<String> documentOperation : documentOperations) {
                word = documentOperation.apply(word);
            }
            return word;
        }).collect(Collectors.toList());
        return this;
    }

    public String[] tokenizeAndPreprocess(String document) {
        for (PreProcessingOperation<String> operation : this.documentOperations) {
            document = operation.apply(document);
        }

        String[] tokens = tokenizer.tokenize(document);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
//            String original = token;
            if (stopwords.contains(token)) {
//                System.out.format("~ %-14s @%d\n", token, ((List<String>) stopwords).indexOf(token));
                continue;
            }
            for (PreProcessingOperation<String> operation : this.tokenOperations) {
                token = operation.apply(token);
            }

//            System.out.format("+ %-14s %s \n", token, original);
            tokens[i] = token;
        }

        return tokens;
    }

    @Override
    public void index(String document) {
        for (String token : tokenizeAndPreprocess(document)) {
            wordFrequencies.put(token, wordFrequencies.getOrDefault(token, 0) + 1);
        }
    }

    @Override
    public int getWordCount(String word) {
        return wordFrequencies.getOrDefault(word, 0);
    }

    @Override
    public boolean contains(String word) {
        return wordFrequencies.containsKey(word);
    }

    @Override
    public String getProcessedForm(String text) {
        for (PreProcessingOperation<String> op : this.documentOperations) {
            text = op.apply(text);
        }
        for (PreProcessingOperation<String> op : this.tokenOperations) {
            text = op.apply(text);
        }

        return text;
    }

    public Map<String, Integer> getWordFrequencies() {
        return wordFrequencies;
    }

    @Override
    public String toString() {
        return "BasicPreProcessing{" +
                "\n  documentOperations=" + documentOperations.stream().map(PreProcessingOperation::toString).collect(Collectors.joining(", ")) +
                "\n  tokenOperations=" + tokenOperations.stream().map(PreProcessingOperation::toString).collect(Collectors.joining(", ")) +
                "\n}";
    }
}
