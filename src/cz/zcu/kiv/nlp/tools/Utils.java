package cz.zcu.kiv.nlp.tools;

import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Utils for I/O operations
 * Created by Tigi on 22.9.2014.
 */
public class Utils {
	private static Logger log = Logger.getLogger(Utils.class);

	public static final java.text.DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");

	/**
	 * Saves text to given file.
	 *
	 * @param file file to save
	 * @param text text to save
	 */
	public static void saveFile(File file, String text) {
		try (PrintStream printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			printStream.print(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves lines from the list into given file; each entry is saved as a new line.
	 *
	 * @param file    file to save
	 * @param results text to save
	 * @param idMap   lines
	 */
	public static void saveFile(File file, Map<String, List<String>> results, Map<String, String> idMap) {
		try (PrintStream printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			for (String key : idMap.keySet()) {
				List<String> resultList = results.get(key);
				String line = idMap.get(key);
				for (String result : resultList) {
					printStream.println(line + "\t" + result);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Saves lines from the list into given file; each entry is saved as a new line.
	 *
	 * @param file       file to save
	 * @param collection lines of text to save
	 */
	public static void saveFile(File file, Collection<String> collection) {
		try (PrintStream printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			for (String text : collection) {
				printStream.println(text);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read lines from a file; lines are trimmed and empty lines are ignored.
	 *
	 * @param f file
	 * @return list of lines
	 */
	public static List<String> readLines(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
			List<String> result = new ArrayList<>();


			String line;

			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					result.add(line.trim());
				}
			}

			return result;
		} catch (IOException e) {
			log.warn(e);
			throw e;
		}
	}

	public static boolean ensureDirectoryExists(String directory) {
		File outputDir = new File(directory);
		if (!outputDir.exists()) {
			boolean mkdirs = outputDir.mkdirs();
			if (!mkdirs) {
				log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: " + outputDir);
				return false;
			}
			log.info("Output directory created: " + outputDir);
		}

		return true;
	}
}
