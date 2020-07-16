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
				String date[] = commit.getAuthorIdent().getWhen().toString().split(" ");
				String dateString = "";
				if(Integer.parseInt(date[2])/10 == 0) {
					dateString = date[5] + "-" + getMonth(date[1]) + "-" + date[2];
				} else {
					dateString = date[5] + "-" + getMonth(date[1]) + "-" + date[2];
				}
				Commit info = new Commit(commit.getName(), "" + dateString);
				commits.add(info);								
			}
			
			RevCommit firstCommit = getLastElement(logs2);
			commits.add(new Commit(firstCommit.getName(), "" + firstCommit.getAuthorIdent().getWhen()));
			
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
	
	private String getMonth(String letterMonth) {
		if(letterMonth.equals("Jan")) return "01";
		else if(letterMonth.equals("Feb")) return "02";
		else if(letterMonth.equals("Mar")) return "03";
		else if(letterMonth.equals("Apr")) return "04";
		else if(letterMonth.equals("May")) return "05";
		else if(letterMonth.equals("Jun")) return "06";
		else if(letterMonth.equals("Jul")) return "07";
		else if(letterMonth.equals("Aug")) return "08";
		else if(letterMonth.equals("Sep")) return "09";
		else if(letterMonth.equals("Oct")) return "10";
		else if(letterMonth.equals("Nov")) return "11";
		else return "12";
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
