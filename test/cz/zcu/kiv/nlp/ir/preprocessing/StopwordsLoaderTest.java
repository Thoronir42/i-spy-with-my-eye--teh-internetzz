package cz.zcu.kiv.nlp.ir.preprocessing;

import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

public class StopwordsLoaderTest {

    @Test
    public void load() {
        Collection<String> stopWords = StopwordsLoader.load("cz.txt");
        String[] expectedWords = {"tímto", "ačkoli", "jestliže", "přičemž"};

        for (String word : expectedWords) {
            assertTrue(stopWords.contains(word));
        }
    }
}
