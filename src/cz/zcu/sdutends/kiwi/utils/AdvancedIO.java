package cz.zcu.sdutends.kiwi.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AdvancedIO<T> {
    private static Logger log = Logger.getLogger(AdvancedIO.class);

    private final Sedes<T> sedes;

    public AdvancedIO(Sedes<T> sedes) {
        this.sedes = sedes;
    }

    public List<T> loadFromDirectory(String path) {
        List<T> result = new ArrayList<>();

        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files == null) {
            log.warn("Failed listing files in " + path);
            return result;
        }
        if(files.length == 0) {
            log.info("No files found in " + path);
        }

        for (File file : files) {
            try{
                String text = new String(Files.readAllBytes(file.toPath()));
                result.add(sedes.deserialize(text));
            } catch (IOException | IllegalArgumentException ex) {
                log.warn("Could not deserialize talk from file: " + file.getName());
                ex.printStackTrace();
            }
        }

        return result;
    }

    public T loadFromFile(File file) {
        try{
            String text = new String(Files.readAllBytes(file.toPath()));
            return sedes.deserialize(text);
        } catch (IOException | IllegalArgumentException ex) {
            log.warn("Could not deserialize talk from file: " + file.getName());
            ex.printStackTrace();
        }
        return null;
    }

    public boolean save(String path, T object) {
        return save(new File(path), object);
    }

    public boolean save(File file, T object) {
        try(FileWriter writer = new FileWriter(file)){
            String serialize = sedes.serialize(object);
            writer.write(serialize);

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
