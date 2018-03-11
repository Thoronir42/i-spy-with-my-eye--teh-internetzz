package cz.zcu.sdutends.kiwi.ir;

import cz.zcu.sdutends.kiwi.IrJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public abstract class CrawlJob extends IrJob {

    public Collection<String> loadUrls(String path) {
        try {
            List<String> strings = Files.readAllLines(new File(path).toPath());
            log.info("Loaded " + strings.size() + " urls");
            return strings;
        } catch (IOException e) {
            log.warn("Url loading failed: " + e.toString());
            return null;
        }
    }

    protected void saveUrls(String fileName, Collection<String> urls) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(fileName))) {
            for (String url : urls) {
                ps.println(url);
            }
        } catch (IOException ex) {
            log.error(ex.toString());
        }
    }
}
