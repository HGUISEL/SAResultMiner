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
		
		//read input
		targetGitAddress = reader.readInput(input);
		//readInput test
		System.out.println("INFO: Target Project is " + targetGitAddress);		
		
		//git clone
		git = gitClone.clone(targetGitAddress);
		
		//get all commit id and latest commit id
		gitCheckout.checkoutToMaster(git);
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
		pmd.execute(rule, commits.get(0).getID(), gitClone.getClonedPath(), 0);
		
		//read first pmd report
		alarms.addAll(reader.readReportFile(pmd.getReportPath()));
		
		//write result form and first detection
		//-Detection ID +Latest Commit ID +PMD Version +Rule Name +File Path +VIC ID +VIC Date +VIC Line Num. -Latest Detection Commit ID -LDC LineNum -LDC Date -VFC ID -VFC Date -VFC Line Num. -Fixed Period(day) +Original Code -Fix Code -Really Fixed?
		writer.initResult(alarms, latestCommitID, pmdVersion, rule, commits.get(0).getID(), commits.get(0).getTime());
		
		//get all commit size for repeating
		int logSize = commits.size();
		
		//repeat until checking all commits
		for(int i = 1; i < logSize; i ++) {			
			alarmsInResult.addAll(reader.readResult(writer.getResult()));
			
			//checkout current +1
			gitCheckout.checkoutToMaster(git);
			gitCheckout.checkout(git, commits.get(i).getID(), i);			
			
			//1. find there are intersections between chagnedFiles and PMD reports			
			//1-1. find their directory and changed info
			//diff: get code of files which were changed
			try {
				changeInfo = gitDiff.diffCommit(git, commits.get(i).getID(), gitClone.getProjectName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//1-2. update original result line num and get changed alarms and unchanged alarms		
			resultUpdater.updateResultLineNum(alarmsInResult, changeInfo);
			alarmsInResult.clear();
			ArrayList<Alarm> changedAlarms = resultUpdater.getChangedAlarms();
			ArrayList<Alarm> unchangedAlarms = resultUpdater.getUnchangedAlarms();
			
			for(Alarm alarm : changedAlarms) {				
				System.out.println("********** Changed ************");
				System.out.println(alarm.getCode());
				System.out.println(alarm.getLineNum());				
			}
			
			for(Alarm alarm : unchangedAlarms) {				
				System.out.println("********** unchanged ************");				
				System.out.println(alarm.getCode());
				System.out.println(alarm.getLineNum());				
			}
			
			//2-1. if in intersections, check pmd report after changed
			//diff: get list of files which were changed
			String changedFiles = gitDiff.getChangedFilesList(git, gitClone.getClonedPath());			
			//write a file which contains a comma delimited changed files list
			String changedFilesListPath = writer.writeChangedFiles(changedFiles, commits.get(i).getID(), i);
						
			//apply pmd to changed files						
			pmd.executeToChangedFiles(commits.get(i).getID(), changedFilesListPath, i);						
			alarms = new ArrayList<Alarm>();
			alarms.addAll(reader.readReportFile(pmd.getReportPath()));
			//compare alarms and alarmsInResult which contains updated line Num
			//2-2. if pmd alarm is existing, newly generated
//			comparator.compareReports(alarms, reader.readReportFile(pmd.getReportPath()));
			//2-2-1. compare alarms between resultAlarms and alarms
			//2-2-2. add to result alarms
			
			//2-3 if alarm is disapeared, violation fixed
			//2-3-1. compare alarms between resultAlarms and alarms
			//2-3-2. add to fixed alarms
			
			//3. if there are no intersections between chagned files and PMD reports, it is maintained or newly generated.
			//3-1. compare alarms between resultAlarms and alarms
			//3-2. add to remain alarms									
			
			//write updated pmd report and its codes
//			writer.appendResult();					
		}								
	}	
}
