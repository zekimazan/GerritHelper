package csv;

public class Member {

	private int ID;
	private String name;
	private String email;
	private String domain;
	private int count_opens;
	private int count_merges;
	private int count_abondoned;
	private int count_authors;
	private int count_owners;
	private int count_submitted;
	private int count_postComments;
	private int count_assigned;
	private int count_approved;
	private int count_verified;
	private int count_codeReviewer;
	private int count_committed;
	private int realReviewer;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getCount_opens() {
		return count_opens;
	}

	public void setCount_opens(int count_opens) {
		this.count_opens = count_opens;
	}

	public int getCount_merges() {
		return count_merges;
	}

	public void setCount_merges(int count_merges) {
		this.count_merges = count_merges;
	}

	public int getCount_abondoned() {
		return count_abondoned;
	}

	public void setCount_abondoned(int count_abondoned) {
		this.count_abondoned = count_abondoned;
	}

	public int getCount_authors() {
		return count_authors;
	}

	public void setCount_authors(int count_authors) {
		this.count_authors = count_authors;
	}

	public int getCount_owners() {
		return count_owners;
	}

	public void setCount_owners(int count_owners) {
		this.count_owners = count_owners;
	}

	public int getCount_submitted() {
		return count_submitted;
	}

	public void setCount_submitted(int count_submitted) {
		this.count_submitted = count_submitted;
	}

	public int getCount_postComments() {
		return count_postComments;
	}

	public void setCount_postComments(int count_postComments) {
		this.count_postComments = count_postComments;
	}

	public int getCount_assigned() {
		return count_assigned;
	}

	public void setCount_assigned(int count_assigned) {
		this.count_assigned = count_assigned;
	}

	public int getCount_approved() {
		return count_approved;
	}

	public void setCount_approved(int count_approved) {
		this.count_approved = count_approved;
	}

	public int getCount_verified() {
		return count_verified;
	}

	public void setCount_verified(int count_verified) {
		this.count_verified = count_verified;
	}

	public int getCount_codeReviewer() {
		return count_codeReviewer;
	}

	public void setCount_codeReviewer(int count_codeReviewer) {
		this.count_codeReviewer = count_codeReviewer;
	}

	public int getCount_committed() {
		return count_committed;
	}

	public void setCount_committed(int count_committed) {
		this.count_committed = count_committed;
	}

	public int getRealReviewer() {
		return realReviewer;
	}

	public void setRealReviewer(int realReviewer) {
		this.realReviewer = realReviewer;
	}

	public void read(String line) throws NumberFormatException {
		String args[] = line.split(",");
		this.ID = Integer.valueOf(args[0]);
		this.name = args[1];
		this.email = args[2];
		this.domain = args[3];
		this.count_opens = Integer.valueOf(args[4]);
		this.count_merges = Integer.valueOf(args[5]);
		this.count_abondoned = Integer.valueOf(args[6]);
		this.count_authors = Integer.valueOf(args[7]);
		this.count_owners = Integer.valueOf(args[8]);
		this.count_submitted = Integer.valueOf(args[9]);
		this.count_postComments = Integer.valueOf(args[10]);
		this.count_assigned = Integer.valueOf(args[11]);
		this.count_approved = Integer.valueOf(args[12]);
		this.count_verified = Integer.valueOf(args[13]);
		this.count_codeReviewer = Integer.valueOf(args[14]);
		this.count_committed = Integer.valueOf(args[15]);
		this.realReviewer = Integer.valueOf(args[16]);
	}
}