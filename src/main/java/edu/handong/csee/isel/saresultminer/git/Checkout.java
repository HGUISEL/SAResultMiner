package edu.handong.csee.isel.saresultminer.git;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

public class Checkout {
	public void checkout(Git git, String commitID, int cnt) {
		System.out.println("INFO: Checkout Start");
		long start = System.currentTimeMillis();
//		try {
		try {				
			CommandLine cmdLine = new CommandLine("git");
			cmdLine.addArgument("checkout");
			cmdLine.addArgument("-f");
			cmdLine.addArgument(commitID);
			cmdLine.addArgument("--quiet");
			DefaultExecutor executor = new DefaultExecutor();
			int[] exitValues = {0};
			executor.setExitValues(exitValues);
			ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
			executor.setWatchdog(watchdog);		
			int exitValue = executor.execute(cmdLine);		
			} catch (ExecuteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			git.checkout().setForceRefUpdate(true).setForced(true).setName(commitID).call();
//		} catch (RefAlreadyExistsException e) {
//			e.printStackTrace();
//		} catch (RefNotFoundException e) {
//			e.printStackTrace();
//		} catch (InvalidRefNameException e) {
//			e.printStackTrace();
//		} catch (CheckoutConflictException e) {
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			e.printStackTrace();
//		}
		long end = System.currentTimeMillis();
		if(cnt == 0) {
			System.out.println("INFO: Checkout to Initial Commit Finished" + "(" + (end-start)/1000 + " sec.)" + " Commit ID: " + commitID);
		} else {
			System.out.println("INFO: Checkout Finished" + "(" + (end-start)/1000 + " sec.)" + " Commit ID: " + commitID);
		}
	}
	@Deprecated
	public void checkoutToMaster(Git git) {
		System.out.println("INFO: Checkout to Master Start");
		long start = System.currentTimeMillis();
		try {
			git.checkout().setName("master").call();
		} catch (RefAlreadyExistsException e) {
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidRefNameException e) {
			e.printStackTrace();
		} catch (CheckoutConflictException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println("INFO: Checkout to Master Finished" + "(" + (end-start)/1000 + " sec.)");
		
	}
}
