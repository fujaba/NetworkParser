package de.uniks.networkparser.ext.git;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class GitRevision {
	public static final String MAYOR="mayor";
	public static final String MINOR="minor";
	public static final String HASH="hash";
	public static final String TAG="tag";
	public static final String BRANCHNAME="branchname";
	public static final String LASTCOMMIT="lastcommit";
	public static final String REVISIONNUMBER="revisionnumber";
	public static final String COMMITS="commits";

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

			Object repoBuilder = ReflectionLoader.call(builder, "setWorkTree", File.class, file);
			repository = ReflectionLoader.callChain(repoBuilder, "readEnvironment", "findGitDir", "build");
				// scan environment GIT_* variables
				// scan up the file system tree

			calcGitTag(repository, info);
			allRefs =(Map<String, ?>) ReflectionLoader.call(repository, "getAllRefs");

			headID = ReflectionLoader.call(repository, "resolve", String.class, "HEAD");
			if(headID != null) {
				id = (String) ReflectionLoader.call(headID, "name");
			}
			commitInfo(map, repository, headID, null);
			String branch = (String) ReflectionLoader.call(repository, "getBranch");
			branches.add(branch);

			while (headID!=null){
				count++;
				Object oldId = headID;
				String name = (String) ReflectionLoader.call(headID, "getName");
				headID = ReflectionLoader.call(repository, "resolve", String.class, name+ "^1");
				commitInfo(map, repository, headID, oldId);
			}
		}catch(Exception e) {
		} finally {
			if(repository != null) {
				ReflectionLoader.call(repository, "close");
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
		Map<String, Object> tags = (Map<String, Object>) ReflectionLoader.call(repository, "getTags");
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
						tagHash = (String) ReflectionLoader.call(entry.getValue(), "getName");
						versionNumber = vNumber;
						minor = value;
					} else if(value > minor) {
						tag = entry.getKey().trim();
						tagHash = (String) ReflectionLoader.call(entry.getValue(), "getName");
						versionNumber = vNumber;
						minor = value;
					} else if(value == minor) {
						if(versionNumber == 0) {
							tagHash = (String) ReflectionLoader.call(entry.getValue(), "getName");
							versionNumber = vNumber;
							tag = entry.getKey().trim();
						} else if(vNumber > versionNumber){
							tag = entry.getKey().trim();
							versionNumber = vNumber;
							tagHash = (String) ReflectionLoader.call(entry.getValue(), "getName");
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
				commit = ReflectionLoader.call(walk, "parseCommit", ReflectionLoader.ANYOBJECTID, objectID);
//				commit = walk.parseCommit(objectID);
			}
			if(commit!=null){
				jsonObject.put("ID", ReflectionLoader.call(objectID, "getName"));
				jsonObject.put("TIME", "" + ReflectionLoader.call(commit, "getCommitTime"));
				Object temp = ReflectionLoader.call(commit,"getCommitterIdent");
				if(temp != null) {
					jsonObject.put("COMMITER", ReflectionLoader.call(temp, "getName"));
				}
				jsonObject.put("MESSAGE", ReflectionLoader.call(commit, "getFullMessage"));

				if(newerrId!=null && full){
					Object newerCommit = ReflectionLoader.call(walk, "parseCommit", newerrId);
					Object reader = ReflectionLoader.call(repository, "newObjectReader");
					Object newerTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
					List<Object> diffs=null;
					Object tree = ReflectionLoader.call(newerCommit, "getTree");
					if(tree!=null){
						ReflectionLoader.call(newerTreeIter, "reset", tree);
						Object newTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
						Object newtree = ReflectionLoader.call(commit, "getTree");
						if(newtree != null){
							ReflectionLoader.call(newTreeIter, "reset", reader, newtree);
							Object git = ReflectionLoader.newInstance(ReflectionLoader.GIT, repository);
							git = ReflectionLoader.call(git, "diff");
							git = ReflectionLoader.call(git, "setNewTree", newerTreeIter);
							git = ReflectionLoader.call(git, "setOldTree", newTreeIter);
							diffs = (List<Object>) ReflectionLoader.call(git, "call");
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
							Object mode = ReflectionLoader.call(entry, "getNewMode");
							String value = (String) ReflectionLoader.call(entry, "getNewPath");
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
			ReflectionLoader.call(walk, "close");
			return jsonObject;
		}catch(Exception e) {
		}
		return null;
	}
	
	protected SimpleKeyValueList<String, Integer> createdComment(String sourcePath, String licenceFile){
		SimpleKeyValueList<String, Integer> values = new SimpleKeyValueList<String, Integer>();
		values.put("LOC", 0);
		
		CharacterBuffer licence = FileBuffer.readFile(licenceFile);
		createdComment(sourcePath, licence, values);
		return values;
	}
	
	private void createdComment(String sourcePath, CharacterBuffer licence, SimpleKeyValueList<String, Integer> values){
		File path= new File(sourcePath);

		File[] listFiles = path.listFiles();
		if(listFiles == null) {
			return;
		}
		for (File child : listFiles){
			if(child == null) {
				continue;
			}

			if(child.isDirectory()){
				createdComment(sourcePath+child.getName()+ "/", licence, values);
			} else {
				if(!child.getAbsolutePath().endsWith(".java")){
					continue;
				}
//				ParserEntity parser = new ParserEntity();
//				Clazz create = parser.parse(FileBuffer.readFile(child));
//				values.put("LOC", values.get("LOC") + parser.getLOC());
//				lineofCode += source.getLineOfCode();
//				String customComment = source.getCustomComment();
//				if(customComment.length()>0){
//					 customItems.put(customComment, source.getFileName());
////					 System.out.println("Thirdparty-Source (" +source.getFileName()+":1)");
//					 String relativ = source.getFileName().substring(new File("src\\main\\java").getAbsolutePath().length() + 1);
//					 int pos = relativ.lastIndexOf("\\");
//					 System.out.println("Thirdparty-Source "+relativ.replaceAll("\\\\", ".")+"("+relativ.substring(pos+1)+":1)");
//					 thirdparty++;
//					 continue;
//				 }
//				if(source.skipComment()){
//					skip++;
//					continue;
//				}
//				if(source.changeComment(commentFile.getComment())){
//					System.out.println(source.getFileName());
//					change++;
//
//					source.write();
//				} else {
//					ok++;
//				}
			}
		}
	}
}
