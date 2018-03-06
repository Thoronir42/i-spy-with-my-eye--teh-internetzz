package cz.zcu.sdutends.kiwi.utils;

import cz.zcu.sdutends.kiwi.RecordIO;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvancedIO<T> {
    private static Logger log = Logger.getLogger(AdvancedIO.class);
    private final RecordIO recordIO;

    private final Class<T> tClass;

    public AdvancedIO(Class<T> tClass) {
        this.tClass = tClass;
        this.recordIO = new RecordIO();
    }

    public List<T> loadFromDirectory(String path) {
        List<T> result = new ArrayList<>();

        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files == null) {
            return result;
        }
        for (File file : files) {
            T item = recordIO.loadItem(file, tClass);
            if(!tClass.isInstance(item)) {
                log.warn("File " + file.getName() + " did not contain " + tClass.getName() + "!");
                continue;
            }
            result.add(item);
        }

        return result;
    }

}
