package csv.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class TextFileContentCache {
	Map<File, String> textFileContentCache =
			new TreeMap<File, String>();
	
	public String readFile(File file) {
		String content = textFileContentCache.get(file);
		if (content != null) {
			return content;
		}

		// TODO(cgerede): Reading the all content is not memory efficient.
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			content = scanner.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}

		textFileContentCache.put(file, content);
		return content;
	}


	public void clear() {
		textFileContentCache.clear();
	}
}
