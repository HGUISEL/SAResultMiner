package edu.handong.csee.isel.saresultminer.git;

public class Commit {
	String commitID = "";
	String commitDate = "";
	
	public Commit(String id, String date) {
		this.commitID = id;
		this.commitDate = date;
	}
	
	public String getID() {
		return commitID;
	}
	
	public String getTime() {
		return commitDate;
	}
}
