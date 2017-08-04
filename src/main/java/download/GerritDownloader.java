package download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONObject;
import csv.Revision;
import json.JSONParser;
import zip.Unzipper;

public class GerritDownloader {
	String options = "&o=ALL_REVISIONS&o=ALL_COMMITS&o=ALL_FILES&o=MESSAGES";

	// skips last <skip> reviews and returns last <length> reviews with status
	// is <status>
	public ArrayList<Revision> getReviewList(String urlToRead, String status, int length, int skip) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		String querry = "?q=status:" + status + "&n=" + length + "&S=" + skip + "&o=ALL_REVISIONS";
		try {
			url = new URL(urlToRead + querry);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP Error Code : " + responseCode);
			}
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			rd.readLine();
			while ((line = rd.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONParser.getReviewIds(stringBuilder.toString());

	}

	public Revision getChangeSet(String urlToRead, int change_ID) {
		StringBuilder result = new StringBuilder();
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String querry = "?q=" + change_ID;
		try {
			url = new URL(urlToRead + querry + options);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				return null;
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			rd.readLine();
			while ((line = rd.readLine()) != null) {
				result.append(line + "\n");
			}
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Revision> revisions = JSONParser.getReviewIds(result.toString());
		if (revisions == null || revisions.isEmpty())
			return null;
		return revisions.get(0);
	}

	public ArrayList<String> getChangedFiles(String urlToRead, int change_ID, int revisionNumber) {
		ArrayList<String> files = new ArrayList<String>();
		// "https://gerrit.wikimedia.org/r/changes/356858/revisions/1/files/"

		StringBuilder result = new StringBuilder();
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		try {
			url = new URL(urlToRead + change_ID + "/revisions/" + revisionNumber + "/files/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP Code : " + responseCode + "\t at ChangeID : " + change_ID);
				return files;
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line + "\n");
			}
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String jsonResult = result.toString().substring(5, result.length() - 1);
		JSONObject obj = null;
		try {
			obj = new JSONObject(jsonResult);
		} catch (Exception e) {
			System.out.println("Exception occured with text : \n" + jsonResult);
			e.printStackTrace();
		}

		for (String key : obj.keySet()) {
			files.add(key);
		}

		return files;
	}

	public String getFile(String urlToRead, int change_ID, int revision, String fileName, boolean isBase) {
		int BUFFER_SIZE = 4096;
		String saveDir = "C:/Users/Zeki/Desktop/AndoidGerritJavaFiles";

		URL url;
		HttpURLConnection conn;
		InputStream inputStream;
		try {
			url = new URL(urlToRead + "/cat/" + change_ID + "," + revision + "," + fileName + "^" + (isBase ? 1 : 0));
			conn = (HttpURLConnection) url.openConnection();
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				return "Http Connection error code " + responseCode;
			}
			String disposition = conn.getHeaderField("Content-Disposition");
			String contentType = conn.getContentType();
			int contentLength = conn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = "output.zip";
			}
			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			inputStream = conn.getInputStream();
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "File downloaded.";
	}

	public String getFileContent(String urlToRead, int change_ID, int revision, String filePath, boolean isParent) {
		StringBuilder result = new StringBuilder();
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;

		try {
			filePath = URLEncoder.encode(filePath, "UTF-8");
			url = new URL(urlToRead + change_ID + "/revisions/" + revision + "/files/" + filePath + "/content"
					+ ((isParent) ? "?parent=1" : ""));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				return "Http Connection Error code " + responseCode;
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line + "\n");
			}
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public void downloadFileContent(String urlToRead, int change_ID, int revision, String filePath, boolean isParent,
			String outputDir, String basicName) {
		int BUFFER_SIZE = 4096;
		String fileName = "deneme.zip";
		URL url;
		HttpURLConnection conn;
		InputStream inputStream;
		try {
			filePath = URLEncoder.encode(filePath, "UTF-8");
			url = new URL(urlToRead + change_ID + "/revisions/" + revision + "/files/" + filePath + "/download"
					+ ((isParent) ? "?parent=1" : ""));
			conn = (HttpURLConnection) url.openConnection();
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				return;
			}
			String disposition = conn.getHeaderField("Content-Disposition");
			String contentType = conn.getContentType();
			int contentLength = conn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = "output.zip";
			}
//			System.out.println("Content-Type = " + contentType);
//			System.out.println("Content-Disposition = " + disposition);
//			System.out.println("Content-Length = " + contentLength);
//			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			inputStream = conn.getInputStream();
			String saveFilePath = outputDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();
			
			Unzipper unzipper = new Unzipper();
			unzipper.unZipIt(saveFilePath, outputDir, basicName);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
