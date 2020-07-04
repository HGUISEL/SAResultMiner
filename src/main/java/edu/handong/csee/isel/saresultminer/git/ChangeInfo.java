package edu.handong.csee.isel.saresultminer.git;

public class ChangeInfo {
	String dir = "";
	String start = "";
	String end = "";
	
	public ChangeInfo() {
	
	}
	
	public ChangeInfo(String dir, String start, String end) {
		this.dir = dir;
		this.start = start;
		this.end = end;
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getStart() {
		return start;
	}
	
	public String getEnd() {
		return end;
	}
}
