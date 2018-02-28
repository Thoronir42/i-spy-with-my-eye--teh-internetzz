package cz.zcu.kiv.nlp.ir.preprocessing;

import cz.zcu.kiv.nlp.tools.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class StopwordsLoader {
    public static Collection<String> load(String filename) {
        try {
            return Utils.readLines(new File("./data/stopwords/" + filename));
//            return new HashSet<>(Utils.readLines(new File("./data/stopwords/" + filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}
