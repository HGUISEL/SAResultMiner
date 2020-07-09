package edu.handong.csee.isel.saresultminer.pmd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import edu.handong.csee.isel.saresultminer.util.Reader;
import edu.handong.csee.isel.saresultminer.util.Writer;

public class PMD {	
	String pmdCmd = "";
	String reportPath = "";
	
	public PMD(String pmdCmd) {
		this.pmdCmd = pmdCmd;
	}
	
	public void execute(String rule, String commitID, String dirPath, int cnt, String projectName) {		
		File newDir = new File("./PMDReports/" + projectName + File.separator);
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
		cmdLine.addArgument("./PMDReports/" + projectName + File.separator + cnt + "_" + commitID+".csv");
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
		reportPath = "./PMDReports/" + projectName + File.separator + cnt + "_" + commitID + ".csv";
		System.out.println("INFO: PMD Report Is Generated Commit ID: " + commitID + "(" + (end-start)/1000 + " sec.)");
	}
	
	public void executeToChangedFiles(String commitID, String filePaths, int cnt, String projectName) {		
		File newDir = new File("./PMDReports");
		if(!newDir.exists()) {
			newDir.mkdir();
		}
		
		Writer writer = new Writer();
		Reader reader = new Reader();
		String changedFileList = reader.readChagnedFileList(filePaths);
		if(changedFileList.equals("Empty")) {
			writer.writeEmptyCSVFile("./PMDReports/"+projectName+ File.separator + cnt + "_" + commitID+".csv");
			reportPath = "./PMDReports/"+ projectName + File.separator + cnt + "_" + commitID+ ".csv";
			return;
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
		cmdLine.addArgument("./PMDReports/"+projectName+ File.separator + cnt + "_" + commitID+".csv");
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
		reportPath = "./PMDReports/"+ projectName + File.separator + cnt + "_" + commitID+ ".csv";
		System.out.println("INFO: PMD Report Is Generated Commit ID: " + commitID + "(" + (end-start)/1000 + " sec.)");
	}
	
	public String getReportPath() {
		return reportPath;
	}
}
