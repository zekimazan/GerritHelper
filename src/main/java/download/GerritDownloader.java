package download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class GerritDownloader {
	String options = "&o=ALL_REVISIONS&o=ALL_COMMITS&o=ALL_FILES&o=MESSAGES";

	public String getChangeSet(String urlToRead, int change_ID) {
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
				return "Http Connection Error code " + responseCode;
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

		return result.toString().substring(6, result.length() - 2);
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
			url = new URL(urlToRead + "/" + change_ID + "/revisions/" + revision + "/files/" + filePath + "/content"
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
}
