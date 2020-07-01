package edu.handong.csee.isel.saresultminer.pmd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

public class PMD {	
	String pmdCmd = "";
	String reportPath = "";
	
	public PMD(String pmdCmd) {
		this.pmdCmd = pmdCmd;
	}
	
	public void execute(String rule, String commitID, String dirPath, int cnt) {		
		File newDir = new File("./PMDReports");
		if(!newDir.exists()) {
			newDir.mkdir();
		}
		System.out.println("INFO: PMD Start");
		long start = System.currentTimeMillis();
		try {				
		CommandLine cmdLine = new CommandLine(pmdCmd);
		cmdLine.addArgument("pmd");
		cmdLine.addArgument("-d");
		cmdLine.addArgument(dirPath);
		cmdLine.addArgument("-R");
		cmdLine.addArgument(rule);
		cmdLine.addArgument("-reportfile");
		cmdLine.addArgument("./PMDReports/"+ cnt + "_" + commitID+".csv");
		DefaultExecutor executor = new DefaultExecutor();
		int[] exitValues = {0, 1, 4};
		executor.setExitValues(exitValues);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);		
		int exitValue = executor.execute(cmdLine);		
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		reportPath = "./PMDReports/"+ cnt + "_" + commitID+ ".csv";
		System.out.println("INFO: PMD Report Is Generated Commit ID: " + commitID + "(" + (end-start)/1000 + " sec.)");
	}
	
	public void executeToChangedFiles(String commitID, String filePaths, int cnt) {		
		File newDir = new File("./PMDReports");
		if(!newDir.exists()) {
			newDir.mkdir();
		}
		System.out.println("INFO: PMD Start");
		long start = System.currentTimeMillis();
		try {				
		CommandLine cmdLine = new CommandLine(pmdCmd);
		cmdLine.addArgument("pmd");
		cmdLine.addArgument("-filelist");
		cmdLine.addArgument(filePaths);
		cmdLine.addArgument("-R");
		cmdLine.addArgument("category/java/errorprone.xml/NullAssignment");
		cmdLine.addArgument("-reportfile");
		cmdLine.addArgument("./PMDReports/"+ cnt + "_Changed_" + commitID+".csv");
		DefaultExecutor executor = new DefaultExecutor();
		int[] exitValues = {0, 1, 4};
		executor.setExitValues(exitValues);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);		
		int exitValue = executor.execute(cmdLine);		
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		reportPath = "./PMDReports/"+ cnt + "_" + commitID+ ".csv";
		System.out.println("INFO: PMD Report Is Generated Commit ID: " + commitID + "(" + (end-start)/1000 + " sec.)");
	}
	
	public String getReportPath() {
		return reportPath;
	}
}
