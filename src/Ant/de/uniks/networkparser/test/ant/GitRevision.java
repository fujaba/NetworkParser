package de.uniks.networkparser.test.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class GitRevision {
	public static void main(String[] args) throws IOException {
		GitRevision revision = new GitRevision();
		revision.execute();
	}
	private boolean full=false;
	public void execute() throws IOException {
		File file = new File("");

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		Map<String, Ref> allRefs = null;
		LinkedHashSet<String> branches=new LinkedHashSet<String>();
		String id = null;
		ObjectId headID = null;
		JsonArray map= new JsonArray();
		try {
			repository = builder.setWorkTree(file)
			  .readEnvironment() // scan environment GIT_* variables
			  .findGitDir() // scan up the file system tree
			  .build();
	
			calcGitTag(repository);
			allRefs = repository.getAllRefs();
			headID = repository.resolve("HEAD");
			if(headID != null) {
				id = headID.name();
			}
			commitInfo(map, repository, headID, null);
			branches.add(repository.getBranch());
		}catch(IOException e) {
		} finally {
			if(repository != null) {
				repository.close();
			}
		}
		if(allRefs != null) {
			if(id == null) {
				id = "";
			}
	
			for(Iterator<Entry<String, Ref>> i = allRefs.entrySet().iterator();i.hasNext();){
				Entry<String, Ref> item = i.next();
				if(id.equals(item.getValue().getObjectId().name())) {
					if(branches.contains(item.getKey()) == false) {
						branches.add(item.getKey());
					}
				}
			}
		}
		Iterator<String> i = branches.iterator();
		StringBuilder allBranches = new StringBuilder(i.next());
		while(i.hasNext()) {
			allBranches.append(" ").append(i.next());
		}
		
		System.setProperty("Branchname", allBranches.toString());
		System.setProperty("LastCommit", id);

		int count=0;
		while (headID!=null){
			count++;
			ObjectId oldId = headID;
			headID = repository.resolve(headID.getName()+ "^1");
			commitInfo(map, repository, headID, oldId);
//			System.out.println(count);
		}
		System.setProperty("Revisionnumber", "" +count);
		OutputStreamWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream("build/commits.json");
			writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			writer.write(map.toString(2));
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public int calcGitTag(Repository repository) {
		int gittag=-1;
		String tagHash = "";
		Map<String, Ref> tags = repository.getTags();
		for(Iterator<Entry<String, Ref>> i = tags.entrySet().iterator();i.hasNext();) {
			Entry<String, Ref> entry = i.next();
			try {
				Integer value = Integer.valueOf(entry.getKey());
				if(value>0 && value > gittag) {
					gittag = value;
					tagHash = entry.getValue().getName();
				}
			}catch(Exception e) {
				// no problem as long as there's another tag with a number
			}
		}
		System.setProperty("GitTag", "" +gittag);
		System.setProperty("GitTagHash", tagHash);
		return gittag;
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
				jsonObject.put("TIME",  "" + commit.getCommitTime());
				if(commit.getCommitterIdent() != null) {
					jsonObject.put("COMMITER", commit.getCommitterIdent().getName());
				}
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
			walk.close();
			return jsonObject;
		}catch(GitAPIException e) {
		}
		return null;
	}
}
