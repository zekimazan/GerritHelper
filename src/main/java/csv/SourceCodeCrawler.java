package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class SourceCodeCrawler {

	static LinkedHashMap<Integer,Integer> patchSetSizeMap;
	static LinkedHashMap<Integer, ArrayList<String>> changeFileMap;
	static String dirPath = "./target/java-classes/";
	static LinkedHashMap<Integer, ArrayList<Integer>> sourceChangeMap;
	static String outputFile = "./target/output.csv";
	static String reviewFileName = "data\\Reviews.csv";
	
	public static void main(String[] args) {
		changeFileMap = new LinkedHashMap<Integer, ArrayList<String>>();
		sourceChangeMap = new LinkedHashMap<Integer, ArrayList<Integer>>();
		patchSetSizeMap = new LinkedHashMap<Integer, Integer>();
		SourceCodeCrawler sc = new SourceCodeCrawler();
		sc.getAllReviews();
		
		File[] files = new File(dirPath).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		});

		for (File file : files) {
			String fileName = file.getName();
			int change_ID = Integer.valueOf(fileName.substring(0, fileName.indexOf("_")));
			if (changeFileMap.containsKey(change_ID)) {
				changeFileMap.get(change_ID).add(fileName);
			} else {
				ArrayList<String> fileList = new ArrayList<String>();
				fileList.add(fileName);
				changeFileMap.put(change_ID, fileList);
			}
		}

		for (int id : changeFileMap.keySet()) {
			int totalFileCont = 0;
			int unclassifiedFileCount = 0;
//			System.out.println(id + " ---> " + changeFileMap.get(id).size());
			for (String fileName : changeFileMap.get(id)) {
				if (fileName.contains("_old_")) {
					totalFileCont++;
					File left = new File(dirPath + fileName);
					File right = new File(dirPath + fileName.replace("_old_", "_new_"));
					FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
					try {
						distiller.extractClassifiedSourceCodeChanges(left, right);
						
					} catch (Exception e) {
						/*
						 * An exception most likely indicates a bug in
						 * ChangeDistiller. Please file a bug report at
						 * https://bitbucket.org/sealuzh/tools-changedistiller/
						 * issues and attach the full stack trace along with the
						 * two files that you tried to distill.
						 */
						System.err.println("Warning: error while change distilling. " + e.getMessage());
					}
					ArrayList<Integer> list = new ArrayList<Integer>();
					for(int i = 0; i < ChangeType.getNumberOfChangeTypes(); i++) {
						list.add(0);
					} 
					sourceChangeMap.put(id, list);
					List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
					
					if(changes.size() == 0) {
//						System.out.println("Change ID : " + id + "," + fileName + " could not classified");
						unclassifiedFileCount++;
					}
					if (changes != null) {
						for (SourceCodeChange change : changes) {
							// see Javadocs for more information
							System.out.println("Type : " + change.getChangeType().name());
							int index = change.getChangeType().ordinal();
							int value = sourceChangeMap.get(id).get(index) + 1;
							sourceChangeMap.get(id).set(index, value);
//							System.out.println("Significance : " + change.getSignificanceLevel());
//							System.out.println("Parent Entity: " + change.getParentEntity().getLabel());
//							System.out.println("Root Entity: " + change.getRootEntity().getLabel());
//							System.out.println("Changed Entity : " + change.getChangedEntity());
//							System.out.println("Change : ");
//							System.out.println("\t" + change.toString() + "\n");

						}
					}
				}
			}
			if(unclassifiedFileCount!=0){
				System.out.println("Change ID : " + id + ", Total Files : " + totalFileCont + ", Unclassified Files : " + unclassifiedFileCount);
				if(unclassifiedFileCount / (double) totalFileCont < 0.6) {
					sourceChangeMap.remove(id);
				}
			}
		}
		
		System.out.println("Total Change : " + sourceChangeMap.size());
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(outputFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String header = "Change_ID,ADDING_ATTRIBUTE_MODIFIABILITY,ADDING_CLASS_DERIVABILITY,ADDING_METHOD_OVERRIDABILITY,ADDITIONAL_CLASS," + 
				"ADDITIONAL_FUNCTIONALITY,ADDITIONAL_OBJECT_STATE,ALTERNATIVE_PART_DELETE,ALTERNATIVE_PART_INSERT,ATTRIBUTE_RENAMING," + 
				"ATTRIBUTE_TYPE_CHANGE,CLASS_RENAMING,COMMENT_DELETE,COMMENT_INSERT,COMMENT_MOVE,COMMENT_UPDATE,CONDITION_EXPRESSION_CHANGE," + 
				"DECREASING_ACCESSIBILITY_CHANGE,DOC_DELETE,DOC_INSERT,DOC_UPDATE,INCREASING_ACCESSIBILITY_CHANGE,METHOD_RENAMING," + 
				"PARAMETER_DELETE,PARAMETER_INSERT,PARAMETER_ORDERING_CHANGE,PARAMETER_RENAMING,PARAMETER_TYPE_CHANGE,PARENT_CLASS_CHANGE," + 
				"PARENT_CLASS_DELETE,PARENT_CLASS_INSERT,PARENT_INTERFACE_CHANGE,PARENT_INTERFACE_DELETE,PARENT_INTERFACE_INSERT," + 
				"REMOVED_CLASS,REMOVED_FUNCTIONALITY,REMOVED_OBJECT_STATE,REMOVING_ATTRIBUTE_MODIFIABILITY,REMOVING_CLASS_DERIVABILITY," + 
				"REMOVING_METHOD_OVERRIDABILITY,RETURN_TYPE_CHANGE,RETURN_TYPE_DELETE,RETURN_TYPE_INSERT,STATEMENT_DELETE,STATEMENT_INSERT," + 
				"STATEMENT_ORDERING_CHANGE,STATEMENT_PARENT_CHANGE,STATEMENT_UPDATE,UNCLASSIFIED_CHANGE" + 
				",#PatchSets";
		
		writer.println(header);
		String line;
		for(int change_ID : sourceChangeMap.keySet()) {
			line = change_ID + "";
			for(int i:sourceChangeMap.get(change_ID)) {
				line += "," + i;
			}
			line += "," + ((patchSetSizeMap.get(change_ID) == 1) ? "ONE":"NOT_ONE");
			writer.println(line);
		}
		writer.close();
		
	}

	
	public void getAllReviews() {
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
				System.out.println("Number format exception at line : " + line);
				System.exit(0);
			}
			patchSetSizeMap.put(r.getChangeId(), r.getNumberOfPatchSets());
		}
		scanner.close();
	}
}
