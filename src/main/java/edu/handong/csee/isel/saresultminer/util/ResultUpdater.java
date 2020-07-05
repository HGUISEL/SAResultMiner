package edu.handong.csee.isel.saresultminer.util;

import java.util.ArrayList;

import edu.handong.csee.isel.saresultminer.git.ChangeInfo;
import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class ResultUpdater {
	public ArrayList<Alarm> updateResultLineNum(ArrayList<Alarm> alarms, ArrayList<ChangeInfo> changes) {
		ArrayList<Alarm> updatedResult = new ArrayList<>();
		if(alarms.size() == 0) {
			return alarms;
		} else {
			for(int i = 0 ; i < alarms.size(); i ++) {
				Alarm tempAlarm = alarms.get(i);
				for(ChangeInfo change : changes) {
					if(tempAlarm.getDir().trim().equals(change.getDir())) {
						updateChangedLine(tempAlarm, change);
					}
				}
			}
		}
		
		return updatedResult;
	}
	
	private void updateChangedLine(Alarm alarm, ChangeInfo change) {
		int alarmLine = Integer.parseInt(alarm.getLineNum().trim());
		if(alarmLine <= change.getOldStart()) {
			return;
		} else if(alarmLine >= change.getOldEnd()) {
			int changedLine = alarmLine + change.getNewRange() - change.getOldRange();
			alarm.setLineNum("" + changedLine);
		} else if(change.getOldStart() <= alarmLine && alarmLine <= change.getOldEnd()) {
			
		}
	}
}
