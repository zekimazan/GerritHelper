package csv.analyzer;

import java.io.File;
import java.util.LinkedHashMap;

public class PatchSetSizeAppender extends Analyzer {
	LinkedHashMap<Integer,Integer> mPatchSetSizeMap;

	public PatchSetSizeAppender(
			LinkedHashMap<Integer, Integer> patchSetSizeMap) {
		mPatchSetSizeMap = patchSetSizeMap;
	}

	@Override
	public void _afterAnalyzeChange(int arg0) {}

	@Override
	public void _analyzeFile(int changeId, File origin, File proposed,
	        TextFileContentCache textFileContentCache) {}

	@Override
	public void _beforeAnalyzeChange(int arg0) {}

	@Override
	public AnalyzerDumper getDumper() {
		return new AnalyzerDumper() {

			public String getColumns() {
				return "#PatchSets";
			}

			public void appendDataForChange(int id, StringBuffer data) {
				String singlePatchOrNot =
						(mPatchSetSizeMap.get(id) == 1) ? "ONE":"NOT_ONE";
				data.append(",").append(singlePatchOrNot);
			}
		};
	}
}