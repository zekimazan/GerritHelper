package download;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import json.JSONParser;

public class GerritDownloaderTester {

	public static void main(String[] args) {
		GerritDownloader downloader = new GerritDownloader();
		String url = "https://android-review.googlesource.com/changes/";
		String filePath = "eclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/common/api/DrawingStyle.java";
//		System.out.println(downloader.getChangeSet(url, 18840));
		
		String base64String = downloader.getFileContent(url, 18840, 1, filePath, true);
		byte[] response = Base64.decodeBase64(base64String);
		for(int i = 0; i < response.length; i++){
			System.out.print((char)response[i]);
		}
//		ArrayList<String> changedFiles = new ArrayList<String>();
//		String unparsedString = downloader.getChangeSet(url, 18840);
//		
//		JSONParser jsonParser = new JSONParser();
//		changedFiles  = jsonParser.getChangedFilesFromJSON(unparsedString);
//		System.out.println();
		
		//https://android-review.googlesource.com/cat/18840%2C1%2Ceclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/eclipse/adt/internal/editors/layout/UiElementPullParser.java%5E0
//		String fileUrl = "https://android-review.googlesource.com/";
//		downloader.getFile(fileUrl, 18840, 1, "eclipse/plugins/com.android.ide.eclipse.adt/src/com/android/ide/eclipse/adt/internal/editors/layout/UiElementPullParser.java", true);
	}

}
