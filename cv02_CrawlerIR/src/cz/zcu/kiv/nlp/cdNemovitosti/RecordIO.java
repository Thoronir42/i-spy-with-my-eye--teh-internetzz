package cz.zcu.kiv.nlp.cdNemovitosti;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecordIO {
    private static final Logger log = Logger.getLogger(RecordIO.class);

    public void save(String path, List<Property> list) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(list);
            log.info(String.format("Serialized data is saved in %s\n", path));
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public List<Property> load(File serializedFile) {
        final Object object;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile))) {
            object = objectInputStream.readObject();
            if (!(object instanceof List)) {
                throw new RuntimeException("Deserialized object is not a list");
            }
            List deserialized = (List)object;

            List<Property> result = new ArrayList<>();
            for(Object item : deserialized) {
                if(item instanceof Property) {
                    result.add((Property) item);
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
