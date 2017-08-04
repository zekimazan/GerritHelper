package csv.analyzer;

public interface AnalyzerDumper {
	public String getColumns();
	public void appendDataForChange(int id, StringBuffer data);
}