package edu.handong.csee.isel.saresultminer.git;

import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class ChangeInfo {
	Alarm resultAlarm;
	Alarm currentAlarm;
	
	public ChangeInfo(Alarm result, Alarm current) {
		resultAlarm = result;
		currentAlarm = current;
	}
}
