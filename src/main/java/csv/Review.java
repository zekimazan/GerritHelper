package csv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Review {

	public enum Status {
		MERGED, ABANDONED, OPEN
	}

	private int changeId;
	private int owner;
	private Date created;
	private Date updated;
	private String link;
	private String branch;
	private String project;
	private Status status;
	private int numberOfComments;
	private int approvers;
	private int verifiers;
	private int assignedReviewers;
	private int codeReviewers;
	private int realReviews;
	private boolean selfReview;
	private int numberOfPatchSets;
	private int numberOfFilesInPatch;
	private int inlineComments;
	private int addFiles;
	private int modFÝles;
	private int delFiles;
	private int renamedFiles;
	private int deleteLOC;
	private int addLOC;
	public ArrayList<String> files;

	public void read(String line) throws NumberFormatException {
		String args[] = line.split(",");
		this.changeId = Integer.parseInt(args[0]);
		this.owner = Integer.parseInt(args[1]);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
		try {
			this.created = sdf.parse(args[2]);
			this.updated = sdf.parse(args[3]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.link = args[4];
		this.branch = args[5];
		this.project = args[6];
		this.status = Status.valueOf(args[7].toUpperCase());
		this.numberOfComments = Integer.valueOf(args[8]);
		this.approvers = Integer.valueOf(args[9]);
		this.verifiers = Integer.valueOf(args[10]);
		this.assignedReviewers = Integer.valueOf(args[11]);
		this.codeReviewers = Integer.valueOf(args[12]);
		this.realReviews = Integer.valueOf(args[13]);
		this.selfReview = Boolean.valueOf(args[14]);
		this.numberOfPatchSets = Integer.valueOf(args[15]);
		this.numberOfFilesInPatch = Integer.valueOf(args[16]);
		this.inlineComments = Integer.valueOf(args[17]);
		this.addFiles = Integer.valueOf(args[18]);
		this.modFÝles = Integer.valueOf(args[19]);
		this.delFiles = Integer.valueOf(args[20]);
		this.renamedFiles = Integer.valueOf(args[21]);
		this.deleteLOC = Integer.valueOf(args[22]);
		this.addLOC = Integer.valueOf(args[23]);
		this.files = new ArrayList<String>();
	}

	public int getChangeId() {
		return changeId;
	}

	public void setChangeId(int changeId) {
		this.changeId = changeId;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getApprovers() {
		return approvers;
	}

	public void setApprovers(int approvers) {
		this.approvers = approvers;
	}

	public int getVerifiers() {
		return verifiers;
	}

	public void setVerifiers(int verifiers) {
		this.verifiers = verifiers;
	}

	public int getAssignedReviewers() {
		return assignedReviewers;
	}

	public void setAssignedReviewers(int assignedReviewers) {
		this.assignedReviewers = assignedReviewers;
	}

	public int getCodeReviewers() {
		return codeReviewers;
	}

	public void setCodeReviewers(int codeReviewers) {
		this.codeReviewers = codeReviewers;
	}

	public int getRealReviews() {
		return realReviews;
	}

	public void setRealReviews(int realReviews) {
		this.realReviews = realReviews;
	}

	public boolean isSelfReview() {
		return selfReview;
	}

	public void setSelfReview(boolean selfReview) {
		this.selfReview = selfReview;
	}

	public int getNumberOfPatchSets() {
		return numberOfPatchSets;
	}

	public void setNumberOfPatchSets(int numberOfPatchSets) {
		this.numberOfPatchSets = numberOfPatchSets;
	}

	public int getNumberOfFilesInPatch() {
		return numberOfFilesInPatch;
	}

	public void setNumberOfFilesInPatch(int numberOfFilesInPatch) {
		this.numberOfFilesInPatch = numberOfFilesInPatch;
	}

	public int getInlineComments() {
		return inlineComments;
	}

	public void setInlineComments(int inlineComments) {
		this.inlineComments = inlineComments;
	}

	public int getAddFiles() {
		return addFiles;
	}

	public void setAddFiles(int addFiles) {
		this.addFiles = addFiles;
	}

	public int getModFÝles() {
		return modFÝles;
	}

	public void setModFÝles(int modFÝles) {
		this.modFÝles = modFÝles;
	}

	public int getDelFiles() {
		return delFiles;
	}

	public void setDelFiles(int delFiles) {
		this.delFiles = delFiles;
	}

	public int getRenamedFiles() {
		return renamedFiles;
	}

	public void setRenamedFiles(int renamedFiles) {
		this.renamedFiles = renamedFiles;
	}

	public int getDeleteLOC() {
		return deleteLOC;
	}

	public void setDeleteLOC(int deleteLOC) {
		this.deleteLOC = deleteLOC;
	}

	public int getAddLOC() {
		return addLOC;
	}

	public void setAddLOC(int addLOC) {
		this.addLOC = addLOC;
	}
}
