package csv.analyzer;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

/**
 * Note: Indentation unit needs to be detected first. Each indentation
 * can be n spaces or tabs. Therefore, a bucket number k below refer to
 * k*n spaces.
 *
 * Computed metric 1: (max differing indentations)
 * For each line in an insertion operation, count the number of
 * indentations (spaces after previous newline and before the non white
 * space character in the line starts). Map the indentations to a bucket
 * of 1, 2, 3, 4, 4+. Calculate the number of buckets filled per insertion.
 *
 * Dump the max over all insertions in all files.
 *
 *
 * Computed metric 2: (max difference between varying indentations)
 * 
 * For buckets computed above per line, calculate the max bucket minus
 * min bucket value (which is a value in {0, 1, 2, 3, 3+}).
 *
 * Dump the max over all insertions in all files.
 *
 */
public class IndendationVarianceAnalyzer extends Analyzer {
	Map<Integer, Integer> indentationBucketCount =
			new TreeMap<Integer, Integer>();
	Map<Integer, Integer> indentationVariance =
			new TreeMap<Integer, Integer>();
	
	@Override
	public void _beforeAnalyzeChange(int changeId) {}

	@Override
	public void _analyzeFile(int changeId, File origin, File proposed,
			TextFileCache cache) {
		LinkedList<Diff> diffs = cache.getDiff(origin, proposed);
		int indentation = 0;
		Map<Integer, Integer> indentationBuckets =
				new TreeMap<Integer, Integer>();

		boolean lineBeginning;
		for (Diff diff : diffs) {
			if (diff.operation == Operation.INSERT) {
				lineBeginning = true;
				indentation = 0;
				for (int i = 0; i < diff.text.length(); i++) {
					if (lineBeginning) {
						switch(diff.text.charAt(i)) {
							case '\n':
								lineBeginning = true;
								indentation = 0;
								break;
							case ' ':
							case '\t':
								if (lineBeginning) {
									indentation++;
								}
								break;
							default:
								lineBeginning = false;
								int count = 0;
								if (indentationBuckets.containsKey(indentation)) {
									count = indentationBuckets.get(indentation);
								}
								indentationBuckets.put(indentation, count + 1);
								indentation = 0;
								break;
						}
					} else if(diff.text.charAt(i) == '\n') {
						lineBeginning = true;
					}
				}
				
				int maxBucketCount = -1;
				if (indentationBucketCount.containsKey(changeId)) {
					maxBucketCount = indentationBucketCount.get(changeId);
				}
				if (indentationBuckets.size() > maxBucketCount) {
					indentationBucketCount.put(changeId, indentationBuckets.size());
				}
				indentationBuckets.clear();
			}
		}
	}

	@Override
	public void _afterAnalyzeChange(int changeId) {}

	@Override
	public AnalyzerDumper getDumper() {
		return new AnalyzerDumper() {
			
			public String getColumns() {
				return "IndentationBucketCount";
			}
			
			public void appendDataForChange(int id, StringBuffer data) {
				data.append(",").append(indentationBucketCount.get(id));
			}
		};
	}

	@Override
	public void reset() {
		indentationBucketCount.clear();
		indentationVariance.clear();
	}
}
