package de.uniks.networkparser.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.ext.tar.TarArchiveEntry;
import de.uniks.networkparser.ext.tar.TarArchiveInputStream;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLContainer;
import de.uniks.networkparser.xml.XMLEntity;

public class Gradle {
	private boolean download=true;

	//	@Test
	public void testMain() {
		Gradle gradle = new Gradle();
		gradle.initProject("np.jar", "Test");
	}

	public boolean initProject(String jarFile, String projectName) {
		if(Os.isReflectionTest() || jarFile == null) {
			return false;
		}
		File file = new File(".");
		String path = "";
		if(projectName == null) {
			projectName = file.getParentFile().getName();
		} else {
			path = projectName+"/";
			new File(projectName).mkdirs();
		}

		writeProjectPath(path, projectName);
		writeGradle(path);
		extractGradleFiles(path);
		
		return true;
	}

	public void writeProjectPath(String path, String name) {
		if(path == null || name == null) {
			return;
		}
		XMLContainer container=new XMLContainer().withStandardPrefix();
		XMLEntity classpath = container.createChild("classpath");
		classpath.createChild("classpathentry", "kind", "src", "path", "src/main/java");
		classpath.createChild("classpathentry", "kind", "con", "path", "org.eclipse.jdt.launching.JRE_CONTAINER");
		classpath.createChild("classpathentry", "kind", "con", "path", "org.eclipse.buildship.core.gradleclasspathcontainer");
		classpath.createChild("classpathentry", "kind", "output","path","bin");
		FileBuffer.writeFile(path+".classpath", container.toString(2));
		
		container=new XMLContainer().withStandardPrefix();
		XMLEntity projectDescription = container.createChild("projectDescription");
		projectDescription.createChild("name", name);
		projectDescription.createChild("comment");
		projectDescription.createChild("projects");
		XMLEntity buildSpec = projectDescription.createChild("buildSpec");
		XMLEntity buildCommand = buildSpec.createChild("buildCommand");
		buildCommand.createChild("name", "org.eclipse.jdt.core.javabuilder");
		buildCommand.createChild("arguments", "");
		XMLEntity natures = projectDescription.createChild("natures");
		natures.createChild("nature", "org.eclipse.jdt.core.javanature");
		FileBuffer.writeFile(path+".project", container.toString(2));
		
		new File(path+"src/main/java").mkdirs();
		new File(path+"bin").mkdirs();
	}
	
	public void writeGradle(String path) {
		if(path == null) {
			return;
		}
		CharacterBuffer sb = new CharacterBuffer();
		sb.withLine("**/build");
		sb.withLine("**/bin");
		sb.withLine("**/gen");
		sb.withLine("**/.settings");
		sb.withLine("**/.gradle");
		sb.withLine("*.*~");
		sb.withLine("gradle.properties");
		FileBuffer.writeFile(path+".gitignore", sb.toString());
		
		HTMLEntity http = NodeProxyTCP.getHTTP("https://services.gradle.org/distributions/");
		String body = http.getBody().toString();
		int pos = body.indexOf("-src.zip");
		int start = body.lastIndexOf('"', pos);
		int end = body.indexOf('"', pos);
		String ref = body.substring(start, end);
		ByteBuffer binary = NodeProxyTCP.getHTTPBinary("https://services.gradle.org"+ref);
		
		FileBuffer.writeFile(path+"gradle.zip", binary.array());
		
		
		
	}
	
	public void extractGradleFiles(String path) {
		if(path == null) {
			return;
		}
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(path+"gradle.zip");
			ZipEntry entry = jarFile.getEntry("gradlew");
			InputStream inputStream = jarFile.getInputStream(entry);
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			
			File targetFile = new File(path+"gradlew");
			FileOutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();
			
			entry = jarFile.getEntry("gradlew.bat");
			inputStream = jarFile.getInputStream(entry);
			buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			
			targetFile = new File(path+"gradlew.bat");
			outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();
		}catch (Exception e) {
		}finally {
			if(jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean loadNPM() {
		if(Os.isReflectionTest()) {
			return true;
		}
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
		if(Os.isReflectionTest()) {
			return true;
		}
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
