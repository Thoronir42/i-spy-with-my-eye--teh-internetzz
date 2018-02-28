package cz.zcu.kiv.nlp.ir.preprocessing;

import cz.zcu.kiv.nlp.tools.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopwordsLoader {
    public static Set<String> load(String filename) {
        try {
            return new HashSet<>(Utils.readLines(new File("./data/stopwords/" + filename)));
        } catch (IOException e) {
            return null;
        }
    }
}
