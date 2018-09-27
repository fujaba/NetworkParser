package de.uniks.networkparser.ext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.xml.ArtifactFile;
import de.uniks.networkparser.xml.ArtifactList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLContainer;
import de.uniks.networkparser.xml.XMLEntity;

public class MavenXML {
	public static final String CRLF="\r\n";
	public static final String JSFILE="diagram.js";
	public static final String UPDATE = "\"update\":\"";
	public static boolean checkForUpdate(String url) {
		boolean updated=false;
		if(url == null) {
			return false;
		}
		String lUrl = url.toLowerCase();
		if(lUrl.startsWith("file") ==false && lUrl.startsWith("http") == false) {
			return false;
		}
		HTMLEntity http = NodeProxyTCP.getHTTP(url+"index.html");
		String string = http.getBody().toString();
		int pos = string.indexOf(UPDATE);
		if(pos>0) {
			pos += UPDATE.length();
			int end = string.indexOf("\"", pos);
			if(end>0) {
				String path = string.substring(pos, end);
				ByteBuffer newVersion = NodeProxyTCP.getHTTPBinary(url+path);
				pos = path.lastIndexOf("/");
				// FileName
				if(pos>0) {
					path = path.substring(pos+1);
				}
				FileBuffer.writeFile(path, newVersion.array());
			}
			
		}
		return updated;
	}

	public void writeJavascript(File path) {
		FileBuffer.writeReourceFile(path.getAbsolutePath()+"/"+JSFILE, "graph/diagram.js");
	}

	public boolean writeIndexHtml(File startingDirectory, ArtifactList fullList) {
		if(fullList == null || fullList.size() < 1) {
			return false;
		}
		HTMLEntity indexHtml=new HTMLEntity().withScript(JSFILE);
		indexHtml.withScript("var indexer = new ArtifactIndexer();",
				"indexer.init([",
				fullList.toJson().toString(2),
				"]);"
				);
		return FileBuffer.writeFile(startingDirectory.getAbsolutePath()+"/index.html", indexHtml.toString())>0;

	}

	public String pomFile(ArtifactFile file) {
		return file.toString();
	}
	
//	public void metaFile(String major, String minor, String time) {

	public XMLContainer metaFile(ArtifactList list) {
		XMLContainer entity = new XMLContainer().withStandardPrefix();
		XMLEntity metadata = entity.createChild("metadata");
		metadata.withChild("groupId", list.getGroup());
		metadata.withChild("artifactId", list.getArtifact());
		String version = list.getVersion();
		metadata.withChild("version", version);
		XMLEntity versioning = metadata.createChild("versioning");
		versioning.withChild("latest", version);
		String time = new SimpleDateFormat("yyyyMMddHHmmSS").format(new Date());
		versioning.withChild("lastUpdated", time);
		if(list.biggestRelease != null) {
			versioning.withChild("release", list.biggestRelease.getVersion());
		}
		if(list.biggestSnapShot != null) {
			XMLEntity snapshot = versioning.createChild("snapshot");
			snapshot.withChild("timestamp", list.biggestSnapShot.getTime(time));
			snapshot.withChild("buildNumber", list.biggestSnapShot.getBuildNumber());
		}
		XMLEntity versions = metadata.createChild("versions");
		XMLEntity snapshotVersions = metadata.createChild("snapshotVersions");
		for(Iterator<ArtifactFile> i = list.iteratorReverse();i.hasNext();){
			ArtifactFile item = i.next();
			if(item.isSnapshot()) {
				XMLEntity snapshot = snapshotVersions.createChild("snapshotVersion");
				snapshot.withChild("extension", item.getExtension());
				snapshot.withChild("value", item.getVersion());
				snapshot.withChild("updated", item.getTime(time));
			}
			versions.withChild("version", item.getVersion());
		}
		return entity;
	}
	
	private void copyArtefact(ArtifactFile artefect, String path, boolean groupPath) {
		File directory = new File(path);
		for(String classifier : artefect.getClassifiers()) {
			String fileName;
			if(groupPath) {
				fileName = directory.getAbsoluteFile()+"/"+getSimpleName(artefect, classifier);
			}else {
				fileName = directory.getAbsoluteFile()+"/"+artefect.toFile(groupPath, classifier);
			}
			FileBuffer.copyFile(artefect.getPath()+artefect.toFile(true, classifier), fileName);
		}
	}

