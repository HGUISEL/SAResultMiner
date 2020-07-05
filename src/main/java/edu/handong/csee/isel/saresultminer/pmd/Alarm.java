package edu.handong.csee.isel.saresultminer.pmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Alarm {
	String dir = "";
	String lineNum = "";
	String code = "";
	
	public Alarm(String alarm) {
		if(alarm.split(":").length > 2) {
			dir = alarm.split(":")[0];
			lineNum = alarm.split(":")[1];			
			readFile(dir, lineNum);
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
	
	public String getCode() {
		return code;
	}
	
	public void setLineNum(String num) {
		this.lineNum = num;
	}
	
	private void readFile(String dir, String lineNum) {
		File f = new File(dir);
		try {
			FileReader fReader =new FileReader(f);
			BufferedReader fBufReader = new BufferedReader(fReader);
			String str = "";
			int num = 1;
			while((str = fBufReader.readLine()) != null) {				
				if(num == Integer.parseInt(lineNum)) {
					code = str;
					break;
				}
				num++;
			}
			fBufReader.close();
		} 
		catch (IOException e) {
				e.printStackTrace();
		}		
	}
}
