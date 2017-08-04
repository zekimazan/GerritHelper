package csv;

public class Revision {
	public int revisionID;
	public String status;
	public int patchSet;
	
	public Revision(int revisionID, String status, int patchSet) {
		this.revisionID = revisionID;
		this.status = status;
		this.patchSet = patchSet;
	}
	
	public Revision() {
		
	}
	
	public void read(String line) {
		String[] args = line.split(",");
		revisionID = Integer.valueOf(args[0]);
		status = (args[1].equals("MERGED")) ? "MERGED" : "ABANDONED";
		patchSet = Integer.valueOf(args[2]);
	}
}
