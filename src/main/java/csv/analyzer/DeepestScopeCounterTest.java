package csv.analyzer;

import java.io.File;

import junit.framework.TestCase;

public class DeepestScopeCounterTest extends TestCase {
  DeepestScopeCounter analyzer = new DeepestScopeCounter();
  final int CHANGE_ID = 1, CHANGE_ID_2 = 2;

  public void testColumnName() {
    assertEquals("DeepestScope", analyzer.getDumper().getColumns());
  }

  public void testOneInsertion() {
    TextFileCache cache = new TextFileCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "did not change.did not change.");
    cache.textFileContentCache.put(proposed,
        "did not change." +
        "ignore } these} but process these 1{2{3}2}1{2}1{2{3{4}3}2}1 ignore }" +
        "did not change.");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    assertEquals(",4", data.toString());
  }

  public void testUnendedScopes() {
    TextFileCache cache = new TextFileCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "did not change.did not change.");
    cache.textFileContentCache.put(proposed,
        "did not change." +
        "ignore } these} but process these 1{2{3}2}1{2}1{2{3{4{5} end" +
        "did not change.");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    assertEquals(",5", data.toString());
  }

  public void testNoScope() {
    TextFileCache cache = new TextFileCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "did not change.did not change.");
    cache.textFileContentCache.put(proposed,
        "did not change." +
        "no } new } scope started" +
        "did not change.");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    assertEquals(",1", data.toString());
  }
}
