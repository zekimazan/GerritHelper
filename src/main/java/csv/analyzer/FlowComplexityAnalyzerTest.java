package csv.analyzer;

import java.io.File;

import junit.framework.TestCase;

public class FlowComplexityAnalyzerTest extends TestCase {
	FlowComplexityAnalyzer analyzer = new FlowComplexityAnalyzer();
	final int CHANGE_ID = 1, CHANGE_ID_2 = 2;

	public void testGetColumns() {
		assertEquals("FlowComplexity", analyzer.getDumper().getColumns());
	}

	public void testSingleInsertion() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(origin, "did not change\n.did not change.");
		cache.textFileContentCache.put(proposed,
				"did not change.\n" +
				" return if else case default for while break; continue; "
						+ "&& || ? catch finally throw\n" +
				"did not change.");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",15", data.toString());
	}

	public void testMultipleInsertions() {
		TextFileCache cache = new TextFileCache();
		File origin = new File("origin");
		File proposed = new File("proposed");
		cache.textFileContentCache.put(origin, "did not change\n.did not change.");
		cache.textFileContentCache.put(proposed,
				"did not change.\n" +
				" return if\n" +
				"did not change.\n" +
				" return if else");

		analyzer._beforeAnalyzeChange(CHANGE_ID);
		analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
		analyzer._afterAnalyzeChange(CHANGE_ID);

		StringBuffer data = new StringBuffer();
		analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
		assertEquals(",3", data.toString());
	}
}
