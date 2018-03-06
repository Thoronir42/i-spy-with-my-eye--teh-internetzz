package cz.zcu.sdutends.kiwi;

import cz.zcu.kiv.nlp.tools.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class IrJob implements Runnable {
    private static Logger log = Logger.getLogger(IrJob.class);

    protected final RecordIO io;

    public IrJob() {
        this.io = new RecordIO();
    }

    protected boolean ensureDirectoriesExist(String... dirs) {
        for (String dir : dirs) {
            if (!Utils.ensureDirectoryExists(dir)) {
                return false;
            }
        }

        return true;
    }

    public Collection<String> loadUrls(String path) {
        try {
            List<String> strings = Utils.readLines(new File(path));
            log.info("Loaded " + strings.size() + " urls");
            return strings;
        } catch (IOException e) {
            log.warn("Url loading failed: " + e.toString());
            return null;
        }
    }
}
