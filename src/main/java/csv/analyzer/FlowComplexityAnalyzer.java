package csv.analyzer;
import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

/**
 * Compute the flow complexity per insertion operation.
 * Dump the max over all the insertions in all files per change.
 * 
 * For computing flow complexity see:
 * https://www.leepoint.net/principles_and_practices/complexity/complexity-java-method.html
 */
public class FlowComplexityAnalyzer extends Analyzer {
	Map<Integer, Integer> changeIdToFlowComplexity =
			new TreeMap<Integer, Integer>();
	String[] branchKeywords = {
			" return ", " if", " else", " case", "default",
			" for", " while", " break;", " continue;",
			"&&", "||", "?", " catch", " finally", " throw"};

	@Override
	public void _beforeAnalyzeChange(int changeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void _analyzeFile(int changeId, File origin, File proposed,
			TextFileCache cache) {
		LinkedList<Diff> diffs = cache.getDiff(origin, proposed);
		int maxFlowComplexity = 0;
		for (Diff diff : diffs) {
			if (diff.operation == Operation.INSERT) {
				int complexity = countFlowComplexity(diff.text);
				if (complexity > maxFlowComplexity) {
					maxFlowComplexity = complexity;
				}
			}
		}

		int currentMax = -1;
		if (changeIdToFlowComplexity.containsKey(changeId)) {
			currentMax = changeIdToFlowComplexity.get(changeId);
		}
		if (maxFlowComplexity > currentMax) {
			changeIdToFlowComplexity.put(changeId, maxFlowComplexity);
		}
	}

	private int countFlowComplexity(String text) {
		int counter = 0;
		for (String keyword : branchKeywords) {
			counter += StringUtils.countMatches(text, keyword);
		}
		return counter;
	}

	@Override
	public void _afterAnalyzeChange(int changeId) {}

	@Override
	public AnalyzerDumper getDumper() {
		return new AnalyzerDumper() {
			
			public String getColumns() {
				return "FlowComplexity";
			}

			public void appendDataForChange(int id, StringBuffer data) {
				data.append(",").append(changeIdToFlowComplexity.get(id));
			}
		};
	}

	@Override
	public void reset() {}
}
