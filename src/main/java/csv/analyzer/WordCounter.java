package csv.analyzer;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch;
import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

public class WordCounter extends Analyzer {
	Map<Integer, Integer> changeIdToWordCount =
			new TreeMap<Integer, Integer>();

	@Override
	public void _beforeAnalyzeChange(int changeId) {}

	@Override
	public void _analyzeFile(
			int changeId, File origin, File proposed,
			TextFileContentCache textFileContentCache) {
		diff_match_patch dmp = new diff_match_patch();

		// TODO(cgerede): Reading the all content is not memory efficient.
		String originContent = textFileContentCache.readFile(origin);
		String proposedContent = textFileContentCache.readFile(proposed);

		LinkedList<Diff> diffs =
				dmp.diff_main(originContent, proposedContent);

		int wordCount = 0;
		for (Diff diff : diffs) {
			if (diff.operation.equals(Operation.EQUAL)) {
				continue;
			}

			wordCount += diff.text.trim().split("\\s+").length;
		}

		changeIdToWordCount.put(changeId, wordCount);
	}

	@Override
	public void _afterAnalyzeChange(int changeId) {}

	@Override
	public AnalyzerDumper getDumper() {
		return new AnalyzerDumper() {
			
			public String getColumns() {
				return "WordCount";
			}
			
			public void appendDataForChange(int id, StringBuffer data) {
				data.append(",").append(changeIdToWordCount.get(id));
			}
		};
	}

}
