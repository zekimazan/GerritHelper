package csv.analyzer;

import java.io.File;

import junit.framework.TestCase;

public class VisualDensityAnalyzerTest extends TestCase {
  VisualDensityAnalyzer analyzer = new VisualDensityAnalyzer();
  final int CHANGE_ID = 1, CHANGE_ID_2 = 2;

  public void testColumnName() {
    assertEquals("VisualDensity", analyzer.getDumper().getColumns());
  }

  public void testOneInsertion() {
    TextFileContentCache cache = new TextFileContentCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "did not change.did not change.");
    cache.textFileContentCache.put(proposed,
        "did not change." +
        "inserted content with forty characters.." +
        "did not change.");

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    // We expect 50 since 50 = (100 x 40) / 80.
    assertEquals(",50", data.toString());
  }

  public void testMultipleLines() {
    TextFileContentCache cache = new TextFileContentCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "");
    StringBuffer insertedLines = new StringBuffer(
        "inserted content with forty characters..");
    for (int i = 0; i < 9; i++) {
      insertedLines.append("\n")
                   .append("inserted content with forty characters..");
    }
    cache.textFileContentCache.put(proposed, insertedLines.toString());

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    // We expect 50 since 50 = (100 x 40 x 10) / (10 x 80).
    assertEquals(",50", data.toString());
  }


  public void testWithMultipleChangeIds() {
    TextFileContentCache cache = new TextFileContentCache();
    File origin = new File("origin");
    File proposed = new File("proposed");
    cache.textFileContentCache.put(origin, "");
    StringBuffer insertedLines = new StringBuffer(
        "inserted content with forty characters..");
    for (int i = 0; i < 9; i++) {
      insertedLines.append("\n")
                   .append("inserted content with forty characters..");
    }
    cache.textFileContentCache.put(proposed, insertedLines.toString());

    File origin2 = new File("origin2");
    File proposed2 = new File("proposed2");
    cache.textFileContentCache.put(origin2, "");
    insertedLines = new StringBuffer(
        "new 20 characters...");
    for (int i = 0; i < 9; i++) {
      insertedLines.append("\n")
                   .append("new 20 characters...");
    }
    cache.textFileContentCache.put(proposed2, insertedLines.toString());

    analyzer._beforeAnalyzeChange(CHANGE_ID);
    analyzer._analyzeFile(CHANGE_ID, origin, proposed, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID);

    analyzer._beforeAnalyzeChange(CHANGE_ID_2);
    analyzer._analyzeFile(CHANGE_ID_2, origin2, proposed2, cache);
    analyzer._afterAnalyzeChange(CHANGE_ID_2);

    StringBuffer data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID, data);
    // We expect 50 since 50 = (100 x 40 x 10) / (10 x 80).
    assertEquals(",50", data.toString());

    data = new StringBuffer();
    analyzer.getDumper().appendDataForChange(CHANGE_ID_2, data);
    // We expect 25 since 25 = (100 x 20 x 10) / (10 x 80).
    assertEquals(",25", data.toString());

  }
}
