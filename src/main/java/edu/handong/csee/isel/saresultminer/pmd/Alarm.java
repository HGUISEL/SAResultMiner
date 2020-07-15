package edu.handong.csee.isel.saresultminer.pmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.handong.csee.isel.saresultminer.util.Result;

public class Alarm {
	int detectionIDInResult = 0;
	String dir = "";
	String lineNum = "";
	String code = "";
	String status = "";
	
	public Alarm() {
		
	}
	
	public Alarm(String alarm) {		
			dir = alarm.split(":")[0];
			dir = "." + dir.split("/SAResultMiner")[1];
			lineNum = alarm.split(":")[1];			
			readFile(dir, lineNum);
	}
	
	public Alarm(Result result) {
		detectionIDInResult = result.getDetectionID();
		dir =result.getFilePath();
		if(result.getLDCLineNum().equals("")) {
			lineNum = result.getVICLineNum();
			status = "LDC";
		}
		else lineNum = result.getLDCLineNum();
		if(!result.getVFCID().equals("")) {
			status = "VFC";
		}
		code = result.getOriginCode();
	}
	
	public Alarm(String path, String lineNum, String code) {
		dir = path;
		this.lineNum = lineNum;
		this.code = code;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getDetectionIDInResult() {
		return detectionIDInResult;
	}
	
	public void setDetectionIDInResult(int id) {
		detectionIDInResult = id;
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
	
	public void setCode(String code) {
		this.code = code;
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
