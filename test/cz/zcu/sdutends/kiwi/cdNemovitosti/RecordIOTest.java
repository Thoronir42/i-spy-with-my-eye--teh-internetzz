package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.sdutends.kiwi.RecordIO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecordIOTest {

    @Test
    public void saveLoad() {
        RecordIO io = new RecordIO();

        List<Estate> estates = Arrays.asList(
                new Estate().setCity("Nyrany").setCanalization("Maybe"),
                new Estate().setRegion("That one").setWater("Some"));

        io.save("./test/recordTest.txt", estates);

        Collection<Estate> loaded = io.loadCollection("./test/recordTest.txt", Estate.class);

        Assert.assertEquals("Loaded estates count ",2, loaded.size());
    }
}
