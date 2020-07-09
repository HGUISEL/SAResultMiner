package edu.handong.csee.isel.saresultminer.git;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class Log {
	String latestCommitId = "";
	
	public ArrayList<Commit> getAllCommitID(Git git) {
		ArrayList<Commit> commits = new ArrayList<>();

		try {
			Iterable<RevCommit> logs1 = git.log().call();
			Iterable<RevCommit> logs2 = git.log().call();
			
			int first = 0;			
			for(RevCommit commit : logs1) {				
				if(first == 0) {
					latestCommitId += commit.getName();				
					first++;
				}
				else if(commit.getParentCount() == 0) {
					continue;
				}
				Commit info = new Commit(commit.getName(), "" + commit.getCommitTime());
				commits.add(info);								
			}
			
			RevCommit firstCommit = getLastElement(logs2);
			commits.add(new Commit(firstCommit.getName(), "" + firstCommit.getCommitTime()));
			
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
	
	private RevCommit getLastElement(Iterable<RevCommit> elements) {
		final Iterator<RevCommit> itr = elements.iterator();
	    RevCommit lastElement = itr.next();
	    while(itr.hasNext()) {
	        lastElement = itr.next();
	    }
	    return lastElement;
	}
	
	public String getLatestCommitId() {
		return latestCommitId;
	}
}
