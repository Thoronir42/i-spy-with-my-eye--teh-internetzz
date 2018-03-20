package cz.zcu.sdutends.kiwi.ted.serdes;

import cz.zcu.sdutends.kiwi.ted.model.TalkStructured;
import cz.zcu.sdutends.kiwi.ted.model.TranscriptBlock;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class TalkStructuredSerDesTest {

    @Test
    public void deserialize() throws IOException {
        TalkStructuredSerDes sedes = new TalkStructuredSerDes();

        Path path = new File("./storage/ted/talks/adam_alter_why_our_screens_make_us_less_happy.txt").toPath();
        String text = new String(Files.readAllBytes(path));

        TalkStructured talk = sedes.deserialize(text);

        assertEquals("Adam Alter", talk.getTalker());
        assertEquals(4, talk.getDateRecorded().getMonthValue());
        assertEquals(2017, talk.getDateRecorded().getYear());
        assertEquals("Why our screens make us less happy", talk.getTitle());

        TranscriptBlock[] transcript = talk.getTranscript();

        assertEquals(27, transcript.length);
        assertEquals(48, transcript[1].getTime());
        assertEquals(87, transcript[2].getTime());
    }
}
