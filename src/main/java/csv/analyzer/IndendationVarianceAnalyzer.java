package csv.analyzer;

import java.io.File;

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
