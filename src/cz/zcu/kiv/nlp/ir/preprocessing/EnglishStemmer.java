package cz.zcu.kiv.nlp.ir.preprocessing;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class EnglishStemmer implements Stemmer {

    private final SnowballStemmer stemmer;

    public EnglishStemmer() {
        this.stemmer = new englishStemmer();
    }

    @Override
    public synchronized String apply(String value) {
        stemmer.setCurrent(value);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
