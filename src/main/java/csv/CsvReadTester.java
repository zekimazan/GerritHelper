package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import download.GerritDownloader;
import json.JSONParser;

public class CsvReadTester {
	static String memberFileName = "C:\\Users\\Zeki\\Desktop\\Bil 570\\project\\android-DefinedDataset-20120128\\Reviewers.csv";
	static String reviewFileName = "C:\\Users\\Zeki\\Desktop\\Bil 570\\project\\android-DefinedDataset-20120128\\Reviews.csv";
	static String outFileName = "C:\\Users\\Zeki\\Desktop\\GerritAndroidDataset.csv";
	static LinkedHashMap<Integer, Member> memberMap = new LinkedHashMap<Integer, Member>();
	static LinkedHashMap<Integer, Review> reviewMap = new LinkedHashMap<Integer, Review>();
	static LinkedHashMap<String, LinkedList<Integer>> branchCounter = new LinkedHashMap<String, LinkedList<Integer>>();
	static LinkedHashMap<String, LinkedList<Integer>> projectCounter = new LinkedHashMap<String, LinkedList<Integer>>();

	static final int MINIMUMREVIEW = 100;
	static final int LOCTRASHOLD = 500;
	static final double FILE_COUNT_TRESHOLD = 0.6;

	public static void main(String[] args) {
		CsvReadTester tester = new CsvReadTester();
		tester.getAllMembers();
		tester.getAllReviews();
		tester.extractNoises();
		System.out.println(reviewMap.size());
		// tester.mergeMaps();
	}

