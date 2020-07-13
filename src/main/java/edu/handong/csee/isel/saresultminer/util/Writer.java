package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Writer {
	String changedFilesPath = "";
	String resultPath = "./Result.csv";
	int cnt;
	
	public void writeEmptyCSVFile(String path) {
		String fileName = path;
		System.out.println("INFO: There's no java file Change. Make Empty Report.");
		long start = System.currentTimeMillis();
		try(
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
			) {														
			writer.flush();
			writer.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println("INFO: Finish to Make Empty Report (" + (end - start)/1000 + " sec.)");
	}
	
	public String writeChangedFiles(String changedFiles, String commitID, int cnt, String projectName) {
		File newDir = new File("." + File.separator+"ChangedFilesList"+File.separator+ projectName + File.separator);
		while(!newDir.exists()) {
			newDir.mkdirs();
		}
		
		BufferedWriter bw = null;
		System.out.println("INFO: Start to Write Changed Files List");
		long start = System.currentTimeMillis();
		try {			    
			File file = new File("."+ File.separator+"ChangedFilesList"+File.separator+ projectName + File.separator + cnt +"_" +commitID + ".txt" );
			changedFilesPath = file.toString();
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(changedFiles);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally { 
			try{
				if(bw!=null)
					bw.close();
			} catch(Exception ex) {
				System.out.println("Error: Failed to Closing The BufferedWriter"+ex);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("INFO: Finish to Write Changed Files List (" + (end-start)/1000 + " sec.)" );
		return changedFilesPath;
	}
	
	public void initResult(ArrayList<Result> results, String projectName) {
		String fileName = "./" + projectName + "_Result.csv";
		System.out.println("INFO: Start to Initialize Result File");
		long start = System.currentTimeMillis();
		try(
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
									.withHeader("Detection ID", "Latest Commit ID", "PMD Version", "Rule Name", "File Path", "Violation Introducing Commit ID", "VIC Date", "VIC Line Num.", "Latest Detection Commit ID", "LDC ID Date", "LDC Line Num.","Violation Fixed Commit ID", "VFC Date", "VFC Line Num.", "Fixed Period(day)", "Original Code", "Fixed Code", "Really Fixed?"));
			) {		
									
			for(Result result : results) {				
				csvPrinter.printRecord(result.getDetectionID(), result.getLCID(), result.getPMDVer(), result.getRuleName(), result.getFilePath(), result.getVICID(), result.getVICDate(), result.getVICLineNum(), "", "", "", "", "", "", "", result.getOriginCode(), "", "");				
			}
			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println("INFO: Finish to Initialize Result File (" + (end - start)/1000 + " sec.)");
	}
	
	public void writeResult(ArrayList<Result> results, String projectName, long time) {
		String fileName = "./" + projectName + "_Result.csv";
		System.out.println("INFO: Start to Initialize Result File");
		long start = System.currentTimeMillis();
		try(
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
									.withHeader("Detection ID", "Latest Commit ID", "PMD Version", "Rule Name", "File Path", "Violation Introducing Commit ID", "VIC Date", "VIC Line Num.", "Latest Detection Commit ID", "LDC ID Date", "LDC Line Num.","Violation Fixed Commit ID", "VFC Date", "VFC Line Num.", "Fixed Period(day)", "Original Code", "Fixed Code", "Really Fixed?", "Time"));
			) {		
									
			for(Result result : results) {										
				csvPrinter.printRecord(result.getDetectionID(), result.getLCID(), result.getPMDVer(), result.getRuleName(), result.getFilePath(), result.getVICID(), result.getVICDate(), result.getVICLineNum(), result.getLDCID(), result.getLDCDate(), result.getLDCLineNum(), result.getVFCID(), result.getVFCDate(), result.getVFCLineNum(), result.getFixedPeriod(), result.getOriginCode(), result.getFixedCode(), "", time);				
			}
			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println("INFO: Finish to Initialize Result File (" + (end - start)/1000 + " sec.)");
	}
	
	public String getResult() {
		return resultPath;
	}
}
