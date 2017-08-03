package csv.analyzer;

import java.io.File;

import junit.framework.TestCase;

public class MaxScopeCounterTest extends TestCase {
  MaxScopeCounter analyzer = new MaxScopeCounter();
  final int CHANGE_ID = 1, CHANGE_ID_2 = 2;

  public void testColumnName() {
    assertEquals("MaxScope", analyzer.getDumper().getColumns());
  }

  public void testOneInsertion() {
    TextFileCache cache = new TextFileCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "did not change.did not change.");
    cache.textFileContentCache.put(proposed,
        "did not change." +
        "Total of four scopes: abc { def { } xyz } { pqr }" +
        "did not change.");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    assertEquals(",4", data.toString());
  }

  public void testWithMultipleChangeIds() {
    TextFileCache cache = new TextFileCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "");
    cache.textFileContentCache.put(proposed, "1{2{3{4{5{6");

    File origin2 = new File("origin2");
    File proposed2 = new File("proposed2");
    cache.textFileContentCache.put(origin2, "");
    cache.textFileContentCache.put(proposed2, "1{2{3{4{5{6{7");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    analyzer._beforeAnalyzeChange(CHANGE_ID_2);
    analyzer._analyzeFile(CHANGE_ID_2, origin2, proposed2, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID_2);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    assertEquals(",6", data.toString());

    data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID_2, data);
    assertEquals(",7", data.toString());

  }
}
