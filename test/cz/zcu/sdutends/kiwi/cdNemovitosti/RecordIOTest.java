package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.sdutends.kiwi.RecordIO;
import cz.zcu.sdutends.kiwi.ted.Talk;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecordIOTest {

    @Test
    public void saveLoad() {
        RecordIO io = new RecordIO();

        List<Talk> estates = Arrays.asList(
                new Talk().setTalker("Bob").setDateRecorded("NOW"),
                new Talk().setTitle("Pizza cakes").setDateRecorded("1.2.3.4"));

        io.save("./test/recordTest.txt", estates);

        Collection<Talk> loaded = io.loadCollection("./test/recordTest.txt", Talk.class);

        Assert.assertEquals("Loaded estates count ",2, loaded.size());
    }
}
