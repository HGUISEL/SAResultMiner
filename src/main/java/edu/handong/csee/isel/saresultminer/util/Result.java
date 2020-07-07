package edu.handong.csee.isel.saresultminer.util;

public class Result {
	//"Detection ID", "Latest Commit ID", "PMD Version", "Rule Name", "File Path", 
	//"Violation Introducing Commit ID", "VIC Date", "VIC Line Num.", 
	//"Latest Detection Commit ID", "LDC ID Date", "LDC Line Num.",
	//"Violation Fixed Commit ID", "VFC Date", "VFC Line Num.", "Fixed Period(day)", 
	//"Original Code", "Fixed Code", "Really Fixed?"
	int detectionID = 0;
	String LCID = "";
	String PMDVer = "";
	String ruleName = "";
	String filePath = "";
	String VICID = "";
	String VICDate = "";
	String VICLineNum = "";
	String LDCID = "";
	String LDCDate = "";
	String LDCLineNum = "";
	String VFCID = "";
	String VFCDate = "";
	String VFCLineNum = "";
	String fixedPeriod = "";
	String originCode = "";
	String fixedCode = "";		
	
	//init
	public Result(int detectionID, String LCID, String PMDVer, String ruleName, String filePath, String VICID, String VICDate, String VICLineNum, String originCode) {
		this.detectionID = detectionID;
		this.LCID = LCID;
		this.PMDVer = PMDVer;
		this.ruleName = ruleName;
		this.filePath = filePath;
		this.VICID = VICID;
		this.VICDate = VICDate;
		this.VICLineNum = VICLineNum;
		this.originCode = originCode;
	}
	
	//getters
	public int getDetectionID() {
		return detectionID;
	}

	public String getLCID() {
		return LCID;
	}

	public String getPMDVer() {
		return PMDVer;
	}

	public String getRuleName() {
		return ruleName;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getVICID() {
		return VICID;
	}

	public String getVICDate() {
		return VICDate;
	}

	public String getVICLineNum() {
		return VICLineNum;
	}

	public String getLDCID() {
		return LDCID;
	}
	
	public void setLDCID(String LDCID) {
		this.LDCID = LDCID;
	}

	public String getLDCDate() {
		return LDCDate;
	}
	
	public void setLDCDate(String LDCDate) {
		this.LDCDate = LDCDate;
	}

	public String getLDCLineNum() {
		return LDCLineNum;
	}

	public void setLDCLineNum(String lDCLineNum) {
		LDCLineNum = lDCLineNum;
	}		
	
	public String getVFCID() {
		return VFCID;
	}
	
	public void setVFCID(String vFCID) {
		VFCID = vFCID;
	}

	public String getVFCDate() {
		return VFCDate;
	}
	
	public void setVFCDate(String vFCDate) {
		VFCDate = vFCDate;
	}

	public String getVFCLineNum() {
		return VFCLineNum;
	}
	
	public void setVFCLineNum(String vFCLineNum) {
		VFCLineNum = vFCLineNum;
	}

	public String getFixedPeriod() {
		return fixedPeriod;
	}
	
	public void setFixedPeriod(String fixedPeriod) {
		this.fixedPeriod = fixedPeriod;
	}

	public String getOriginCode() {
		return originCode;
	}

	public String getFixedCode() {
		return fixedCode;
	}
	
	public void setFixedCode(String fixedCode) {
		this.fixedCode = fixedCode;
	}

}
