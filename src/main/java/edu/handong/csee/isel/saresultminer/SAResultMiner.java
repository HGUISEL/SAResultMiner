package edu.handong.csee.isel.saresultminer;

import java.util.ArrayList;

import org.eclipse.jgit.api.Git;

import edu.handong.csee.isel.saresultminer.git.Checkout;
import edu.handong.csee.isel.saresultminer.git.Clone;
import edu.handong.csee.isel.saresultminer.git.Diff;
import edu.handong.csee.isel.saresultminer.git.Log;
import edu.handong.csee.isel.saresultminer.pmd.PMD;
import edu.handong.csee.isel.saresultminer.util.Reader;
import edu.handong.csee.isel.saresultminer.util.Writer;

public class SAResultMiner {	
	public void run(String input) {
		String targetGitAddress = "";
		Reader reader = new Reader();
		Clone gitClone = new Clone();
		Log gitLog = new Log();
		Checkout gitCheckout = new Checkout();
		//@param pmd command location
		PMD pmd = new PMD("/Users/yoonho/Documents/ISEL/SAResultMiner/pmd-bin-6.25.0/bin/run.sh");
		Diff gitDiff = new Diff();
		Writer writer = new Writer();
		
		Git git;
		ArrayList<String> commitIDList = new ArrayList<>();
		
		//read input
		targetGitAddress = reader.readInput(input);
		//readInput test
		System.out.println(targetGitAddress);		
		
		/*
		 * Member of gitClone
		 * 1) cloned path
		 */		 		
		
		git = gitClone.clone(targetGitAddress);
		
		/*
		 * Member of gitLogs		 
		 * 1) latest commit id
		 */		
		gitCheckout.checkoutToMaster(git);
		commitIDList.addAll(gitLog.getAllCommitID(git));
		String latestCommitID = gitLog.getLatestCommitId();
		
		/*
		 * checkout to first version
		 * @param git: a cloned git repository 		 
		 * @param commitID: Commit ID from List of all commit ID
		 * @param cnt: setting first, second, third... commit
		 */		
		gitCheckout.checkout(git, commitIDList.get(0), 0);
		
		//apply pmd to init version
		pmd.execute(commitIDList.get(0), gitClone.getClonedPath(), 0);
		
		int logSize = commitIDList.size();
		
		//repeat until checking all commits
		for(int i = 1; i <= 1; i ++) {
			//checkout current +1
			gitCheckout.checkoutToMaster(git);
			gitCheckout.checkout(git, commitIDList.get(i), i);
			
			//diff & get list of files which were changed
			String changedFiles = gitDiff.diff(git, gitClone.getClonedPath());
			
			//write a file which contains chagned a comma delimited file list
			String changedFilesListPath = writer.writeChangedFiles(changedFiles, commitIDList.get(i));
			
			//apply pmd to changed files
			pmd.executeToChangedFiles(commitIDList.get(i), changedFilesListPath, i);
			
			//report comparison
			
			//write updated pmd report and its codes
			
			//updated modified report contents
		}								
	}	
}
