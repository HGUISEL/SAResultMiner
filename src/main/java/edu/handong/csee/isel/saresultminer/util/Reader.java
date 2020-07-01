package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class Reader {
	public String readInput(String path) {
		File f = new File(path);
		try {
			FileReader fReader =new FileReader(f);
			BufferedReader fBufReader = new BufferedReader(fReader);
			String str = "";
			
			while((str = fBufReader.readLine()) != null) {
				fBufReader.close();
				return str;
			}
			fBufReader.close();
		} 
		catch (IOException e) {
				e.printStackTrace();
		}
		return "WRONG";
	}
	
	public ArrayList<Alarm> readReportFile(String path){
		File f = new File(path);
		ArrayList<Alarm> alarms = new ArrayList<>();
		
		try {
			FileReader fReader =new FileReader(f);
			BufferedReader fBufReader = new BufferedReader(fReader);
			String alarm = "";		
			
			while((alarm = fBufReader.readLine()) != null) {
				Alarm temp = new Alarm(alarm);
				alarms.add(temp);
			}
			fBufReader.close();
		} 
		catch (IOException e) {
				e.printStackTrace();
		}
		
		return alarms;
	}
}