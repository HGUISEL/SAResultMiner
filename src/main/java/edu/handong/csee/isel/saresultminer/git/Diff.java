package edu.handong.csee.isel.saresultminer.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Diff {
	enum Status{
		idle, diff, lineNum, codes
	}
	
	String changedFiles = "";
	@Deprecated
	public String getChangedFilesList(Git git, String clonedPath) {		
		String changedFiles = "";
		try {
			ObjectReader reader = git.getRepository().newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			ObjectId oldTree = git.getRepository().resolve( "HEAD~1^{tree}" );
			oldTreeIter.reset( reader, oldTree );
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			ObjectId newTree;
			newTree = git.getRepository().resolve( "HEAD^{tree}" );
			newTreeIter.reset( reader, newTree );

			DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
			diffFormatter.setRepository( git.getRepository() );
			List<DiffEntry> entries = diffFormatter.scan( oldTreeIter, newTreeIter );
			int entrySize = entries.size();
			int cnt = 0;
			for( DiffEntry entry : entries ) {				
				String changedPath = entry.getNewPath();			
				cnt++;
			    if(changedPath.split("\\.").length > 1 && changedPath.split("\\.")[1].equals("java")) {				  
			    	if(cnt != entrySize)
			    		changedFiles += clonedPath + "/" + entry.getNewPath() + ",";
			    	else
			    		changedFiles += clonedPath + "/" + entry.getNewPath();
			    }
			}
		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(changedFiles);
		return changedFiles;
	}
	
	public ArrayList<ChangeInfo> diffCommit(Git git, String projectName) throws IOException {							    
	    String codeChange = getDiffOfCommit(git);
	    
//	    System.out.println(codeChange);
	    String[] codeChangeSplitByNewLine = codeChange.split("\n");	    
	    ArrayList<ChangeInfo> changeInfo = new ArrayList<>();
	    changeInfo.addAll(getChangeInfos(codeChangeSplitByNewLine, projectName));
	    
//	    for(ChangeInfo temp : changeInfo) {
//		    System.out.println(temp.getDir()+ " Old (Start: " + temp.getOldStart() + " End: " + temp.getOldEnd() + ")  New ( Start: " + temp.getNewStart() + " End: " + temp.getNewEnd() + "\n" + temp.getChangedCode());	    	
//	    }

	    return changeInfo;
	}
	
	private String getDiffOfCommit(Git git) throws IOException {
	    AbstractTreeIterator oldTreeIterator = getOldCanonicalTreeParser(git);
	    AbstractTreeIterator newTreeIterator = getNewCanonicalTreeParser(git);	    
	    OutputStream outputStream = new ByteArrayOutputStream();
	    try {	    	
			git.diff().setOldTree(oldTreeIterator).setNewTree(newTreeIterator).setOutputStream(outputStream).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	    
//	    try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
//	        formatter.setRepository(git.getRepository());
//	        formatter.format(oldTreeIterator, newTreeIterator);	        
//	    }
	    String diff = outputStream.toString();
	    return diff;
	}

	public RevCommit getPrevHash(RevCommit commit, Git git)  throws  IOException {

	    try (RevWalk walk = new RevWalk(git.getRepository())) {
	        walk.markStart(commit);	        
	        int count = 0;
	        for (RevCommit rev : walk) {
	            if (count == 1) {
	                return rev;
	            }
	            count++;
	        }
	        walk.dispose();
	    }
	    return null;
	}

	private AbstractTreeIterator getOldCanonicalTreeParser(Git git) throws IOException {
		ObjectReader reader = git.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		ObjectId oldTree = git.getRepository().resolve( "HEAD~1^{tree}" );
		oldTreeIter.reset( reader, oldTree );
		
		return oldTreeIter;
	}
	
	private AbstractTreeIterator getNewCanonicalTreeParser(Git git) throws IOException {
		ObjectReader reader = git.getRepository().newObjectReader();
		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		ObjectId newTree;
		newTree = git.getRepository().resolve( "HEAD^{tree}" );
		newTreeIter.reset( reader, newTree );
		
		return newTreeIter;
	}
	private ArrayList<ChangeInfo> getChangeInfos(String[] codeChangeSplitByNewLine, String projectName) {
		ArrayList<ChangeInfo> changeInfo = new ArrayList<>();
	    Status status = Status.idle;
	    int oldRange = 0, oldStart = 0, oldEnd=0, newStart=0, newRange=0, newEnd=0;
	    String code = "";
	    String dir = ""; 
	    String changeType = "";
    	int cnt = 0;
	    for(String change : codeChangeSplitByNewLine) {	    	
	    	if(status == Status.idle && change.startsWith("diff")) {	    			    		
	    		change = change.replaceAll("diff .+ b/","");	    	
	    		if(!change.contains(".java")) continue;
	    		dir = "./TargetProjects/" + projectName +"/"+ change;
	    		status = Status.diff;
	    	}
	    	else if(status == Status.diff && change.contains("@@")) {

	    		change = change.replaceAll("@@", "");
	    		
	    		String oldPart = change.split("\\+")[0];
	    		if(!oldPart.contains(",")) {
	    			oldStart = 0;
	    			oldRange = 0;
	    		} else {
	    			oldRange = Integer.parseInt(oldPart.split(",")[1].trim());
	    			oldStart = Integer.parseInt(oldPart.split(",")[0].replace("-", "").trim());
	    		}
	    		if(oldStart == 0 && oldRange == 0) oldEnd = 0;
	    		else oldEnd = oldStart + oldRange - 1;
	    		
	    		String newPart = change.split("\\+")[1];
	    		if(!newPart.contains(",")) {
	    			newStart = Integer.parseInt(newPart.split(",")[0].trim());
		    		newRange = 0;
	    		}else {
	    			newStart = Integer.parseInt(newPart.split(",")[0].trim());
	    			newRange = Integer.parseInt(newPart.split(",")[1].trim());
	    		}
	    		newEnd = newStart + newRange - 1;	    			    			    	
	    		
	    		status = Status.lineNum;
	    	}	    	
	    	else if((status == Status.lineNum || status == Status.codes) && (change.startsWith(" ") || change.startsWith("+") || change.startsWith("-"))) {
	    		code += change + "\n";
	    		if(codeChangeSplitByNewLine.length -1 == cnt) {
	    			changeInfo.add(new ChangeInfo(dir, changeType, oldStart, oldRange, oldEnd, newStart, newRange, newEnd, code));
		    		oldRange = 0; oldStart = 0; oldEnd=0; newStart=0; newRange=0; newEnd=0; code = ""; changeType = "";
	    		}
	    		status = Status.codes;
	    	}
	    	else if(status == Status.codes && change.startsWith("diff")) {
	    		changeInfo.add(new ChangeInfo(dir, changeType, oldStart, oldRange, oldEnd, newStart, newRange, newEnd, code));
	    		oldRange = 0; oldStart = 0; oldEnd=0; newStart=0; newRange=0; newEnd=0; dir = ""; code = ""; changeType = "";
	    		
	    		change = change.replaceAll("diff .+ b/","");	    	
	    		if(!change.contains(".java")) continue;
	    		dir = "./TargetProjects/" + projectName + File.separator + change;
	    		status = Status.diff;
	    	} 
	    	else if(status == Status.codes && change.contains("@@")) {
	    		changeInfo.add(new ChangeInfo(dir, changeType, oldStart, oldRange, oldEnd, newStart, newRange, newEnd, code));
	    		oldRange = 0; oldStart = 0; oldEnd=0; newStart=0; newRange=0; newEnd=0; code = ""; changeType = "";
	    		change = change.replaceAll("@@", "");

	    		String oldPart = change.split("\\+")[0];
	    		if(!oldPart.contains(",")) {
	    			oldStart = 0;
	    			oldRange = 0;
	    		} else {
	    			oldRange = Integer.parseInt(oldPart.split(",")[1].trim());
	    			oldStart = Integer.parseInt(oldPart.split(",")[0].replace("-", "").trim());
	    		}
	    		if(oldStart == 0 && oldRange == 0) oldEnd = 0;
	    		else oldEnd = oldStart + oldRange - 1;
	    		
	    		String newPart = change.split("\\+")[1];
	    		if(!newPart.contains(",")) {
	    			newStart = Integer.parseInt(newPart.split(",")[0].trim());
		    		newRange = 0;
	    		}else {
	    			newStart = Integer.parseInt(newPart.split(",")[0]);
	    			newRange = Integer.parseInt(newPart.split(",")[1].trim());
	    		}
	    		newEnd = newStart + newRange - 1;	    		
	    		status = Status.lineNum;
	    	}
	    	else if(change.startsWith("deleted")) {
	    		changeType = "D";
	    	}
	    	else if(status == Status.codes && codeChangeSplitByNewLine.length -1 == cnt) {
	    		changeInfo.add(new ChangeInfo(dir, changeType, oldStart, oldRange, oldEnd, newStart, newRange, newEnd, code));
	    		oldRange = 0; oldStart = 0; oldEnd=0; newStart=0; newRange=0; newEnd=0; code = ""; changeType = "";	
	    	}	    	
	    	cnt ++;
	    }
	    return changeInfo;
	}
}
