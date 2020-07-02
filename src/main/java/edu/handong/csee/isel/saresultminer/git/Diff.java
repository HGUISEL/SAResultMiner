package edu.handong.csee.isel.saresultminer.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Diff {
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
//			    System.out.println( changedPath );			    
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
	
	public void diffCommit(Git git, String commitID) throws IOException {
	    //Get the commit you are looking for.
	    RevCommit newCommit;
	    try (RevWalk walk = new RevWalk(git.getRepository())) {
	        newCommit = walk.parseCommit(git.getRepository().resolve(commitID));
	    }

	    System.out.println("LogCommit: " + newCommit);
	    String logMessage = newCommit.getFullMessage();
	    System.out.println("LogMessage: " + logMessage);
	    //Print diff of the commit with the previous one.
	    System.out.println(getDiffOfCommit(newCommit, git));

	}
	
	private String getDiffOfCommit(RevCommit newCommit, Git git) throws IOException {
	    RevCommit oldCommit = getPrevHash(newCommit, git);
	    if(oldCommit == null){
	        return "Start of repo";
	    }

	    AbstractTreeIterator oldTreeIterator = getCanonicalTreeParser(oldCommit, git);
	    AbstractTreeIterator newTreeIterator = getCanonicalTreeParser(newCommit, git);
	    OutputStream outputStream = new ByteArrayOutputStream();
	    try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
	        formatter.setRepository(git.getRepository());
	        formatter.format(oldTreeIterator, newTreeIterator);
	    }
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

	private AbstractTreeIterator getCanonicalTreeParser(ObjectId commitId, Git git) throws IOException {
	    try (RevWalk walk = new RevWalk(git.getRepository())) {
	        RevCommit commit = walk.parseCommit(commitId);
	        ObjectId treeId = commit.getTree().getId();
	        try (ObjectReader reader = git.getRepository().newObjectReader()) {
	            return new CanonicalTreeParser(null, reader, treeId);
	        }
	    }
	}
	
}
