package csv.analyzer;

import java.io.File;

/**
 * Find the max nesting level per insertion operation.
 * Dump the max over all the insertions in all files per change.
 */
public class DeepestScopeCounter extends Analyzer {

  @Override
  public void _beforeAnalyzeChange(int changeId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void _analyzeFile(int changeId, File origin, File proposed,
      TextFileCache textFileContentCache) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void _afterAnalyzeChange(int changeId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public AnalyzerDumper getDumper() {
    // TODO Auto-generated method stub
    return null;
  }

}
