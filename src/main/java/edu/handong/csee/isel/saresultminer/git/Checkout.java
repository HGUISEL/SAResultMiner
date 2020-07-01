package edu.handong.csee.isel.saresultminer.git;

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
		try {
			git.checkout().setName(commitID).call();
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
		if(cnt == 0) {
			System.out.println("INFO: Checkout to Initial Commit Finished" + "(" + (end-start)/1000 + " sec.)" + " Commit ID: " + commitID);
		} else {
			System.out.println("INFO: Checkout Finished" + "(" + (end-start)/1000 + " sec.)" + " Commit ID: " + commitID);
		}
	}
	
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
