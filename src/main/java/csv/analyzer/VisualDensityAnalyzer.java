package csv.analyzer;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch;
import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

/**
 * For each insertion operation, let's say it has L lines of change.
 * Then calculate how dense the inserted text is. In other words,
 *
 *   text.length / (L x 80)
 *
 * Dump the maximum number over all insertions in all files.
 */
public class VisualDensityAnalyzer extends Analyzer {
  final static int MAX_COLUMN_COUNT = 80;

  Map<Integer, Integer> changeIdToMaxDensity =
      new TreeMap<Integer, Integer>();

  @Override
  public void _beforeAnalyzeChange(int changeId) {
  }

  @Override
  public void _analyzeFile(int changeId, File origin, File proposed,
      TextFileCache textFileCache) {
    int maxRatio = 0;
    for (Diff diff : textFileCache.getDiff(origin, proposed)) {
      if (diff.operation.equals(Operation.INSERT)) {
        int numberOfLines = countLines(diff.text);
        int ratio = 100;
        // Remove new line chars from length.
        int nonWhiteSpaceTextLength = diff.text.length() - numberOfLines + 1;
        // The code may not follow a rule restricting the column size as we
        // expect.
        if (nonWhiteSpaceTextLength < numberOfLines * MAX_COLUMN_COUNT) {
          ratio =
              (100 * nonWhiteSpaceTextLength) / (numberOfLines * MAX_COLUMN_COUNT);
        }
        if (ratio > maxRatio) {
          maxRatio = ratio;
        }
      }
    }

    if (changeIdToMaxDensity.containsKey(changeId)) {
      int changeIdMaxRatio = changeIdToMaxDensity.get(changeId);
      if (maxRatio > changeIdMaxRatio) {
        changeIdToMaxDensity.put(changeId, maxRatio);
      }
    } else {
      changeIdToMaxDensity.put(changeId, maxRatio);
    }
  }

  @Override
  public void _afterAnalyzeChange(int changeId) {}

  @Override
  public AnalyzerDumper getDumper() {
    return new AnalyzerDumper() {
      
      public String getColumns() {
          return "VisualDensity";
      }
      
      public void appendDataForChange(int id, StringBuffer data) {
          data.append(",").append(changeIdToMaxDensity.get(id));
      }
    };
  }

  private int countLines(String text) {
    int counter = 1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '\n') {
        counter++;
      }
    }
    return counter;
  }
}
