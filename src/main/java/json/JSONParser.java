package json;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
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
//			System.out.println("\tRevision Sha Code : " + revID);
			JSONObject revision = revisions.getJSONObject(revID);
			if (revision.getInt("_number") == 1) {
//				System.out.println("\t\tRevision No : 1");
				JSONObject files = revision.getJSONObject("files");
				Set<String> fileSet = files.keySet();
				for (String fileName : fileSet) {
//					System.out.println("\t\t\tFile Name : " + fileName);
					result.add(fileName);
				}
				return result;
			}
		}

		return result;
	}
}
