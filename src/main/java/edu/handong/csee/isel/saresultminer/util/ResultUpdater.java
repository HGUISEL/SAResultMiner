package edu.handong.csee.isel.saresultminer.util;

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
							} else {
							//line is deleted
							alarm.setLineNum("-1");
							alarm.setCode(change.getChangedCode());
							changedAlarms.add(alarm);
							}
							break;
						}
						else if(!alarm.getLineNum().equals(classifiedAlarm.getLineNum())) {
							classifiedAlarm.setDetectionIDInResult(alarm.getDetectionIDInResult());
							changedAlarms.add(classifiedAlarm);
							break;
						}
					}
					cnt ++;
				}
				if(changes.size() == cnt)
					unchangedAlarms.add(alarm);				
			}
		}
	}
	
	private Alarm classifyAlarms(Alarm alarm, ChangeInfo change) {		
		int alarmLine = Integer.parseInt(alarm.getLineNum().trim());
		
		if(change.getNewStart() == 0 && change.getNewRange() == 0) {
			//file is deleted
			return null;
		}
		
		if(alarmLine <= change.getOldStart()) {			
				return alarm;						
		} 
		else if(alarmLine >= change.getOldEnd()) {
			int changedLine = alarmLine + change.getNewRange() - change.getOldRange();
			alarm.setLineNum("" + changedLine);
			return alarm;
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
	
	public ArrayList<Alarm> getChangedAlarms(){
		return changedAlarms;
	}
	
	public ArrayList<Alarm> getUnchangedAlarms(){
		return unchangedAlarms;
	}
}
