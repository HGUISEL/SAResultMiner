package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	String path = "";
	
	public String writeChangedFiles(String changedFiles, String commitID) {
		File newDir = new File("./ChangedFilesList");
		if(!newDir.exists()) {
			newDir.mkdir();
		}
		
		BufferedWriter bw = null;
		System.out.println("INFO: Start to Write Changed Files List");
		long start = System.currentTimeMillis();
		try {			    
			File file = new File("./ChangedFilesList/" + commitID + ".txt" );
			path = file.toString();
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
		return path;
	}
}
