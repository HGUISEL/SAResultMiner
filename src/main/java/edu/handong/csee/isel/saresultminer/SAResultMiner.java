package edu.handong.csee.isel.saresultminer;

import java.util.ArrayList;

import org.eclipse.jgit.api.Git;

import edu.handong.csee.isel.saresultminer.git.Checkout;
import edu.handong.csee.isel.saresultminer.git.Clone;
import edu.handong.csee.isel.saresultminer.git.Log;
import edu.handong.csee.isel.saresultminer.util.Reader;

public class SAResultMiner {	
	public void run(String input) {
		String targetGitAddress = "";
		Reader reader = new Reader();
		Clone gitClone = new Clone();
		Log gitLog = new Log();
		Checkout gitCheckout = new Checkout();
		
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
		
		commitIDList.addAll(gitLog.getAllCommitID(git));
		
		/*
		 * @param git: a cloned git repository 		 
		 * @param commitID: Commit ID from List of all commit ID
		 * @param cnt: setting first, second, third... commit
		 */
	
		gitCheckout.checkout(git, commitIDList.get(0), 0);
		
		//apply pmd to init version
		
		//repeat{
		//checkout current +1
		
		//diff & get list of files which were changed
		
		//apply pmd to changed files
		
		//report comparison
		
		//write updated pm report and its codes
		
		//updated modified report contents	
		//}
			
	}	
}
