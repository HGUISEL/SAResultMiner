package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class Reader {
	enum Headers{
		DetectionID, LatestCommitID, PMDVersion, RuleName, FilePath, VICID, VICDate,VICLine, 
		LDCID, LDCDate, LDCLine, VFCID, VFCDate, VFCLine, FixedPeriod, OriginalCode, FixedCode, RealFix
	}
	
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
	
	public ArrayList<Alarm> readResult(String path){
		ArrayList<Alarm> temp = new ArrayList<>();
		try {
			FileReader in = new FileReader(path);
			Iterable<CSVRecord> records;			
			records = CSVFormat.RFC4180.withHeader(Headers.class).parse(in);
			int cnt = 0;
			for (CSVRecord record : records) {
				if(cnt == 0) { cnt++; continue; }
			    String id = record.get(Headers.VICID);
			    String lineNum = record.get(Headers.VICLine);
			    temp.add(new Alarm(id, lineNum));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return temp;
	}
}