package cz.zcu.kiv.nlp.cdNemovitosti;

import java.io.*;
import java.util.List;

public class RecordIO {
	public void save(String path, List<Record> list) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
			out.writeObject(list);
			System.out.print("Serialized data is saved in /tmp/employee.ser\n");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public List<Record> load(File serializedFile) {
		final Object object;
		try {
			final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile));
			object = objectInputStream.readObject();
			objectInputStream.close();
			return (List<Record>) object;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
