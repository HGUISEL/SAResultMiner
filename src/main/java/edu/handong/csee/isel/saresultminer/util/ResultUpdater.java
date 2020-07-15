package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.handong.csee.isel.saresultminer.git.ChangeInfo;
import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class ResultUpdater {
	ArrayList<Alarm> changedAlarms = new ArrayList<>();
	ArrayList<Alarm> unchangedAlarms = new ArrayList<>();
	
	public void init() {
		changedAlarms.clear();
		unchangedAlarms.clear();
	}
	
	public void updateResultLineNum(ArrayList<Alarm> alarms, ArrayList<ChangeInfo> changes) {
		
		if(alarms.size() == 0 || changes.size() == 0) {
			return;
		} else {
			for(Alarm alarm : alarms) {
				if(alarm.getStatus().equals("VFC")) continue;
				String originAlarmedLine = "" + alarm.getLineNum().trim();
				Alarm classifiedAlarm = new Alarm();
				int cnt = 0;
				for(ChangeInfo change : changes) {
					if(alarm.getDir().trim().equals(change.getDir())) {
						classifiedAlarm = classifyAlarms(alarm, change);
						if(classifiedAlarm == null) {
							//file deleted
							if(change.getNewStart() == 0 && change.getNewRange() == 0) {
								alarm.setLineNum("0");
								alarm.setCode("FILE IS DELETED");
								changedAlarms.add(alarm);
								break;
							} else {
							//line is deleted
							alarm.setLineNum("-1");
							alarm.setCode(change.getChangedCode());
							changedAlarms.add(alarm);
							break;
							}							
						}
						else if(!alarm.getLineNum().equals(classifiedAlarm.getLineNum())) {
							classifiedAlarm.setDetectionIDInResult(alarm.getDetectionIDInResult());
							changedAlarms.add(classifiedAlarm);
							break;
						}
					}
					cnt ++;
				}
				if(changes.size() == cnt) {
					alarm.setLineNum(getLineNum(alarm, originAlarmedLine));
					unchangedAlarms.add(alarm);
				}
			}
		}
	}
	
	private Alarm classifyAlarms(Alarm alarm, ChangeInfo change) {		
		int alarmLine = Integer.parseInt(alarm.getLineNum().trim());
		int flag = 0;
		if(change.getNewStart() == 0 && change.getNewRange() == 0) {
			//file is deleted
			return null;
		}
		
		if(alarmLine <= change.getOldStart()) {			
				return alarm;						
		} 
		else if(change.getOldStart() != 0 && change.getOldRange() != 0 && alarmLine >= change.getOldEnd()) {
			int changedLine = alarmLine + change.getNewRange() - change.getOldRange();
			alarm.setLineNum("" + changedLine);
			return alarm;
		} 
		else if(change.getOldStart() == 0 && change.getOldRange() == 0) {			
			int changedLineNum = 0;			
			if(change.getChangedCode().contains(alarm.getCode())) {
				changedLineNum = change.getNewStart();
				for(String codeLine : change.getChangedCode().split("\n")) {					
					String [] splitCode = change.getChangedCode().split("\n");
					String alarmedCode = alarm.getCode().trim();
					String predictedCode = splitCode[alarmLine-1].replaceAll("[+-]"," ").trim();
					if(flag == 0 && alarmedCode.equals(predictedCode)) {
						alarm.setLineNum("" + alarmLine);
						flag = 1;
						return alarm;
					}
					if(codeLine.split(" ").length > 1 &&codeLine.split(" ", 2)[1].trim().equals(alarm.getCode().trim())) {																		
							alarm.setLineNum("" + changedLineNum);
							return alarm;										
					} else if(codeLine.startsWith(" ") || codeLine.startsWith("+")) {
						changedLineNum++;
					}					
				}
			}	
		}
		else if(change.getOldStart() < alarmLine && alarmLine < change.getOldEnd()) {
			int changedLineNum = 0;			
			if(change.getChangedCode().contains(alarm.getCode())) {
				changedLineNum = change.getNewStart();
				for(String codeLine : change.getChangedCode().split("\n")) {
					if(codeLine.split(" ").length > 1 &&codeLine.split(" ", 2)[1].trim().equals(alarm.getCode().trim())) {						
						if(codeLine.startsWith("-")) {
							//violating code line is deleted
							return null;
						} else {
							alarm.setLineNum("" + changedLineNum);
							return alarm;
						}						
					} else if(codeLine.startsWith(" ") || codeLine.startsWith("+")) {
						changedLineNum++;
					}
					
				}
			}			
		}
		return alarm;
	}
	
	private String getLineNum(Alarm alarm, String originLineNum) {		
		File f = new File(alarm.getDir());
		if(!f.exists()) {
			return "File Doesn't Exist";
		}
		int lineNum = Integer.parseInt(alarm.getLineNum());
		int originLineNumber = Integer.parseInt(originLineNum);
		int cnt = 1;
		ArrayList<String> codes = new ArrayList<>();
		ArrayList<Integer> sameCodes = new ArrayList<>();
		try {
			FileReader fReader =new FileReader(f);
			BufferedReader fBufReader = new BufferedReader(fReader);
			String str = "";
			
			while((str = fBufReader.readLine()) != null) {				
				codes.add(str);
				if(str.equals(alarm.getCode())) {
					sameCodes.add(cnt);
				}
				cnt++;
			}			
			fBufReader.close();
			
		} 
		catch (IOException e) {
				e.printStackTrace();
		}
		
		if(codes.get(lineNum-1).equals(alarm.getCode())) {
			return alarm.getLineNum();
		} else if(codes.get(originLineNumber-1).equals(alarm.getCode())) {
			return originLineNum; 
		} else {
			if(sameCodes.size() == 1) {				
				return "" + sameCodes.get(0); 
			} else {
				System.out.println("Break");
			}
		}
		return "";
	}
	
	public ArrayList<Alarm> getChangedAlarms(){
		return changedAlarms;
	}
	
	public ArrayList<Alarm> getUnchangedAlarms(){
		return unchangedAlarms;
	}
}
