package edu.handong.csee.isel.saresultminer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
	public String readInput(String path) {
		File f = new File(path);
		try {
			FileReader fReader =new FileReader(f);
			BufferedReader fBufReader = new BufferedReader(fReader);
			String str = "";
			
			while((str = fBufReader.readLine()) != null) {
				fBufReader.close();
				return str;
			}
			fBufReader.close();
		} 
		catch (IOException e) {
				e.printStackTrace();
		}
		return "WRONG";
	}
}