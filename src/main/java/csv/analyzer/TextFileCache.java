package csv.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch;
import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.LinesToCharsResult;

public class TextFileCache {
	protected Map<File, String> textFileContentCache =
			new TreeMap<File, String>();
	// Origin file to diff between origin and proposed (assumes there is only
	// one proposed)
	protected Map<File, LinkedList<Diff>> textFileDiffCache =
			new TreeMap<File, LinkedList<Diff>>();

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

	public LinkedList<Diff> getDiff(File origin, File proposed) {
		if (textFileDiffCache.containsKey(origin)) {
			return textFileDiffCache.get(origin);
		}

		diff_match_patch diffUtil = new diff_match_patch();
		String originText = readFile(origin);
		String proposedText = readFile(proposed);

		long deadline = System.currentTimeMillis() + (long) (1.0f * 1000);
		LinkedList<Diff> diffs = diffUtil.diff_lineMode(
				originText, proposedText, deadline);
		textFileDiffCache.put(origin, diffs);
		return diffs;
	}

	public void clear() {
		textFileContentCache.clear();
		textFileDiffCache.clear();
	}
}
