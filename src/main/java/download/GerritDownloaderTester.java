package download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import json.JSONParser;

public class GerritDownloaderTester {

	public static void main(String[] args) throws IOException {
		GerritDownloader downloader = new GerritDownloader();
		String url = "https://android-review.googlesource.com/changes/";
		String filePath = "eclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/common/api/DrawingStyle.java";
		// System.out.println(downloader.getChangeSet(url, 18840));

		String base64String = downloader.getFileContent(url, 18840, 1, filePath, true);
		byte[] baseFileContent = Base64.decodeBase64(base64String);
		File file = new File("base.java");
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		writer.write(baseFileContent);
		writer.flush();
		writer.close();

		base64String = downloader.getFileContent(url, 18840, 1, filePath, false);
		byte[] revFileContent = Base64.decodeBase64(base64String);
		File file1 = new File("base1.java");
		writer = new BufferedOutputStream(new FileOutputStream(file1));
		writer.write(revFileContent);
		writer.flush();
		writer.close();

		// for(int i = 0; i < baseFileContent.length; i++){
		// System.out.print((char)baseFileContent[i]);
		// }

		// System.out.println("*************************************************************************");

		// for(int i = 0; i < revFileContent.length; i++){
		// System.out.print((char)revFileContent[i]);
		// }

		File left = new File("base.java");
		File right = new File("base1.java");

		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
			distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch (Exception e) {
			/*
			 * An exception most likely indicates a bug in ChangeDistiller.
			 * Please file a bug report at
			 * https://bitbucket.org/sealuzh/tools-changedistiller/issues and
			 * attach the full stack trace along with the two files that you
			 * tried to distill.
			 */
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if (changes != null) {
			for (SourceCodeChange change : changes) {
				// see Javadocs for more information
				System.out.println("Type : " + change.getChangeType().name());
				System.out.println("Significance : " + change.getSignificanceLevel());
				System.out.println("Parent Entity: " + change.getParentEntity().getLabel());
				System.out.println("Root Entity: " + change.getRootEntity().getLabel());
				System.out.println("Changed Entity : " + change.getChangedEntity());
				System.out.println("Change : ");
				System.out.println("\t" + change.toString() + "\n");

			}
		}

		// ArrayList<String> changedFiles = new ArrayList<String>();
		// String unparsedString = downloader.getChangeSet(url, 18840);
		//
		// JSONParser jsonParser = new JSONParser();
		// changedFiles = jsonParser.getChangedFilesFromJSON(unparsedString);
		// System.out.println();

		// https://android-review.googlesource.com/cat/18840%2C1%2Ceclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/eclipse/adt/internal/editors/layout/UiElementPullParser.java%5E0
		// String fileUrl = "https://android-review.googlesource.com/";
		// downloader.getFile(fileUrl, 18840, 1,
		// "eclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/eclipse/adt/internal/editors/layout/UiElementPullParser.java",
		// true);
	}

}
