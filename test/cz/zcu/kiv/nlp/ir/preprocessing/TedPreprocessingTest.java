package cz.zcu.kiv.nlp.ir.preprocessing;

import cz.zcu.sdutends.kiwi.ted.model.Talk;
import cz.zcu.sdutends.kiwi.ted.serdes.TalkSerDes;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TedPreprocessingTest {

    @Test
    public void testIndexAndCheck() {
        Collection<String> stopWords = StopwordsLoader.load("en.txt");

        BasicPreProcessing basicPreProcessing = new BasicPreProcessing(new AdvancedTokenizer(), stopWords)
                .addDocumentOperation(String::toLowerCase)
                .addDocumentOperation(new AccentStripper(AccentStripper.Mode.Advanced))
                .addTokenOperation(new CzechStemmerAgressive())
                .applyDocumentOperationsOnStopWords();

        AdvancedIO<Talk> taio = new AdvancedIO<>(new TalkSerDes());
        taio.loadFromDirectory("./storage/ted/talks")
                .forEach(talk -> {
                    basicPreProcessing.index(talk.getTranscript());
                });

        assertFalse(basicPreProcessing.contains("and"));

        assertTrue(basicPreProcessing.contains("01:06"));
        assertTrue(basicPreProcessing.contains("(applause)"));
    }
}
