package cz.zcu.sdutends.kiwi;

import cz.zcu.kiv.nlp.tools.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class IrJob implements Runnable {
    private static Logger log = Logger.getLogger(IrJob.class);

    protected final RecordIO io;

    public IrJob() {
        this.io = new RecordIO();
    }

    protected void runProgress(Collection<String> urls, Consumer<String> urlAction) {
        int count = 0;
        int progressNotch = urls.size() * 5 / 100;
        if(progressNotch < 5) {
            progressNotch = urls.size();
        }
        for (String url : urls) {
            urlAction.accept(url);

            if (++count % progressNotch == 0) {
                log.info(count + " / " + urls.size() + " = " + count *100.0f / urls.size() + "% done.");
            }
        }
    }

    protected <T> Collection<T> runProgress(Collection<String> urls, Function<String, T> urlAction) {
        Collection<T> items = new LinkedList<>();
        int count = 0;
        int progressNotch = urls.size() * 5 / 100;
        if(progressNotch < 5) {
            progressNotch = urls.size();
        }
        for (String url : urls) {
            items.add(urlAction.apply(url));

            if (++count % progressNotch == 0) {
                log.info(String.format("%d / %d = %5.2f %% done.", count, urls.size(), 100.0 * count / urls.size()));
            }
        }

        return items;
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
