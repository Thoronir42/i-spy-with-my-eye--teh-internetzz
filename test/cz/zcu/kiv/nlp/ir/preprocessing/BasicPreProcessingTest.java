package cz.zcu.kiv.nlp.ir.preprocessing;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class BasicPreProcessingTest {

    @Test
    public void indexSkipStopWords() {
        List<String> stopWords = Arrays.asList("v", "kdyby");
        BasicPreProcessing processing = new BasicPreProcessing(new AdvancedTokenizer(), stopWords);
        processing.addDocumentOperation(String::toLowerCase);

        processing.index("Kdyby nebyly v rybniku ryby");

        Map<String, Integer> wordFrequencies = processing.getWordFrequencies();

//        System.out.println(Arrays.toString(wordFrequencies.keySet().toArray()));

        assertEquals(3, wordFrequencies.size());
    }

}
