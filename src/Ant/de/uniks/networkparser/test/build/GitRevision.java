package de.uniks.networkparser.test.build;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class GitRevision {
	private boolean full=false;
	public void execute() throws IOException {
		File file = new File("");
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setWorkTree(file)
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		
		System.setProperty("Branchname", repository.getBranch());
		ObjectId headID = repository.resolve("HEAD");
		System.setProperty("LastCommit", headID.getName());
		
		JsonArray map= new JsonArray();
		commitInfo(map, repository, headID, null);
		
		int count=0;
		while (headID!=null){
			count++;
			ObjectId oldId = headID;
			headID = repository.resolve(headID.getName()+ "^1");
			commitInfo(map, repository, headID, oldId);
//			System.out.println(count);
		}
		System.setProperty("Revisionnumber", "" +count);
		FileWriter writer= new FileWriter("build/commits.json");
		writer.write(map.toString(2));
		writer.close();
	}
	
	public void setFull(boolean full) {
    	this.full = full;
    }
	
	private JsonObject commitInfo(JsonArray map, Repository repository, ObjectId objectID, ObjectId newerrId) throws MissingObjectException, IncorrectObjectTypeException, IOException{
		try{
			JsonObject jsonObject = new JsonObject();
			RevWalk walk = new RevWalk(repository);
			RevCommit commit = null;
			if(objectID!=null){
				commit = walk.parseCommit(objectID);
			}
			if(commit!=null){
				if(objectID!=null){
					jsonObject.put("ID", objectID.getName());
				}
				jsonObject.put("TIME", commit.getCommitTime());
				jsonObject.put("COMMITER", commit.getCommitterIdent().getName());
				jsonObject.put("MESSAGE", commit.getFullMessage());

				if(newerrId!=null && full){
					RevCommit newerCommit = walk.parseCommit(newerrId);
					ObjectReader reader = repository.newObjectReader();
					CanonicalTreeParser newerTreeIter = new CanonicalTreeParser();
					List<DiffEntry> diffs=null;
					if(newerCommit.getTree()!=null){
						newerTreeIter.reset(reader, newerCommit.getTree());
						CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
						if(commit.getTree()!=null){
							newTreeIter.reset(reader, commit.getTree());
							diffs= new Git(repository).diff()
							                        .setNewTree(newerTreeIter)
							                        .setOldTree(newTreeIter)
							                        .call();
						}
					}
					if(diffs!=null){
						JsonArray files= new JsonArray();
						for (DiffEntry entry : diffs) {
							FileMode mode =entry.getNewMode(); 
							if(FileMode.MISSING==mode){
								files.add(new JsonObject().withValue("REM", entry.getNewPath()));
							} else {
								files.add(new JsonObject().withValue("CHANGE", entry.getNewPath()));
							}
						}
						jsonObject.put("FILES", files);
					}
				}
				map.add(jsonObject);
			}
			return jsonObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
