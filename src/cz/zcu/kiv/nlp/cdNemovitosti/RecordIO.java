package cz.zcu.kiv.nlp.cdNemovitosti;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecordIO {
    private static final Logger log = Logger.getLogger(RecordIO.class);

    public void save(String path, Collection<Estate> list) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(list);
            log.info(String.format("Serialized data is saved in %s\n", path));
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Collection<Estate> load(String serializedFilePath) {
        final Object object;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFilePath))) {
            object = objectInputStream.readObject();
            if (!(object instanceof Collection)) {
                throw new RuntimeException("Deserialized object is not a collection");
            }
            Collection deserialized = (Collection)object;

            List<Estate> result = new ArrayList<>();
            for(Object item : deserialized) {
                if(item instanceof Estate) {
                    result.add((Estate) item);
                    continue;
                }
                log.warn("Deserialization contained invalid type");
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
