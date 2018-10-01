package de.uniks.networkparser.ext;

import java.io.File;
import java.io.FileOutputStream;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.ext.tar.TarArchiveEntry;
import de.uniks.networkparser.ext.tar.TarArchiveInputStream;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.HTMLEntity;

public class NPMDownloader {
	private boolean download=true;

	public boolean load() {
		JsonObject packageJson = new JsonObject();
		CharacterBuffer buffer = FileBuffer.readFile("package.json");
		packageJson.withValue(buffer);
		if(download) {
			JsonObject dependencies = packageJson.getJsonObject("dependencies");
			for(int i=0;i<dependencies.size();i++) {
				String lib = dependencies.getKeyByIndex(i);
				HTMLEntity answer = NodeProxyTCP.getHTTP("https://registry.npmjs.org/"+lib+"/latest");
				JsonObject npmVersion = new JsonObject().withValue(answer.getBody().getValue());
				FileBuffer.writeFile("node_modules/"+lib+".json", npmVersion.toString(2));
				JsonObject dist = npmVersion.getJsonObject("dist");
				if(dist != null) {
					String url = dist.getString("tarball");
					ByteBuffer httpBinary = NodeProxyTCP.getHTTPBinary(url);
					FileBuffer.writeFile("node_modules/"+lib+".tgz", httpBinary.array());
					decompress("node_modules/"+lib);
				}
			}
		}
		JsonObject copyJob = packageJson.getJsonObject("//");
		if(copyJob != null) {
			for(int i=0;i<copyJob.size();i++) {
				String key = copyJob.getKeyByIndex(i);
				JsonArray job = copyJob.getJsonArray(key);
				for(int j=0;j<job.size();j+=2) {
					FileBuffer.copyFile(job.getString(j), job.getString(j+1));
				}
			}
		}
		return true;
	}
	
	public boolean decompress(String file) {
		if(file == null) {
			return false;
		}
		try {
			TarArchiveInputStream tis = TarArchiveInputStream.create(file+".tgz");
			if(tis == null) {
				return false;
			}
			TarArchiveEntry tarEntry = null;
			while ((tarEntry = tis.getNextTarEntry()) != null) {
				String outputName;
				if(tarEntry.getName().startsWith("package/")) {
					outputName = file +"/"+ tarEntry.getName().substring(8);
				} else {
					outputName = "node_modules/"+tarEntry.getName();
				}
				File outputFile = new File(outputName);
				if(tarEntry.isDirectory()){
//					System.out.println("outputFile Directory ---- "+ outputFile.getAbsolutePath());
					if(!outputFile.exists()){
						outputFile.mkdirs();
					}
				} else {
//					System.out.println("outputFile File ---- " + outputFile.getAbsolutePath());
					outputFile.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(outputFile);
					FileBuffer.copy(tis, fos);
					fos.close();
				}
			}
			tis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
