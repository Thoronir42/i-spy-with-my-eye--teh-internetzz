package cz.zcu.sdutends.kiwi;

import org.apache.log4j.Logger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class IrJob implements Runnable {
    protected static Logger log = Logger.getLogger(IrJob.class);

    private static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");

    protected boolean ensureDirectoriesExist(String... dirs) {
        for (String dir : dirs) {
            File outputDir = new File(dir);
            if (!outputDir.exists()) {
                boolean mkdirs = outputDir.mkdirs();
                if (!mkdirs) {
                    log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: " + outputDir);
                    return false;
                }
                log.info("Output directory created: " + outputDir);
            }

        }

        return true;
    }

    public String time() {
        return SDF.format(System.currentTimeMillis());
    }
}
