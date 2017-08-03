package csv.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class ChangeDistillerAnalyzer extends Analyzer {
	LinkedHashMap<Integer, ArrayList<Integer>> sourceChangeMap =
			new LinkedHashMap<Integer, ArrayList<Integer>>();

	int mUnclassifiedFileCount = 0;
	
	public void _beforeAnalyzeChange(int id) {
		mUnclassifiedFileCount = 0;
	}

	public void _analyzeFile(
			int id, File origin, File proposed,
			TextFileCache textFileContentCache) {
		FileDistiller distiller =
				ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
			distiller.extractClassifiedSourceCodeChanges(origin, proposed);
			
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
//			System.out.println("Change ID : " + id + "," + fileName + " could not classified");
			mUnclassifiedFileCount++;
		}
		if (changes != null) {
			for (SourceCodeChange change : changes) {
				// see Javadocs for more information
				System.out.println("Type : " + change.getChangeType().name());
				int index = change.getChangeType().ordinal();
				int value = sourceChangeMap.get(id).get(index) + 1;
				sourceChangeMap.get(id).set(index, value);
//				System.out.println("Significance : " + change.getSignificanceLevel());
//				System.out.println("Parent Entity: " + change.getParentEntity().getLabel());
//				System.out.println("Root Entity: " + change.getRootEntity().getLabel());
//				System.out.println("Changed Entity : " + change.getChangedEntity());
//				System.out.println("Change : ");
//				System.out.println("\t" + change.toString() + "\n");

			}
		}
	}

	public void _afterAnalyzeChange(int id) {
		if(mUnclassifiedFileCount != 0){
			System.out.println(
					"Change ID : " + id + ", Total Files : " + mTotalFileCount +
					", Unclassified Files : " + mUnclassifiedFileCount);
			if(mUnclassifiedFileCount / (double) mTotalFileCount < 0.6) {
				sourceChangeMap.remove(id);
			}
		}
	}
	
	public AnalyzerDumper getDumper() {
		return new AnalyzerDumper() {
			public String getColumns() {
				return  "ADDING_ATTRIBUTE_MODIFIABILITY,ADDING_CLASS_DERIVABILITY,ADDING_METHOD_OVERRIDABILITY,ADDITIONAL_CLASS," + 
						"ADDITIONAL_FUNCTIONALITY,ADDITIONAL_OBJECT_STATE,ALTERNATIVE_PART_DELETE,ALTERNATIVE_PART_INSERT,ATTRIBUTE_RENAMING," + 
						"ATTRIBUTE_TYPE_CHANGE,CLASS_RENAMING,COMMENT_DELETE,COMMENT_INSERT,COMMENT_MOVE,COMMENT_UPDATE,CONDITION_EXPRESSION_CHANGE," + 
						"DECREASING_ACCESSIBILITY_CHANGE,DOC_DELETE,DOC_INSERT,DOC_UPDATE,INCREASING_ACCESSIBILITY_CHANGE,METHOD_RENAMING," + 
						"PARAMETER_DELETE,PARAMETER_INSERT,PARAMETER_ORDERING_CHANGE,PARAMETER_RENAMING,PARAMETER_TYPE_CHANGE,PARENT_CLASS_CHANGE," + 
						"PARENT_CLASS_DELETE,PARENT_CLASS_INSERT,PARENT_INTERFACE_CHANGE,PARENT_INTERFACE_DELETE,PARENT_INTERFACE_INSERT," + 
						"REMOVED_CLASS,REMOVED_FUNCTIONALITY,REMOVED_OBJECT_STATE,REMOVING_ATTRIBUTE_MODIFIABILITY,REMOVING_CLASS_DERIVABILITY," + 
						"REMOVING_METHOD_OVERRIDABILITY,RETURN_TYPE_CHANGE,RETURN_TYPE_DELETE,RETURN_TYPE_INSERT,STATEMENT_DELETE,STATEMENT_INSERT," + 
						"STATEMENT_ORDERING_CHANGE,STATEMENT_PARENT_CHANGE,STATEMENT_UPDATE,UNCLASSIFIED_CHANGE";
			}

			public void appendDataForChange(int id, StringBuffer data) {
				for(int i : sourceChangeMap.get(id)) {
					data.append(",").append(i);
				}
			}
		};
	}
}