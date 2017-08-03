package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import csv.analyzer.Analyzer;
import csv.analyzer.AnalyzerDumper;
import csv.analyzer.ChangeDistillerAnalyzer;
import csv.analyzer.PatchSetSizeAppender;
import csv.analyzer.TextFileContentCache;
import csv.analyzer.VisualDensityAnalyzer;
import csv.analyzer.WordCounter;

public class SourceCodeCrawler {
	static final String dirPath = "./target/java-classes/";
	static final String outputFile = "./target/output.csv";
	static final String reviewFileName = "data/Reviews.csv";

	public static void main(String[] args) {
		SourceCodeCrawler.run();
	}

	public static void run() {
		File[] files = getAllJavaFiles(dirPath);

		LinkedHashMap<Integer, ArrayList<String>> changeIdToFilesMap =
				getChangeIdToFilesMap(files);

		ArrayList<Analyzer> analyzers = initAnalyzers();
		TextFileContentCache textFileContentCache = new TextFileContentCache();

		for (int id : changeIdToFilesMap.keySet()) {
			for (Analyzer analyzer : analyzers) {
				analyzer.beforeAnalyzeChange(id);
	
				for (String fileName : changeIdToFilesMap.get(id)) {
					if (fileName.contains("_old_")) {
						File origin = new File(dirPath + fileName);
						File proposed = new File(
								dirPath + fileName.replace("_old_", "_new_"));
						analyzer.analyzeFile(
								id, origin, proposed, textFileContentCache);
					}
				}

				analyzer.afterAnalyzeChange(id);
			}

			// Cache is change id specific.
			textFileContentCache.clear();
		}

		dump(changeIdToFilesMap.keySet(), analyzers);

		// TODO(cegerede): Sanity check during refactoring. Remove later.
		// validate();
	}

	private static ArrayList<Analyzer> initAnalyzers() {
		ArrayList<Analyzer> analyzers = new ArrayList<Analyzer>();

		analyzers.add(new ChangeDistillerAnalyzer());
		analyzers.add(new WordCounter());
		analyzers.add(new VisualDensityAnalyzer()); 

		LinkedHashMap<Integer,Integer> patchSetSizeMap =
				getChangeIdToMetadataMap();
		PatchSetSizeAppender patchSetSizeAppender =
				new PatchSetSizeAppender(patchSetSizeMap);
		analyzers.add(patchSetSizeAppender);

		return analyzers;
	}

	private static LinkedHashMap<Integer, ArrayList<String>>
	getChangeIdToFilesMap(File[] files) {
		LinkedHashMap<Integer, ArrayList<String>> changeFileMap =
				new LinkedHashMap<Integer, ArrayList<String>>();
		for (File file : files) {
			String fileName = file.getName();
			int change_ID = Integer.valueOf(
					fileName.substring(0, fileName.indexOf("_")));
			if (changeFileMap.containsKey(change_ID)) {
				changeFileMap.get(change_ID).add(fileName);
			} else {
				ArrayList<String> fileList = new ArrayList<String>();
				fileList.add(fileName);
				changeFileMap.put(change_ID, fileList);
			}
		}
		return changeFileMap;
	}

	private static void dump(
			Set<Integer> changeIDs,
			ArrayList<Analyzer> analyzers) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		StringBuffer buff = new StringBuffer("Change_ID");
		for (Analyzer analyzer : analyzers) {
			AnalyzerDumper analyzerDumper = analyzer.getDumper();

			String analyzerColumns = analyzerDumper.getColumns();
			buff.append(",").append(analyzerColumns);
		}
		writer.println(buff);

		for(int change_ID : changeIDs) {
			StringBuffer data = new StringBuffer();
			data.append(change_ID);

			for (Analyzer analyzer : analyzers) {
				AnalyzerDumper analyzerDumper = analyzer.getDumper();
				analyzerDumper.appendDataForChange(change_ID, data);
			}

			writer.println(data);
		}
		writer.close();
	}

	static LinkedHashMap<Integer, Integer> getChangeIdToMetadataMap() {
		LinkedHashMap<Integer,Integer> patchSetSizeMap =
				new LinkedHashMap<Integer, Integer>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(reviewFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		scanner.nextLine();
		while (scanner.hasNext()) {
			Review r = new Review();
			String line = scanner.nextLine();
			try {
				r.read(line);
			} catch (NumberFormatException e) {
				System.out.println(
						"Number format exception at line : " + line);
				System.exit(0);
			}
			patchSetSizeMap.put(r.getChangeId(), r.getNumberOfPatchSets());
		}
		scanner.close();
		return patchSetSizeMap;
	}

	static File[] getAllJavaFiles(String path) {
		return new File(path).listFiles(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".java");
					}
				});
	}

	static void validate() {
		try {
			boolean isEqual = FileUtils.contentEquals(
					new File(outputFile),
					new File("./target/verify_output.csv"));
			if (!isEqual) {
				throw new RuntimeException("Not Equal");
			} else {
				System.out.println("Passed the test!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

