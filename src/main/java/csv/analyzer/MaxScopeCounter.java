package csv.analyzer;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

/**
 * Count the number of scopes (essentially number of open curly braces)
 * in each insertion operation. Dump the  max over all the insertions in all
 * files per change. Currently it approximates it. Ideally an AST should be
 * built for this to compute the number accurately. 
 */
public class MaxScopeCounter extends Analyzer {
  Map<Integer, Integer> changeIdToMaxScope =
      new TreeMap<Integer, Integer>();

  @Override
  public void _beforeAnalyzeChange(int changeId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void _analyzeFile(int changeId, File origin, File proposed,
      TextFileCache cache) {
    int maxNumberOfScopes = 0;
    for (Diff diff : cache.getDiff(origin, proposed)) {
      if (diff.operation.equals(Operation.INSERT)) {
        int numberOfScopes = countOpenBraces(diff.text) + 1;
        if (numberOfScopes > maxNumberOfScopes) {
          maxNumberOfScopes = numberOfScopes;
        }
      }
    }
    if (changeIdToMaxScope.containsKey(changeId)) {
      int changeIdMaxScope = changeIdToMaxScope.get(changeId);
      if (maxNumberOfScopes > changeIdMaxScope) {
        changeIdToMaxScope.put(changeId, maxNumberOfScopes);
      }
    } else {
      changeIdToMaxScope.put(changeId, maxNumberOfScopes);
    }
  }

  private int countOpenBraces(String text) {
    int counter = 0;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '{') {
        counter++;
      }
    }
    return counter;
  }

  @Override
  public void _afterAnalyzeChange(int changeId) {}

  @Override
  public AnalyzerDumper getDumper() {
    return new AnalyzerDumper() {
      
      public String getColumns() {
          return "MaxScope";
      }
      
      public void appendDataForChange(int id, StringBuffer data) {
        data.append(",").append(changeIdToMaxScope.get(id));
      }
    };
  }

}
