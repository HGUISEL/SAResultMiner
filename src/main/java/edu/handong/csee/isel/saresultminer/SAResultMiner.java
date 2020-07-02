package edu.handong.csee.isel.saresultminer;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;

import edu.handong.csee.isel.saresultminer.git.Checkout;
import edu.handong.csee.isel.saresultminer.git.Clone;
import edu.handong.csee.isel.saresultminer.git.Commit;
import edu.handong.csee.isel.saresultminer.git.Diff;
import edu.handong.csee.isel.saresultminer.git.Log;
import edu.handong.csee.isel.saresultminer.pmd.Alarm;
import edu.handong.csee.isel.saresultminer.pmd.PMD;
import edu.handong.csee.isel.saresultminer.util.Comparator;
import edu.handong.csee.isel.saresultminer.util.Reader;
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
		for(int i = 1; i <= 10; i ++) {
			//checkout current +1
			gitCheckout.checkoutToMaster(git);
			gitCheckout.checkout(git, commits.get(i).getID(), i);
			
			//diff: get list of files which were changed
			String changedFiles = gitDiff.getChangedFilesList(git, gitClone.getClonedPath());
			
			//diff: get code of files which were changed
			try {
				gitDiff.diffCommit(git, commits.get(i).getID());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//write a file which contains a comma delimited changed files list
			String changedFilesListPath = writer.writeChangedFiles(changedFiles, commits.get(i).getID());
						
			//apply pmd to changed files			
			pmd.executeToChangedFiles(commits.get(i).getID(), changedFilesListPath, i);
			alarms = new ArrayList<Alarm>();
			alarms.addAll(reader.readReportFile(pmd.getReportPath()));			
						
			//report comparison	with a result file{
			//read result's alarm [E] [H]
			ArrayList<Alarm> alarmsInResult = new ArrayList<>();
			alarmsInResult.addAll(reader.readResult());
			
			//first, check directory			
			alarmsInResult = comparator.compareDir(alarmsInResult, alarms);
			
			
			//second, check added codes and calculate line num						
			alarmsInResult = comparator.compareCode(alarmsInResult, alarms);				
			if(isMoved) {
				writer.appendResult();
			}
			
			//third, check line num}
			alarmsInResult = comparator.compareLine(alarmsInResult, alarms);			
			
			//write updated pmd report and its codes
			writer.appendResult();					
		}								
	}	
}
