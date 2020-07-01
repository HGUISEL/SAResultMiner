package edu.handong.csee.isel.saresultminer.git;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class Log {
	String latestCommitId = "";
	
	public ArrayList<Commit> getAllCommitID(Git git) {
		ArrayList<Commit> commits = new ArrayList<>();

		try {
			Iterable<RevCommit> logs = git.log().call();
			
			int first = 0;
			for(RevCommit commit : logs) {
				if(first == 0) {
					latestCommitId += commit.getName();				
					first++;
				}
				Commit info = new Commit(commit.getName(), "" + commit.getCommitTime());
				commits.add(info);								
			}
			
			//first commit in index 0
			Collections.reverse(commits);
			
		
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commits;
	}
	
	public String getLatestCommitId() {
		return latestCommitId;
	}
}