	public boolean isMostlyJava(Review review) {
		GerritDownloader downloader = new GerritDownloader();
		String url = "https://android-review.googlesource.com/changes/";

		ArrayList<String> changedFiles = new ArrayList<String>();
		String unparsedString = downloader.getChangeSet(url, review.getChangeId());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JSONParser jsonParser = new JSONParser();
//		System.out.println("Change ID : " + review.getChangeId());
		changedFiles = jsonParser.getChangedFilesFromJSON(unparsedString);
		if(changedFiles.size() == 0)
			return false;
		int javaFileCounter = 0;
		for (String fileName : changedFiles) {
			if (fileName.lastIndexOf(".") != -1) {
				if (fileName.substring(fileName.lastIndexOf(".")).contains(".java"))
					javaFileCounter++;
			}
		}
		if (javaFileCounter / (double) changedFiles.size() >= FILE_COUNT_TRESHOLD) {
			try {
				review.files = changedFiles;
			}
			catch(NullPointerException e) {
				System.out.println("Exception occured with Change id : " + review.getChangeId());
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	public void mergeMaps() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(outFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int i = 0;

		/*
		 * Change ID,Owner,Created,Updated,Link,Branch,Project,Status,#Comments,
		 * Approvers,Verifiers,AssignedReviewers,CodeReviewers,RealReviews,
		 * SelfReview,#PatchSets,#FilesInPatch,InlineComments,Addfiles,ModFiles,
		 * DelFiles,RenamedFile,DeleteLOC,AddLOC
		 * 
		 * ID,name,email,domain,#opens,#merges,#abandoned,#authors,#owners,#
		 * submitted,#PostComments,#assigned,#approved,#verified,#codeReviewer,#
		 * committed,RealReviewer
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(
				"Change ID,Owner,Dev_#opens,Dev_#merges,Dev_#abandoned,Dev_#authors,Dev_#owners,Dev_#submitted,Dev_#PostComments,"
						+ "Dev_#assigned,Dev_#approved,Dev_#verified,Dev_#codeReviewer,Dev_#committed,Dev_RealReviewer,Link,Branch,Project,"
						+ "Status,#Comments,Approvers,Verifiers,AssignedReviewers,CodeReviewers,RealReviews,SelfReview,#PatchSets,"
						+ "#FilesInPatch,InlineComments,Addfiles,ModFiles,DelFiles,RenamedFile,DeleteLOC,AddLOC\n");
		for (Integer key : reviewMap.keySet()) {
			Review review = reviewMap.get(key);
			if (memberMap.containsKey(review.getOwner())) {
				Member member = memberMap.get(review.getOwner());
				builder.append(review.getChangeId() + ",").append(review.getOwner() + ",")
						.append(member.getCount_opens() + ",");
				builder.append(member.getCount_merges() + ",").append(member.getCount_abondoned() + ",")
						.append(member.getCount_authors() + ",");
				builder.append(member.getCount_owners() + ",").append(member.getCount_submitted() + ",")
						.append(member.getCount_postComments() + ",");
				builder.append(member.getCount_assigned() + ",").append(member.getCount_approved() + ",")
						.append(member.getCount_verified() + ",");
				builder.append(member.getCount_codeReviewer() + ",").append(member.getCount_committed() + ",")
						.append(member.getRealReviewer() + ",");
				builder.append(review.getLink() + ",")
						.append(review.getBranch().equals("refs/heads/master") ? "master," : "other,")
						.append(review.getProject() + ",")
						.append((review.getStatus() == Review.Status.MERGED) ? "MERGED," : "ABANDONED,");
				builder.append(review.getNumberOfComments() + ",").append(review.getApprovers() + ",")
						.append(review.getVerifiers() + ",");
				builder.append(review.getAssignedReviewers() + ",").append(review.getCodeReviewers() + ",")
						.append(review.getRealReviews() + ",");
				builder.append(review.isSelfReview() ? "TRUE," : "FALSE,");
				int numOfPatchSets = review.getNumberOfPatchSets();
				String patchsets = "";
				if (numOfPatchSets == 1) {
					patchsets = "ONE";
				} else {
					patchsets = "NOT_ONE";
				}
				builder.append(patchsets + ",").append(review.getNumberOfFilesInPatch() + ",");
				builder.append(review.getInlineComments() + ",").append(review.getAddFiles() + ",")
						.append(review.getModFÝles() + ",").append(review.getDelFiles() + ",");
				builder.append(review.getRenamedFiles() + ",").append(review.getDeleteLOC() + ",")
						.append(review.getAddLOC() + "\n");
			}
			if (i++ == 1) {
				System.out.println(review.getNumberOfPatchSets());
			}
		}
		writer.write(builder.toString());

		writer.close();
	}

	public void getAllMembers() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(memberFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		scanner.nextLine();
		while (scanner.hasNext()) {
			Member m = new Member();
			String line = scanner.nextLine();
			try {
				m.read(line);
			} catch (NumberFormatException e) {
				System.out.println("Number format exception at line : " + line);
				System.exit(0);
			}
			memberMap.put(m.getID(), m);
		}
		scanner.close();
	}

	public void getAllReviews() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(reviewFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		scanner.nextLine();
		while (scanner.hasNext()) {
			Review r = new Review();
			String line = scanner.nextLine();
			try {
				r.read(line);
			} catch (NumberFormatException e) {
				System.out.println("Number format exception at line : " + line);
				System.exit(0);
			}

			if (r.getStatus() != Review.Status.MERGED) {
				// continue;
				r.setNumberOfPatchSets(Integer.MAX_VALUE);
			}

			if (r.getAddLOC() > LOCTRASHOLD || r.getDeleteLOC() > LOCTRASHOLD || !isMostlyJava(r))
				continue;

			System.out.println("Change ID : " + r.getChangeId());
			
			reviewMap.put(r.getChangeId(), r);
			if (branchCounter.containsKey(r.getBranch())) {
				branchCounter.get(r.getBranch()).add(r.getChangeId());
			} else {
				LinkedList<Integer> reviewList = new LinkedList<Integer>();
				reviewList.add(r.getChangeId());
				branchCounter.put(r.getBranch(), reviewList);
			}

			if (projectCounter.containsKey(r.getProject())) {
				projectCounter.get(r.getProject()).add(r.getChangeId());
			} else {
				LinkedList<Integer> reviewList = new LinkedList<Integer>();
				reviewList.add(r.getChangeId());
				projectCounter.put(r.getProject(), reviewList);
			}
		}
		scanner.close();
	}

	public void extractNoises() {
		// Set<String> keyset = branchCounter.keySet();
		// for(String key:keyset) {
		// if(branchCounter.get(key).size() <= MINIMUMREVIEW) {
		// for(Integer r:branchCounter.get(key)) {
		// reviewMap.remove(r);
		// }
		// }
		// }

		Set<String> keyset = projectCounter.keySet();
		for (String key : keyset) {
			if (projectCounter.get(key).size() <= MINIMUMREVIEW) {
				// System.out.println("Removed : " + key + ", Total : " +
				// projectCounter.get(key).size());
				for (Integer r : projectCounter.get(key)) {
					reviewMap.remove(r);
				}
			}
		}
	}
}
