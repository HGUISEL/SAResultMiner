package edu.handong.csee.isel.saresultminer.util;

import java.util.ArrayList;

import edu.handong.csee.isel.saresultminer.git.ChangeInfo;
import edu.handong.csee.isel.saresultminer.pmd.Alarm;

public class Comparator {
	public ArrayList<Alarm> compareDir(ArrayList<Alarm> inResult, ArrayList<Alarm> inCurrent, ArrayList<ChangeInfo> changes){
		if(inResult.size() == 0) {
			return inCurrent;
		} else {
			for(Alarm curTemp : inCurrent) {
				for(Alarm resultTemp : inResult) {
					if(curTemp.getDir().equals(resultTemp.getDir()) ) {
						
					}
				}				
			}
		}
		
	}
}
