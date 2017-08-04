package csv.analyzer;

import java.io.File;

/**
 * In each line in each insertion operation, compute the complexity of each
 * line.
 *
 * Dump:
 *   max
 *   median
 *  
 * over all insertions per change.
 */
public class LineComplexityAnalyzer extends Analyzer {

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

	@Override
	public void reset() {}
}
