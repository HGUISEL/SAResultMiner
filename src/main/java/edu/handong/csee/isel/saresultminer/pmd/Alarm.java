package edu.handong.csee.isel.saresultminer.pmd;

public class Alarm {
	String dir = "";
	String lineNum = "";
	String violatingCode = "";
	
	public Alarm(String alarm) {
		if(alarm.split(":").length > 2) {
			dir = alarm.split(":")[0];
			lineNum = alarm.split(":")[1];			
			
		}
	}
	
	public Alarm(String path, String lineNum) {
		dir = path;
		this.lineNum = lineNum;
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getLineNum() {
		return lineNum;
	}
	
	public String getViolatingCode() {
		return violatingCode;
	}
	
}
