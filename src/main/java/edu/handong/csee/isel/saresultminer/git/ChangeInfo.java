package edu.handong.csee.isel.saresultminer.git;

public class ChangeInfo {
	String dir = "";
	String start = "";
	String end = "";
	//How many line changed
	String changeNum = "";
	
	public ChangeInfo() {
	
	}
	
	public ChangeInfo(String dir, String start, String end, String changeNum) {
		this.dir = dir;
		this.start = start;
		this.end = end;
		this.changeNum = changeNum;
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
