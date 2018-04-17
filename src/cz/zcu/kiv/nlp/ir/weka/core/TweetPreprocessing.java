package cz.zcu.kiv.nlp.ir.weka.core;

import cz.zcu.kiv.nlp.ir.weka.tokenizers.TweetNLPTokenizer;
import weka.core.stemmers.NullStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.Null;
import weka.core.stopwords.StopwordsHandler;
import weka.core.tokenizers.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class performing preprocessing of tweets
 */
public class TweetPreprocessing {


    /**
     * Convert tweet text to lower case
     */
    private boolean toLowerCase = true;

    /**
     * Replacing sequences of letters occurring more than twotimes
     * in a row with two occurrences of them (e.g., huuungry is reduced to huungry, loooove to loove)
     */
    private boolean reduceRepeatedLetters = true;

    /**
     * Convert URLs and usernames to common token
     */
    private boolean standarizeUrlsUsers = true;

    /**
     * Tokenizer
     */
    private Tokenizer tokenizer = new TweetNLPTokenizer();

    /**
     * Stemmer
     */
    private Stemmer stemmer = new NullStemmer();

    /**
     * Stopwords
     */
    private StopwordsHandler stop = new Null();

    /**
     * Create list of tokens from given string
     */
    public List<String> tokenize(String content) {
        List<String> tokens = new ArrayList<>();
        tokenizer.tokenize(content);
        while(tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextElement());
        }

        return tokens;
    }

    /**
     * Preprocess content, depending on how the flags are set
     *
     * content is tokenized with tokenizer and then all tokens are joined with space
     */
    public String preprocess(String content) {
        if (toLowerCase) {
            content = content.toLowerCase();
        }

        //remove letters repeated more than two times, for example huuuungry to huungry
        if (reduceRepeatedLetters) {
            //TODO
           // content =
        }

        //list of tokens
        List<String> tokens = new ArrayList<>();
        tokenizer.tokenize(content);

        //tokenize with our tokenizer, replace urls with token http://www.url.com, and all usernames with @user
        //use stemmer on tokens and remove stop words
        while(tokenizer.hasMoreElements()) {
            //TODO
        }

        content = String.join(" ", tokens);
        return content;

    }
}
