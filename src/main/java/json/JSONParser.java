package json;

import java.util.ArrayList;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import csv.Revision;

public class JSONParser {
	
	public static ArrayList<Revision> getReviewIds(String unparsedString) {
		ArrayList<Revision> reviewIDList = new ArrayList<Revision>();
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(unparsedString);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			int reviewID = object.getInt("_number");
			String status = object.getString("status");
			int patchSet = object.getJSONObject("revisions").length();
			
			reviewIDList.add(new Revision(reviewID, status, patchSet));
		}
		
		return reviewIDList;
	}
	
	public ArrayList<String> getChangedFilesFromJSON(String unparsedString) {
		ArrayList<String> result = new ArrayList<String>();
		if(unparsedString.length() <= 20)
			return result;
		JSONObject obj = null;
		try {
			obj = new JSONObject(unparsedString);
		} catch (Exception e) {
			System.out.println("Exception occured with text : \n" + unparsedString);
			e.printStackTrace();
		}

		JSONObject revisions = obj.getJSONObject("revisions");
		Set<String> keySet = revisions.keySet();
		for (String revID : keySet) {
			JSONObject revision = revisions.getJSONObject(revID);
			if (revision.getInt("_number") == 1) {
				JSONObject files = revision.getJSONObject("files");
				Set<String> fileSet = files.keySet();
				for (String fileName : fileSet) {
					result.add(fileName);
				}
				return result;
			}
		}

		return result;
	}
}
