package cz.zcu.kiv.nlp.ir.preprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

public class StopwordsLoader {
    public static Collection<String> load(String filename) {
        try {
            return Files.readAllLines(new File("./data/stopwords/" + filename).toPath());
//            return new HashSet<>(Utils.readLines(new File("./data/stopwords/" + filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}