	public void writeMavenMetaFile(ArtifactList list, File path) {
		if(list != null && list.size() > 0) {
			XMLContainer metaFile = metaFile(list);
			FileBuffer.writeFile(path.getAbsolutePath()+File.separator+"maven-metadata.xml", metaFile.toString(2));
			if(list.biggestRelease != null) {
				copyArtefact(list.biggestRelease, path.getAbsolutePath()+"/latest", false);
			}
			if(list.biggestSnapShot != null) {
				copyArtefact(list.biggestSnapShot, path.getAbsolutePath()+"/latest-SNAPSHOT", false);
			}
			String pathStr =  path.getAbsolutePath();
			for(ArtifactFile artefact : list) {
				copyArtefact(artefact, pathStr, true);
			}
		}
	}

	// TestSHA1
	public void writeSHA1(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest cript = MessageDigest.getInstance("SHA-1");
		cript.reset();
		FileInputStream fis = new FileInputStream(file);
		byte[] dataBytes = new byte[1024];
		int nread = 0; 
		while ((nread = fis.read(dataBytes)) != -1) {
			cript.update(dataBytes, 0, nread);
		}
		fis.close();
		File output = new File(file.getAbsolutePath()+".sha1");
		output.createNewFile();
		FileOutputStream os = new FileOutputStream(output);
		os.write(new String(cript.digest()).getBytes());
		os.close();
	}
	
	public void writeMD5(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		FileInputStream fis = new FileInputStream(file);
		byte[] dataBytes = new byte[1024];
		int nread = 0; 
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		fis.close();
		byte[] mdbytes = md.digest();

		//convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		System.out.println("Digest(in hex format):: " + sb.toString());
		//convert the byte to hex format method 2
		// StringBuffer hexString = new StringBuffer();
//        	for (int i=0;i<mdbytes.length;i++) {
//        		String hex=Integer.toHexString(0xff & mdbytes[i]);
//       	     	if(hex.length()==1) hexString.append('0');
//       	     	hexString.append(hex);
//        	}
//        	maven-metadata.xml.md5
		File output = new File(file.getAbsolutePath()+".md5");
		output.createNewFile();
		FileOutputStream os = new FileOutputStream(output);
		os.write(sb.toString().getBytes());
		os.close();
//        	System.out.println("Digest(in hex format):: " + hexString.toString());
		}

	private String getSimpleName(ArtifactFile artifact, String classifier) {
		if("pom".equalsIgnoreCase(classifier) || "jar".equalsIgnoreCase(classifier)) {
			classifier = "."+classifier;
		}else {
			classifier = "-"+classifier+".jar";
		}
		String path = artifact.getVersion();
		if(artifact.isSnapshot()) {
			path += "-SNAPSHOT";
		}
		String file=artifact.getArtifactId() + "-" + artifact.getVersion();
		if(artifact.isSnapshot()) {
			file += "-SNAPSHOT";
		}
		return artifact.getGroupId().replace(".","/")+"/"+artifact.getArtifactId()+"/"+path+"/"+file+classifier;
	}

	public ArtifactList indexer(String srcPath, String groupId, String... extension) {
		ArtifactList list = new ArtifactList();
		File[] listFiles = null;
		try {
			listFiles = new File(srcPath).listFiles();
		}catch (Exception e) {
		}
		if(listFiles == null) {
			return list;
		}
		if(extension ==null || extension.length<1) {
			extension = new String[] {"*"};
		}
		for(File file : listFiles) {
			boolean add=false;
			for(String ext : extension) {
				if("*".equals(ext)) {
					add = true;
					break;
				}else if(file.getName().endsWith(ext)) {
					add = true;
					break;
				}
			}
			if(add) {
				String time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(file.lastModified());
				list.add(ArtifactFile.createContext(file.getPath(), groupId, time));
			}
		}
		return list;
	}
	public ArtifactList buildMaven(String srcPath, String groupId, String target, String... extension) {
		ArtifactList list = indexer(srcPath, groupId, extension);
		if(list != null) { 
			File targetPath = new File(target);
			this.writeMavenMetaFile(list, targetPath);
			this.writeIndexHtml(targetPath, list);
		}
		return list;
	}
}
