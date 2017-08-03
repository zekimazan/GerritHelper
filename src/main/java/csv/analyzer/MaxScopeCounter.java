package csv.analyzer;

import java.io.File;

/**
 * Count the number of scopes (essentially number of open curly braces)
 * in each insertion operation. Dump the  max over all the insertions in all
 * files per change.
 */
public class MaxScopeCounter extends Analyzer {

  @Override
  public void _beforeAnalyzeChange(int changeId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void _analyzeFile(int changeId, File origin, File proposed,
      TextFileContentCache textFileContentCache) {
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
