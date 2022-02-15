package de.uniks.networkparser.ext;

/*
 * The MIT License
 * 
 * Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class GitRevision.
 *
 * @author Stefan
 */
public class GitRevision {
	
	/** The Constant MAINTAG. */
	public static final String MAINTAG = "GIT";
	
	/** The Constant MAYOR. */
	public static final String MAYOR = "mayor";
	
	/** The Constant MINOR. */
	public static final String MINOR = "minor";
	
	/** The Constant HASH. */
	public static final String HASH = "hash";
	
	/** The Constant TAG. */
	public static final String TAG = "tag";
	
	/** The Constant BRANCHNAME. */
	public static final String BRANCHNAME = "branchname";
	
	/** The Constant LASTCOMMIT. */
	public static final String LASTCOMMIT = "lastcommit";
	
	/** The Constant REVISIONNUMBER. */
	public static final String REVISIONNUMBER = "revisionnumber";
	
	/** The Constant COMMITS. */
	public static final String COMMITS = "commits";

	private boolean full = false;
	private int max = -1;
	private String path;
	private String username;
	private String password;

	/**
	 * With path.
	 *
	 * @param path the path
	 * @return the git revision
	 */
	public GitRevision withPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Execute.
	 *
	 * @return the json object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JsonObject execute() throws IOException {
		JsonObject json = execute(max);
		if (json != null) {
			System.setProperty("Branchname", json.getString(BRANCHNAME));
			System.setProperty("LastCommit", json.getString(LASTCOMMIT));
			System.setProperty("Revisionnumber", json.getString(REVISIONNUMBER));
		}
		return json;
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public Object getRepository() {
		if (ReflectionLoader.FILEREPOSITORYBUILDER == null) {
			return null;
		}

		File file = null;
		String localPath = path;
		if (localPath == null) {
			localPath = "";
		} else if ((localPath.endsWith("/") || localPath.endsWith("\\")) == false) {
			localPath += "/";
		}
		File projectFile = null;
		if (new File(localPath + ".git/config").exists()) {
			file = new File(localPath + ".git");
			projectFile = file;
		} else if (new File(localPath + "config").exists()) {
			file = new File(localPath);
			projectFile = file;
		}
		if (file == null) {
			return null;
		}
		Object repository = null;
		try {
			Object builder = ReflectionLoader.newInstance(ReflectionLoader.FILEREPOSITORYBUILDER);
			ReflectionLoader.call(builder, "setWorkTree", File.class, file);
			if (projectFile != null && "".equals(projectFile.getName()) == false) {
				ReflectionLoader.call(builder, "setGitDir", File.class, projectFile);
			}
			repository = ReflectionLoader.callChain(builder, "readEnvironment", "findGitDir", "build");
			/* scan environment GIT_* variables */
			/* scan up the file system tree */
		} catch (Exception e) {
		}
		return repository;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new GitRevision().execute(3);
	}

	/**
	 * Execute.
	 *
	 * @param maxCommit the max commit
	 * @return the json object
	 */
	@SuppressWarnings("unchecked")
	public JsonObject execute(int maxCommit) {
		Object repository = getRepository();
		if (repository == null) {
			return null;
		}
		
		Map<String, ?> allRefs = null;
		LinkedHashSet<String> branches = new LinkedHashSet<String>();
		String id = null;
		Object headID = null;
		JsonArray map = new JsonArray();
		JsonObject info = new JsonObject();
		Object remoteURL = ReflectionLoader.call(ReflectionLoader.call(repository, "getConfig"), "getString","remote", "origin", "url");
		if(remoteURL  != null) {
			info.put("remote", remoteURL);
		}

		int count = 0;
		try {
			calcGitTag(repository, info);
			allRefs = (Map<String, ?>) ReflectionLoader.call(repository, "getAllRefs");
			try {
				headID = ReflectionLoader.call(repository, "resolve", String.class, "HEAD");
			} catch (Exception e) {
			}
			if (headID != null) {
				id = (String) ReflectionLoader.call(headID, "name");
			}
			commitInfo(map, repository, headID, null);
			String branch = (String) ReflectionLoader.call(repository, "getBranch");
			branches.add(branch);
			info.put("currentBranch", branch);

			while (headID != null) {
				count++;
				Object oldId = headID;
				String name = (String) ReflectionLoader.call(headID, "getName");
				headID = ReflectionLoader.call(repository, "resolve", String.class, name + "^1");
				if (maxCommit < 0 || count < maxCommit) {
					commitInfo(map, repository, headID, oldId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ReflectionLoader.call(repository, "close");
		}
		if (allRefs != null) {
			if (id == null) {
				id = "";
			}
			for (Iterator<?> i = allRefs.entrySet().iterator(); i.hasNext();) {
				Entry<String, Object> item = (Entry<String, Object>) i.next();
				Object value = item.getValue();

				String newId = (String) ReflectionLoader.callChain(value, false, "getObjectId", "getName");
				if (id.equals(newId)) {
					if (branches.contains(item.getKey()) == false) {
						branches.add(item.getKey());
					}
				}
			}
		}
		Iterator<String> i = branches.iterator();
		String branchesTag = "";
		if (i != null && i.hasNext()) {
			CharacterBuffer allBranches = new CharacterBuffer().with(i.next());
			while (i.hasNext()) {
				allBranches.with(' ').with(i.next());
			}
			branchesTag = allBranches.toString();
		}

		info.put(BRANCHNAME, branchesTag);
		info.put(LASTCOMMIT, id);
		if (count == 0) {
			info.put(REVISIONNUMBER, 1);
		} else {
			info.put(REVISIONNUMBER, count);
		}
		info.put(COMMITS, map);
		return info;
	}

	/**
	 * With max commit.
	 *
	 * @param value the value
	 * @return the git revision
	 */
	public GitRevision withMaxCommit(int value) {
		this.max = value;
		return this;
	}

	/**
	 * Sets the full.
	 *
	 * @param full the full
	 * @return true, if successful
	 */
	public boolean setFull(boolean full) {
		if (full != this.full) {
			this.full = full;
			return true;
		}
		return false;
	}

	/**
	 * Checks if is full.
	 *
	 * @return true, if is full
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * Calc git tag.
	 *
	 * @param repository the repository
	 * @param info the info
	 * @return the int
	 */
	@SuppressWarnings("unchecked")
	public int calcGitTag(Object repository, JsonObject info) {
		if (ReflectionLoader.REPOSITORY == null || repository == null
				|| ReflectionLoader.REPOSITORY.isAssignableFrom(repository.getClass()) == false) {
			return -1;
		}
		int minor = -1;
		int mayor = 0;
		String tag = null;
		String tagHash = "";
		Map<String, Object> tags = (Map<String, Object>) ReflectionLoader.call(repository, "getTags");
		SimpleKeyValueList<String, String> tagNames = new SimpleKeyValueList<String, String>();
		for (Iterator<Entry<String, Object>> i = tags.entrySet().iterator(); i.hasNext();) {
			Entry<String, Object> entry = i.next();
			try {
				tagNames.add(entry.getKey().trim(), (String) ReflectionLoader.call(entry.getValue(), "getName"));

			} catch (Exception e) {
			}
		}
		if (tags == null || tags.size() == 0) {
			Object refDatabase = ReflectionLoader.call(repository, "getRefDatabase");
			List<?> refs = (List<?>) ReflectionLoader.call(refDatabase, "getRefsByPrefix", "refs/tags/");
			if (refs.size() > 0) {
				for (int i = 0; i < refs.size(); i++) {
					try {
						String id = (String) ReflectionLoader.call(refs.get(i), "getTagName");
						String hashValue = (String) ReflectionLoader.call(refs.get(i), "getObject", "name");
						tagNames.add(ReflectionLoader.call(id, hashValue));
					} catch (Exception e) {
					}
				}
			}
		}
		int versionNumber = 1;
		for (int i = 0; i < tagNames.size(); i++) {
			try {
				String id = tagNames.getKeyByIndex(i);
				int start = id.indexOf(".");
				int vNumber = 0;
				int mayorNumber = 0;
				if (start > 0) {
					mayorNumber = StringUtil.getInteger(id.substring(0, start));
					id = id.substring(start + 1);
					start = id.indexOf(".");
					if (start > 0) {
						vNumber = StringUtil.getInteger(id.substring(start + 1));
						id = id.substring(0, start);
					}
				}
				Integer value = StringUtil.getInteger(id);
				if (value > 0) {
					if (mayorNumber > mayor || value > minor) {
						tag = id;
						mayor = mayorNumber;
						tagHash = tagNames.getValueByIndex(i);
						versionNumber = vNumber;
						minor = value;
					} else if (value == minor) {
						if (versionNumber == 0) {
							tagHash = tagNames.getValueByIndex(i);
							versionNumber = vNumber;
							tag = id;
						} else if (vNumber > versionNumber) {
							tag = id;
							versionNumber = vNumber;
							tagHash = tagNames.getValueByIndex(i);
						}
						minor = value;
					}
				}
			} catch (Exception e) {
				/* no problem as long as there's another tag with a number */
			}
		}
		info.put(MAYOR, mayor);
		if (minor < 0) {
			info.put(MINOR, 0);
		} else {
			info.put(MINOR, minor);
		}
		info.put(HASH, tagHash);
		info.put(TAG, tag);
		return minor;
	}

	private JsonObject commitInfo(JsonArray map, Object repository, Object objectID, Object newerrId) throws Exception {
		Object walk = null;
		try {
			JsonObject jsonObject = new JsonObject();
			walk = ReflectionLoader.newInstance(ReflectionLoader.REVWALK, ReflectionLoader.REPOSITORY, repository);
			Object commit = null;
			if (objectID != null) {
				commit = ReflectionLoader.call(walk, "parseCommit", ReflectionLoader.ANYOBJECTID, objectID);
			}
			if (commit != null) {
				jsonObject.put("ID", ReflectionLoader.call(objectID, "getName"));
				jsonObject.put("TIME", "" + ReflectionLoader.call(commit, "getCommitTime"));
				Object temp = ReflectionLoader.call(commit, "getCommitterIdent");
				if (temp != null) {
					jsonObject.put("COMMITTER", ReflectionLoader.call(temp, "getName"));
					jsonObject.put("COMMITTER_EMAIL", ReflectionLoader.call(temp, "getEmailAddress"));
				}
				temp = ReflectionLoader.call(commit, "getAuthorIdent");
				if (temp != null) {
					jsonObject.put("AUTHOR", ReflectionLoader.call(temp, "getName"));
					jsonObject.put("AUTHOR_EMAIL", ReflectionLoader.call(temp, "getEmailAddress"));
				}
				String msg = (String) ReflectionLoader.call(commit, "getFullMessage");
				if (msg != null) {
					msg = msg.trim();
					if (msg.endsWith("\\u000a")) {
						msg = msg.substring(0, msg.length() - 6);
					}
					jsonObject.put("MESSAGE", msg);
				}

				if (newerrId != null && full) {
					Object newerCommit = ReflectionLoader.call(walk, "parseCommit", newerrId);
					Object reader = ReflectionLoader.call(repository, "newObjectReader");
					Object newerTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
					Collection<?> diffs = null;
					Object tree = ReflectionLoader.call(newerCommit, "getTree");
					if (tree != null) {
						ReflectionLoader.call(newerTreeIter, "reset", tree);
						Object newTreeIter = ReflectionLoader.newInstance(ReflectionLoader.CANONICALTREEPARSER);
						Object newtree = ReflectionLoader.call(commit, "getTree");
						if (newtree != null) {
							ReflectionLoader.call(newTreeIter, "reset", reader, newtree);
							Object git = ReflectionLoader.newInstance(ReflectionLoader.GIT, repository);
							git = ReflectionLoader.call(git, "diff");
							git = ReflectionLoader.call(git, "setNewTree", newerTreeIter);
							git = ReflectionLoader.call(git, "setOldTree", newTreeIter);
							diffs = (Collection<?>) ReflectionLoader.call(git, "call");
						}
					}
					if (diffs != null) {
						JsonArray files = new JsonArray();
						Object refmode = ReflectionLoader.getField(ReflectionLoader.FILEMODE, "MISSING");
						for (Object entry : diffs) {
							Object mode = ReflectionLoader.call(entry, "getNewMode");
							String value = (String) ReflectionLoader.call(entry, "getNewPath");
							if (mode == refmode) {
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
			return jsonObject;
		} catch (Exception e) {
		} finally {
			ReflectionLoader.call(walk, "close");
		}
		return null;
	}

	protected SimpleKeyValueList<String, Integer> createdComment(String sourcePath, String licenceFile) {
		SimpleKeyValueList<String, Integer> values = new SimpleKeyValueList<String, Integer>();
		values.put("LOC", 0);

		CharacterBuffer licence = FileBuffer.readFile(licenceFile);
		createdComment(sourcePath, licence, values);
		return values;
	}

	private void createdComment(String sourcePath, CharacterBuffer licence,
			SimpleKeyValueList<String, Integer> values) {
		if (sourcePath == null) {
			return;
		}
		File path = new File(sourcePath);

		File[] listFiles = path.listFiles();
		if (listFiles == null) {
			return;
		}
		for (File child : listFiles) {
			if (child == null) {
				continue;
			}

			if (child.isDirectory()) {
				createdComment(sourcePath + child.getName() + "/", licence, values);
			} else if (!child.getAbsolutePath().endsWith(".java")) {
				continue;
			}
		}
	}

	/**
	 * Inits the.
	 *
	 * @param remoteURL the remote URL
	 * @return true, if successful
	 */
	public boolean init(String remoteURL) {
		if (ReflectionLoader.GIT == null) {
			return false;
		}
		String localPath = path;
		if (localPath == null) {
			localPath = "";
		} else if ((localPath.endsWith("/") || localPath.endsWith("\\")) == false) {
			localPath += "/";
		}
		File dir = new File(localPath);
		File gitDir = new File(localPath + ".git/");
		gitDir.mkdirs();
		Object git = null;
		try {

			Object initGIT = ReflectionLoader.call(ReflectionLoader.GIT, "init");
			ReflectionLoader.call(initGIT, "setDirectory", dir);
			ReflectionLoader.call(initGIT, "setGitDir", gitDir);
			git = ReflectionLoader.call(initGIT, "call");
			if (remoteURL != null) {
				new URL(remoteURL);
				Object config = ReflectionLoader.callChain(git, "getRepository", "getConfig");
				ReflectionLoader.call(config, "setString", "remote", "origin", "url", remoteURL);
				ReflectionLoader.call(config, "save");
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (git != null) {
				ReflectionLoader.call(git, "close");
			}
		}
		return true;
	}

	/**
	 * With authentification.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the git revision
	 */
	public GitRevision withAuthentification(String username, String password) {
		if (username == null || password == null) {
			return this;
		}
		this.username = username;
		this.password = password;

		if (username.length() < 1) {
			this.username = System.getProperty("user.name");
		}
		if (password.length() < 1) {
			this.password = System.getProperty("git");
		}
		return this;
	}

	/**
	 * Pull.
	 *
	 * @param commitId the commit id
	 * @return true, if successful
	 */
	public boolean pull(String... commitId) {
		Object repository = getRepository();
		if (repository == null) {
			return false;
		}
		// org.eclipse.jgit.lib.Repository
		String updateId = null;
		try {
			if (commitId != null && commitId.length > 0) {
				updateId = commitId[0];
			}
			if (updateId == null) {
				// CHECKOUT FIRST
				// FIRST FETCH
				// SECOND CHECKOUT
				updateId = "master";
			}
			Class<?> ref = ReflectionLoader.getClass("org.eclipse.jgit.lib.Repository");
			Object fetch = ReflectionLoader.newInstance("org.eclipse.jgit.api.FetchCommand", ref, repository);

			Object storedConfig = ReflectionLoader.call(repository, "getConfig");
			String url = (String) ReflectionLoader.call(storedConfig, "getString", String.class, "remote", String.class,
					"origin", String.class, "url");
			if (url != null) {
				ReflectionLoader.call(fetch, "setRemote", String.class, url);
			}
			String[] refs = new String[] { "+refs/heads/*:refs/remotes/origin/*", "+refs/tags/*:refs/tags/*",
					"+refs/notes/*:refs/notes/*" };
			ReflectionLoader.call(fetch, "setRefSpecs", String[].class, refs);
			if (this.username != null && this.password != null) {
				Object credentials = ReflectionLoader.newInstance(
						"org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider", String.class, username,
						String.class, password);
				Object credition = ReflectionLoader.getClass("org.eclipse.jgit.transport.CredentialsProvider");
				ReflectionLoader.call(fetch, "setCredentialsProvider", credition, credentials);
			}
			ReflectionLoader.call(fetch, "call");
			Object checkout = ReflectionLoader.newInstance("org.eclipse.jgit.api.CheckoutCommand", ref, repository);
			ReflectionLoader.call(checkout, "setStartPoint", "origin/" + updateId);
			ReflectionLoader.call(checkout, "setCreateBranch", boolean.class, true);
			ReflectionLoader.call(checkout, "setName", updateId);
			ReflectionLoader.call(checkout, "call");
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ReflectionLoader.call(repository, "close");
		}
		return false;
	}
}
