package csv.analyzer;
import java.io.File;

/**
 * Compute the flow complexity per insertion operation.
 * Dump the max over all the insertions in all files per change.
 * 
 * For computing flow complexity see:
 * https://www.leepoint.net/principles_and_practices/complexity/complexity-java-method.html
 */
public class FlowComplexityAnalyzer extends Analyzer {

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
