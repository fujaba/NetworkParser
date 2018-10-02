package de.uniks.networkparser.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLContainer;
import de.uniks.networkparser.xml.XMLEntity;

public class Gradle {
	@Test
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
}
