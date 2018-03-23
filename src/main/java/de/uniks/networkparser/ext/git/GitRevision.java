package de.uniks.networkparser.ext.git;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class GitRevision {
	public static final String MAYOR="mayor";
	public static final String MINOR="minor";
	public static final String HASH="hash";
	public static final String TAG="tag";
	public static final String BRANCHNAME="branchname";
	public static final String LASTCOMMIT="lastcommit";
	public static final String REVISIONNUMBER="revisionnumber";
	public static final String COMMITS="commits";

	public static void main(String[] args) throws IOException {
		GitRevision revision = new GitRevision();
		System.out.println(revision.execute());
	}
	private boolean full=false;

	@SuppressWarnings("unchecked")
	public JsonObject execute() throws IOException {
		if(ReflectionLoader.FILEREPOSITORYBUILDER == null) {
			return null;
		}
		File file = new File("");
		Object builder = ReflectionLoader.newInstance(ReflectionLoader.FILEREPOSITORYBUILDER);
		Object repository = null;
		Map<String, ?> allRefs = null;
		LinkedHashSet<String> branches=new LinkedHashSet<String>();
		String id = null;
		Object headID = null;
		JsonArray map= new JsonArray();
		JsonObject info = new JsonObject();

		int count=0;
		try {

			Object repoBuilder = ReflectionLoader.call("setWorkTree", builder, File.class, file);
			repository = ReflectionLoader.callChain(repoBuilder, "readEnvironment", "findGitDir", "build");
				// scan environment GIT_* variables
				// scan up the file system tree

			calcGitTag(repository, info);
			allRefs =(Map<String, ?>) ReflectionLoader.call("getAllRefs",repository);

			headID = ReflectionLoader.call("resolve", repository, String.class, "HEAD");
			if(headID != null) {
				id = (String) ReflectionLoader.call("name", headID);
			}
			commitInfo(map, repository, headID, null);
			String branch = (String) ReflectionLoader.call("getBranch", repository);
			branches.add(branch);

			while (headID!=null){
				count++;
				Object oldId = headID;
				String name = (String) ReflectionLoader.call("getName", headID);
				headID = ReflectionLoader.call("resolve", repository, String.class, name+ "^1");
				commitInfo(map, repository, headID, oldId);
			}
		}catch(Exception e) {
		} finally {
			if(repository != null) {
				ReflectionLoader.call("close", repository);
			}
		}
		if(allRefs != null) {
			if(id == null) {
				id = "";
			}
			for(Iterator<?> i = allRefs.entrySet().iterator();i.hasNext();){
				Entry<String, Object> item = (Entry<String, Object>) i.next();
				Object value = item.getValue();
				String newId = (String) ReflectionLoader.callChain(value, "getObjectId", "name");
				if(id.equals(newId)) {
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


		info.put(BRANCHNAME, allBranches.toString());
		info.put(LASTCOMMIT, id);
		info.put(REVISIONNUMBER, count);
		info.put(COMMITS, map);
		return info;
	}
	public boolean setFull(boolean full) {
		if(full != this.full) {
			this.full = full;
			return true;
		}
		return false;
	}

	public boolean isFull() {
		return full;
	}

	public int calcGitTag(Object repository, JsonObject info) {
		int minor=-1;
		int mayor=-1;
		String tag = null;
		String tagHash = "";
		@SuppressWarnings("unchecked")
		Map<String, Object> tags = (Map<String, Object>) ReflectionLoader.call("getTags", repository);
		int versionNumber = 0;
		for(Iterator<Entry<String, Object>> i = tags.entrySet().iterator();i.hasNext();) {
			Entry<String, Object> entry = i.next();
			try {
				String id = entry.getKey().trim();
				int start = id.indexOf(".");
				int vNumber = 0;
				int mayorNumber = 0;
				if(start>0) {
					mayorNumber = Integer.valueOf(id.substring(0, start));
					id = id.substring(start+1);
					start = id.indexOf(".");
					if(start>0) {
						try {
							vNumber = Integer.valueOf(id.substring(start+1));
						}catch (Exception e) {
						}
						id = id.substring(0, start);
					}
				}
				Integer value = Integer.valueOf(id);
				if(value>0 ) {
					if(mayorNumber > mayor) {
						tag = entry.getKey().trim();
						mayor = mayorNumber;
						tagHash = (String) ReflectionLoader.call("getName", entry.getValue());
						versionNumber = vNumber;
						minor = value;
					} else if(value > minor) {
						tag = entry.getKey().trim();
						tagHash = (String) ReflectionLoader.call("getName", entry.getValue());
						versionNumber = vNumber;
						minor = value;
					} else if(value == minor) {
						if(versionNumber == 0) {
							tagHash = (String) ReflectionLoader.call("getName", entry.getValue());
							versionNumber = vNumber;
							tag = entry.getKey().trim();
						} else if(vNumber > versionNumber){
							tag = entry.getKey().trim();
							versionNumber = vNumber;
							tagHash = (String) ReflectionLoader.call("getName", entry.getValue());
						}
						minor = value;
					}
				}
			}catch(Exception e) {
				// no problem as long as there's another tag with a number
			}
		}
		info.put(MAYOR, mayor);
		info.put(MINOR, minor);
		info.put(HASH, tagHash);
		info.put(TAG, tag);
		return minor;
	}

	@SuppressWarnings("unchecked")
	private JsonObject commitInfo(JsonArray map, Object repository, Object objectID, Object newerrId) throws Exception {
		try{
			JsonObject jsonObject = new JsonObject();
			Object walk = ReflectionLoader.newInstance(ReflectionLoader.REVWALK, ReflectionLoader.REPOSITORY, repository);
			Object commit = null;
			if(objectID!=null){
				commit = ReflectionLoader.call("parseCommit", walk, ReflectionLoader.ANYOBJECTID, objectID);
//				commit = walk.parseCommit(objectID);
			}
			if(commit!=null){
				jsonObject.put("ID", ReflectionLoader.call("getName", objectID));
				jsonObject.put("TIME", "" + ReflectionLoader.call("getCommitTime", commit));
				Object temp = ReflectionLoader.call("getCommitterIdent", commit);
				if(temp != null) {
					jsonObject.put("COMMITER", ReflectionLoader.call("getName", temp));
				}
				jsonObject.put("MESSAGE", ReflectionLoader.call("getFullMessage", commit));

				if(newerrId!=null && full){
					Object newerCommit = ReflectionLoader.call("parseCommit", walk, newerrId);
					Object reader = ReflectionLoader.call("newObjectReader", repository);
					Object newerTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
					List<Object> diffs=null;
					Object tree = ReflectionLoader.call("getTree", newerCommit);
					if(tree!=null){
						ReflectionLoader.call("reset", newerTreeIter, tree);
						Object newTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
						Object newtree = ReflectionLoader.call("getTree", commit);
						if(newtree != null){
							ReflectionLoader.call("reset", newTreeIter, reader, newtree);
							Object git = ReflectionLoader.newInstance(ReflectionLoader.GIT, repository);
							git = ReflectionLoader.call("diff", git);
							git = ReflectionLoader.call("setNewTree", git, newerTreeIter);
							git = ReflectionLoader.call("setOldTree", git, newTreeIter);
							diffs = (List<Object>) ReflectionLoader.call("call", git);
//							diffs= new Git(repository).diff()
//													.setNewTree(newerTreeIter)
//													.setOldTree(newTreeIter)
//													.call();
						}
					}
					if(diffs!=null){
						JsonArray files= new JsonArray();
						Object refmode = ReflectionLoader.getField("MISSING", ReflectionLoader.FILEMODE);
						for (Object entry : diffs) {
							Object mode = ReflectionLoader.call("getNewMode", entry);
							String value = (String) ReflectionLoader.call("getNewPath", entry);
							if(mode==refmode) {
								files.add(new JsonObject().withValue("REM", value));
							} else {
								files.add(new JsonObject().withValue("CHANGE", value));
							}
						}
						jsonObject.put("FILES", files);
					}
				}
				map.add(jsonObject);
			}
			ReflectionLoader.call("close", walk);
			return jsonObject;
		}catch(Exception e) {
		}
		return null;
	}
}
