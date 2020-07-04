package edu.handong.csee.isel.saresultminer.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Clone {
	String projectName = "";
	String clonedPath = "";
	
	public Git clone(String gitAddress) {
		getProjectName(gitAddress);
		Git git = null;
		
		File newDir = new File("./TargetProjects");
		if(!newDir.exists()) {
			newDir.mkdir();
		}
		
		try {
			System.out.println("INFO: Clone Start");
			long start = System.currentTimeMillis();
			File clonePath = new File(newDir.toString() + File.separator + projectName);
			if(!clonePath.exists()) {
				git = Git.cloneRepository().setURI(gitAddress).setDirectory(clonePath).call();
			} else {
				git = Git.open(clonePath);				 
				System.out.println("INFO: Clone Skip, The Target Project was already cloned");
			}
			long end = System.currentTimeMillis();
			System.out.println("INFO: Clone Finished" + "(" + (end-start)/1000 + " sec.)");
			clonedPath = clonePath.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}		
	
		return git;
	}
	
	private void getProjectName(String gitAddress) {
		String[] addressSplit = gitAddress.split("/");
		String gitName = addressSplit[addressSplit.length-1];
		projectName = gitName.split("\\.")[0];				
	}
	
	public String getClonedPath() {
		return clonedPath;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
}