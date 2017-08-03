package csv.analyzer;

import java.io.File;

/**
 * For each insertion operation, let's say it has L lines of change.
 * Then calculate how dense the inserted text is. In otherwords,
 *
 *   text.length / (L x 80)
 *
 * Dump the maximum number over all insertions in all files.
 */
public class VisualDensityAnalyzer extends Analyzer {

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
