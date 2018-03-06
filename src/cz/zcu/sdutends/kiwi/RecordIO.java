package cz.zcu.sdutends.kiwi;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecordIO {
    private static final Logger log = Logger.getLogger(RecordIO.class);

    public void save(String path, Object obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(obj);
            log.info(String.format("Serialized data is saved in %s\n", path));
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public <T> T loadItem(String path, Class<T> tClass) {
        return loadItem(new File(path), tClass);
    }

    public <T> T loadItem(File file, Class<T> tClass) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            final Object object = objectInputStream.readObject();
            if (!tClass.isInstance(object)) {
                log.warn("Deserialization contained invalid type");
                return null;
            }
            return tClass.cast(object);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public <T> List<T> loadCollection(String serializedFilePath, Class<T> tClass) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFilePath))) {
            final Object object = objectInputStream.readObject();
            if (!(object instanceof Collection)) {
                throw new RuntimeException("Deserialized object is not a collection");
            }
            Collection deserialized = (Collection) object;

            List<T> result = new ArrayList<>();
            for (Object item : deserialized) {
                if (!tClass.isInstance(item)) {
                    log.warn("Deserialization contained invalid type");
                    continue;
                }
                result.add(tClass.cast(item));
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
