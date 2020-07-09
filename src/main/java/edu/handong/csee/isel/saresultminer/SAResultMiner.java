package edu.handong.csee.isel.saresultminer;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;

import edu.handong.csee.isel.saresultminer.git.ChangeInfo;
import edu.handong.csee.isel.saresultminer.git.Checkout;
import edu.handong.csee.isel.saresultminer.git.Clone;
import edu.handong.csee.isel.saresultminer.git.Commit;
import edu.handong.csee.isel.saresultminer.git.Diff;
import edu.handong.csee.isel.saresultminer.git.Log;
import edu.handong.csee.isel.saresultminer.pmd.Alarm;
import edu.handong.csee.isel.saresultminer.pmd.PMD;
import edu.handong.csee.isel.saresultminer.util.Comparator;
import edu.handong.csee.isel.saresultminer.util.Reader;
import edu.handong.csee.isel.saresultminer.util.Result;
import edu.handong.csee.isel.saresultminer.util.ResultUpdater;
import edu.handong.csee.isel.saresultminer.util.Writer;

public class SAResultMiner {			
	public void run(String input) {	
		//instances related with git 
		Clone gitClone = new Clone();
		Log gitLog = new Log();
		Checkout gitCheckout = new Checkout();
		Diff gitDiff = new Diff();		
		Git git;
		String targetGitAddress = "";
		ArrayList<Commit> commits = new ArrayList<>();
		ArrayList<ChangeInfo> changeInfo = new ArrayList<>();
		ArrayList<Alarm> alarmsInResult = new ArrayList<>();
		
		//pmd instance
		//@param pmd command location
		PMD pmd = new PMD("./pmd-bin-6.25.0/bin/run.sh");
		String pmdVersion = "6.25";
		String rule = "category/java/errorprone.xml/NullAssignment";
		ArrayList<Alarm> alarms = new ArrayList<>();
		
		//utils instances
		Writer writer = new Writer();
		Reader reader = new Reader();
		Comparator comparator = new Comparator();
		ResultUpdater resultUpdater = new ResultUpdater();
		ArrayList<Result> results = new ArrayList<>();
		int detectionID = 0;
		
		long start = System.currentTimeMillis();
		
		//read input
		targetGitAddress = reader.readInput(input);
		//readInput test
		System.out.println("INFO: Target Project is " + targetGitAddress);		
		
		//git clone
		git = gitClone.clone(targetGitAddress);
		
		//get all commit id and latest commit id
		commits.addAll(gitLog.getAllCommitID(git));
		String latestCommitID = gitLog.getLatestCommitId();
		/*
		 * checkout to first version
		 * @param git: a cloned git repository 		 
		 * @param commitID: Commit ID from List of all commit ID
		 * @param cnt: setting first, second, third... commit
		 */		
		gitCheckout.checkout(git, commits.get(0).getID(), 0);
		
		/*
		 * apply pmd to init version 
		 * @param rule
		 * @param commitID
		 * @param clonedPath: pmd's target direcotry
		 * @param cnt: setting first, second, third... commit
		 */
		pmd.execute(rule, commits.get(0).getID(), gitClone.getClonedPath(), 0, gitClone.getProjectName());
		
		//read first pmd report
		alarms.addAll(reader.readReportFile(pmd.getReportPath()));
		for(Alarm alarm : alarms) {
			detectionID++;
			results.add(new Result(detectionID, latestCommitID, pmdVersion, rule, alarm.getDir(), commits.get(0).getID(), commits.get(0).getTime(), alarm.getLineNum(), alarm.getCode()));
		}
		
		//write result form and first detection		
		writer.initResult(results, gitClone.getProjectName());
		
		//get all commit size for repeating
		int logSize = commits.size();
		
		//repeat until checking all commits
		for(int i = 1; i < logSize; i ++) {			
			for(Result result: results) {
				alarmsInResult.add(new Alarm(result));
			}			
			
			//checkout current +1			
			gitCheckout.checkout(git, commits.get(i).getID(), i);			
			
			//1. find there are intersections between chagnedFiles and PMD reports			
			//1-1. find their directory and changed info
			//diff: get code of files which were changed
			if(commits.get(i).getID().equals("314ff2593c34f08a3aa9307b1d42c718869a5b24")) {
				System.out.println("break point");
			}
			try {				
				changeInfo = gitDiff.diffCommit(git, commits.get(i).getID(), commits.get(i-1).getID(), gitClone.getProjectName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//***** not have been tested 1-2. *****
			//1-2. update original result line num and get changed alarms and unchanged alarms		
			resultUpdater.updateResultLineNum(alarmsInResult, changeInfo);
			alarmsInResult.clear();
			ArrayList<Alarm> changedAlarms = resultUpdater.getChangedAlarms();
			ArrayList<Alarm> unchangedAlarms = resultUpdater.getUnchangedAlarms();						
			
			//2-1. if in intersections, check pmd report after changed
			//diff: get list of files which were changed
			String changedFiles = "";
			for(ChangeInfo change : changeInfo) {
				if(change.getChangeType().equals("D")) continue;
				if(change.getDir().equals("")) continue;
				if(changedFiles.contains(change.getDir())) continue;
				changedFiles += change.getDir() + ",";
			}
			if(changedFiles.contains("./TargetProjects/camel-k-runtime/camel-knative/src/main/java/org/apache/camel/component/knative/KnativeEndpoint.java")) {
				System.out.println("break point");
			}
			//write a file which contains a comma delimited changed files list
			String changedFilesListPath = writer.writeChangedFiles(changedFiles, commits.get(i).getID(), i, gitClone.getProjectName());
						
			//apply pmd to changed files						
			pmd.executeToChangedFiles(commits.get(i).getID(), changedFilesListPath, i, gitClone.getProjectName());
			alarms = new ArrayList<Alarm>();
			alarms.addAll(reader.readReportFile(pmd.getReportPath()));
			
			//compare alarms and alarmsInResult which contains updated line Num
			//2-2. if pmd alarm is existing, newly generated			
			comparator.compareAlarms(alarms, changedAlarms, unchangedAlarms);			
			
			//fixed  
			//"Violation Fixed Commit ID", "VFC Date", "VFC Line Num.", "Fixed Period(day)", 
			//"Fixed Code"	
			for(Alarm fixedAlarm : comparator.getFixedAlarms()) {
				for(int j = 0 ; j < results.size(); j ++ ) {
					Result tempResult = results.get(j);
					if(tempResult.getDetectionID() == fixedAlarm.getDetectionIDInResult()) {
						tempResult.setVFCID(commits.get(i).getID());
						tempResult.setVFCLineNum(fixedAlarm.getLineNum());
						tempResult.setVFCDate(commits.get(i).getTime());
						tempResult.setFixedPeriod(tempResult.getVFCDate()+"/"+tempResult.getVICDate());
						tempResult.setFixedCode(fixedAlarm.getCode());	
						results.set(j, tempResult);
						break;
					}
				}
			}			
			
			//maintained  
			//"Latest Detection Commit ID", "LDC ID Date", "LDC Line Num.", 
			for(Alarm maintainedAlarm : comparator.getMaintainedAlarms()) {
				for(int j = 0 ; j < results.size(); j ++ ) {
					Result tempResult = results.get(j);
					if(tempResult.getDetectionID() == maintainedAlarm.getDetectionIDInResult()) {
						tempResult.setLDCID(commits.get(i).getID());
						tempResult.setLDCLineNum(maintainedAlarm.getLineNum());
						tempResult.setLDCDate(commits.get(i).getTime());	
						results.set(j, tempResult);
						break;
					}
				}
			}	
			
			//newly generated
			//"Detection ID", "Latest Commit ID", "PMD Version", "Rule Name", "File Path", 
			//"Violation Introducing Commit ID", "VIC Date", "VIC Line Num.",  
			//"Original Code"
			for(Alarm newAlarm : comparator.getNewGeneratedAlarms()) {	
				detectionID++;				
				results.add(new Result(detectionID, latestCommitID, pmdVersion, rule, newAlarm.getDir(), commits.get(i).getID(), commits.get(i).getTime(), newAlarm.getLineNum(), newAlarm.getCode()));	
			}
			
			comparator.init();
			resultUpdater.init();
			alarms.clear();
			
			//write updated pmd report and its codes
			writer.writeResult(results, gitClone.getProjectName());
		}					
		long end = System.currentTimeMillis();
		System.out.println("FINAL RESULT IS GENERATED!! (TOTAL: " + (end-start)/1000 + " sec.)" );
	}			
}
