package csv.analyzer;

import java.io.File;

import junit.framework.TestCase;

public class IndendationVarianceAnalyzerTest extends TestCase {
	IndendationVarianceAnalyzer analyzer = new IndendationVarianceAnalyzer();
	final int CHANGE_ID = 1, CHANGE_ID_2 = 2;

	public void testGetColumns() {
		assertEquals(
				"IndentationBucketCount", analyzer.getDumper().getColumns());
	}

	public void testSingleInsertion() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(origin, "did not change.did not change.");
		cache.textFileContentCache.put(proposed,
				"did not change." +
				"    4 spaces indented\n" +
				"      6 spaces indented\n" +
				"0 spaces indented\n" +
				"\t\t 2 tabs and 1 space indented\n" +
				"did not change.");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",4,6", data.toString());  // Buckets are: 4, 6, 0, 3
	}

	public void testNoIndentation() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(origin, "did not change.did not change.");
		cache.textFileContentCache.put(proposed,
				"did not change." +
				"no indentation\n" +
				"no indentation\n" +
				"did not change.");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",1,0", data.toString());  // Buckets are: 0
	}

	public void testSingleLine() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(
				origin, "did not change.did not change.");
		cache.textFileContentCache.put(proposed,
				"did not change." +
				"no indentation" +
				"did not change." +
				"no indentation");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",1,0", data.toString());  // Buckets are: 0
	}

	public void testMultipleLinesAndDiffs() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(
				origin, "did not change.\ndid not change.");
		cache.textFileContentCache.put(proposed,
				"did not change.\n" +
				" 1 space indentation   \n" +
				"did not change.\n" +
				"\n\n" +
				"  2 spaces indentation");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",1,0", data.toString());  // Max bucket is: 1
	}
}
