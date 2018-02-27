package cz.zcu.kiv.nlp.cdNemovitosti;

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

        Collection loaded = io.load("./test/recordTest.txt");

        Assert.assertEquals("Loaded estates count ",2, loaded.size());
    }
}
