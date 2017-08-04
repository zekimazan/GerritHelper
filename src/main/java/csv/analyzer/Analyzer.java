package csv.analyzer;

import java.io.File;
import java.util.Map;

public abstract class Analyzer {
	protected int mTotalFileCount = 0;
	Map<File, String> fileContentCache;

	public final void analyzeFile(
			int changeId, File origin, File proposed,
			TextFileCache textFileContentCache) {
		mTotalFileCount++;
		_analyzeFile(changeId, origin, proposed, textFileContentCache);
	}
	
	public final void beforeAnalyzeChange(int changeId) {
		reset();
		mTotalFileCount = 0;
		_beforeAnalyzeChange(changeId);
	}

	public final void afterAnalyzeChange(int changeId) {
		_afterAnalyzeChange(changeId);
	}

	public abstract void _beforeAnalyzeChange(int changeId);
	public abstract void _analyzeFile(
			int changeId, File origin, File proposed,
			TextFileCache textFileContentCache);
	public abstract void _afterAnalyzeChange(int changeId);
	public abstract AnalyzerDumper getDumper();
	public abstract void reset();
}