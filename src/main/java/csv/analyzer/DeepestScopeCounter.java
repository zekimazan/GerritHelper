package csv.analyzer;

import java.io.File;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import csv.analyzer.thirdparty.diff_match_patch.Diff;
import csv.analyzer.thirdparty.diff_match_patch.Operation;

/**
 * Find the max nesting level per insertion operation.
 * Dump the max over all the insertions in all files per change.
 */
public class DeepestScopeCounter extends Analyzer {
  Map<Integer, Integer> changeIdToDeepestScope =
      new TreeMap<Integer, Integer>();

  @Override
  public void _beforeAnalyzeChange(int changeId) {}

  @Override
  public void _analyzeFile(int changeId, File origin, File proposed,
      TextFileCache cache) {
    int maxSoFar = 0;
    for (Diff diff : cache.getDiff(origin, proposed)) {
      if (diff.operation.equals(Operation.INSERT)) {
        int deepestScope = findDeepestScope(diff.text) + 1;
        if (deepestScope > maxSoFar) {
          maxSoFar = deepestScope;
        }
      }
    }
    if (changeIdToDeepestScope.containsKey(changeId)) {
      int changeIdDeepestScope = changeIdToDeepestScope.get(changeId);
      if (maxSoFar > changeIdDeepestScope) {
        changeIdToDeepestScope.put(changeId, maxSoFar);
      }
    } else {
      changeIdToDeepestScope.put(changeId, maxSoFar);
    }
  }

  @Override
  public void _afterAnalyzeChange(int changeId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public AnalyzerDumper getDumper() {
    return new AnalyzerDumper() {
      public String getColumns() {
          return "DeepestScope";
      }
      
      public void appendDataForChange(int id, StringBuffer data) {
          data.append(",").append(changeIdToDeepestScope.get(id));
      }
    };
  }

  private int findDeepestScope(String text) {
    Stack<Boolean> stack = new Stack<Boolean>();
    int counter = 0, max = 0;
    for (int i = 0; i < text.length(); i++) {
      switch(text.charAt(i)) {
        case '{':
          counter++;
          stack.push(true);
          break;
        case '}':
          if (!stack.isEmpty()) {
            stack.pop();
            if (counter > max) {
              max = counter;
            }
            counter--;
          }
          break;
      }
    }
    // Consider any unclosed scope since we are dealing with partial diffs.
    if (stack.size() > max) {
      max = stack.size();
    }
    return max;
  }
}
