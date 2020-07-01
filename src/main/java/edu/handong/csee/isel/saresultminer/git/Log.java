package edu.handong.csee.isel.saresultminer.git;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class Log {
	String latestCommitId = "";
	
	public ArrayList<String> getAllCommitID(Git git) {
		ArrayList<String> commitIds = new ArrayList<>();

		try {
			Iterable<RevCommit> logs = git.log().call();
			
			int first = 0;
			for(RevCommit commit : logs) {
				if(first == 0) {
					latestCommitId += commit.getName();				
					first++;
				}
				commitIds.add(commit.getName());								
			}
			
			//first commit in index 0
			Collections.reverse(commitIds);
			
		
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commitIds;
	}
	
	public String getLatestCommitId() {
		return latestCommitId;
	}
}
