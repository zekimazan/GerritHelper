package download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import csv.Revision;
import json.JSONParser;

public class GerritDownloaderTester {

	public static void main(String[] args) throws IOException {
		GerritDownloader downloader = new GerritDownloader();
		
		//https://android-review.googlesource.com/changes/
		//https://git.eclipse.org/r/changes/
		String url = "https://git.eclipse.org/r/changes/";
		String outputDir = "target/java-classes/";
		
		int startIndex = 9046, finishIndex = 20000;
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File("target/eclipse_revison.csv"), true));
//		printWriter.println("ChangeID,Status,#PatchSets");

		Revision revision = null;
		for (int id = startIndex; id < finishIndex + 1; id++) {
			revision = downloader.getChangeSet(url, id);
			if(revision == null)
				continue;
			ArrayList<String> fileNames = downloader.getChangedFiles(url, revision.revisionID, 1);
			if (fileNames.size() > 20)
				continue;
			System.out.println(revision.revisionID + ", " + revision.status + ", " + revision.patchSet);
			int javaFileCounter = 0;
			for (String fileName : fileNames) {
				if (fileName.endsWith(".java"))
					javaFileCounter++;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if ((double) javaFileCounter / (fileNames.size() - 1) >= 0.6) {
				System.out.println("RevisionID : " + revision.revisionID + " is mostly java");
				try {
					for (String fileName : fileNames) {
						String basicName = (fileName.lastIndexOf("/") == -1) ? fileName
								: fileName.substring(fileName.lastIndexOf("/") + 1);
						if (basicName.contains(".java")) {
							String nameToSave = revision.revisionID + "_" + 1 + "_old_" + basicName;
							downloader.downloadFileContent(url, revision.revisionID, 1, fileName, true, outputDir, nameToSave);
							BufferedOutputStream writer = null;
							
//							String base64String = downloader.getFileContent(url, revision.revisionID, 1, fileName,
//									true);
//							byte[] baseFileContent = Base64.decodeBase64(base64String);
//							File file = new File(outputDir + revision.revisionID + "_" + 1 + "_old_" + basicName);
//							try {
//								writer = new BufferedOutputStream(new FileOutputStream(file));
//								writer.write(baseFileContent);
//								writer.flush();
//								writer.close();
//								System.out.println("File is written to " + outputDir + revision.revisionID + "_" + 1
//										+ "_old_" + basicName);
//							} catch (FileNotFoundException e) {
//								System.out.println("Exception with fileName : " + revision.revisionID + "_" + 1
//										+ "_old_" + basicName);
//								e.printStackTrace();
//							} catch (IOException e) {
//								System.out.println("IOException at fileName : " + revision.revisionID + "_" + 1
//										+ "_old_" + basicName);
//								e.printStackTrace();
//							}
//
//							try {
//								Thread.sleep(500);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}

							String base64String = downloader.getFileContent(url, revision.revisionID, 1, fileName, false);
							byte[] revFileContent = Base64.decodeBase64(base64String);
							File file = new File(outputDir + revision.revisionID + "_" + 1 + "_new_" + basicName);
							try {
								writer = new BufferedOutputStream(new FileOutputStream(file));
								writer.write(revFileContent);
								writer.flush();
								writer.close();
								System.out.println("File is written to " + outputDir + revision.revisionID + "_" + 1
										+ "_new_" + basicName);
							} catch (FileNotFoundException e) {
								System.out.println("Exception with fileName : " + revision.revisionID + "_" + 1
										+ "_new_" + basicName);
								e.printStackTrace();
							} catch (IOException e) {
								System.out.println("IOException at fileName : " + revision.revisionID + "_" + 1
										+ "_new_" + basicName);
								e.printStackTrace();
							}
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (NullPointerException e) {
					System.out.println("Exception occured with Change id : " + revision.revisionID);
					e.printStackTrace();
				}
				printWriter.println(revision.revisionID + "," + revision.status + "," + revision.patchSet);
				printWriter.flush();
			} else {
				System.out.println("RevisionID : " + revision.revisionID + " is not mostly java");
			}
		}
		
		printWriter.close();
	}

}
