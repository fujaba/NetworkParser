package de.uniks.networkparser.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.io.TarArchiveEntry;
import de.uniks.networkparser.ext.io.TarArchiveInputStream;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLContainer;
import de.uniks.networkparser.xml.XMLEntity;

public class Gradle implements ObjectCondition{
	private boolean download = true;
	public static final String REFLECTIONTEST="test";
	public static final String GIT="git";
	public static final String GRADLE="gradle";

	public static void main(String[] args) {
		Gradle gradle = new Gradle();
		gradle.addAtrifact(null, "Test", "MIT");
//		gradle.initProject("np.jar", "Test", "MIT");
	}

	public boolean initProject(String jarFile, String projectName, String licence) {
		if (Os.isReflectionTest() || jarFile == null) {
			return false;
		}
		File file = new File(".");
		String path = "";
		if (projectName == null) {
			projectName = file.getParentFile().getName();
		} else {
			path = projectName + "/";
			new File(projectName).mkdirs();
		}

		writeProjectPath(path, projectName);
		writeGradle(path, projectName, licence);
		extractGradleFiles(path);

		return true;
	}

	public void writeProjectPath(String path, String name) {
		if (Os.isReflectionTest() || path == null || name == null) {
			return;
		}
		XMLContainer container = new XMLContainer().withStandardPrefix();
		XMLEntity classpath = container.createChild("classpath");
		classpath.createChild("classpathentry", "kind", "src", "path", "src/main/java");
		classpath.createChild("classpathentry", "kind", "con", "path", "org.eclipse.jdt.launching.JRE_CONTAINER");
		classpath.createChild("classpathentry", "kind", "con", "path",
				"org.eclipse.buildship.core.gradleclasspathcontainer");
		classpath.createChild("classpathentry", "kind", "output", "path", "bin");
		FileBuffer.writeFile(path + ".classpath", container.toString(2));

		container = new XMLContainer().withStandardPrefix();
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
		FileBuffer.writeFile(path + ".project", container.toString(2));

		new File(path + "src/main/java").mkdirs();
		new File(path + "bin").mkdirs();
	}
	
	public static Object getType(String type) {
		if(REFLECTIONTEST.equals(type)) {
			return new ReflectionBlackBoxTester();
		}
		if(GIT.equals(type)) {
			return new GitRevision();
		}
		if(GRADLE.equals(type)) {
			return new Gradle();
		}
		return null;
	}

	public boolean execute(Object item, String... fileName) {
		if (item == null) {
			return false;
		}
		try {
			if (item.getClass().getName().equals("org.gradle.execution.taskgraph.DefaultTaskExecutionGraph") == false) {
				return false;
			}
			// Access private variables of tasks graph
			Object tep = ReflectionLoader.getField(item, "taskExecutionPlan");
			if(tep == null) {
				tep = ReflectionLoader.getField(item, "executionPlan");
			}
			if(tep == null) {
				System.out.println("tep");
				return false;
			}
			// Execution starts on these tasks
			Set<?> entryTasks = (Set<?>) ReflectionLoader.getField(tep, "entryTasks");

			// Already processed edges
			SimpleSet<String> edges = new SimpleSet<String>();
			// Create output buffer
			CharacterBuffer dotGraph = new CharacterBuffer();
			dotGraph.with("digraph compile { ");
			dotGraph.with("colorscheme=spectral11;");
			dotGraph.with("rankdir=TB;");
			dotGraph.with("splines=spline;");
			dotGraph.withLine("color=10;");
			// Generate graph for each input
			for (Object et : entryTasks) {
				printGraph(dotGraph, et, edges);
			}

//            // Entry tasks nodes have the same priority
//            dotGraph.append("{ rank=same; ")
//            entryTasks.each { et -> dotGraph.append('"').append(et.task.path).append("\" ") }
//            dotGraph.append("}").append(ls)
			// Finalize graph
			dotGraph.withLine("}");

			// Save graph
			if (fileName != null && fileName.length > 0) {
				FileBuffer.writeFile(fileName[0], dotGraph.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static String getTaskName(Object ti) {
		String name = ""+ReflectionLoader.getField(ti, "task", "path");
		if(name.startsWith("task '")) {
			return name.substring(6, name.length() - 1);
		}
		return name;
	}
	public static void printGraph(CharacterBuffer buffer, Object entry, Set<String> edges) {
		LinkedList<Object> q = new LinkedList<Object>();
		q.add(entry);
		HashSet<String> seen = new HashSet<String>();
		while (q.isEmpty() == false) {
			Object ti = q.remove();
			String tname = getTaskName(ti);
			if (seen.contains(tname)) {
				continue;
			}
			seen.add(tname);
			// org.gradle.execution.taskgraph.LocalTaskInfo
			Object items = ReflectionLoader.call(ti, "getAllSuccessors");

			if (items != null && items instanceof Iterable<?>) {
				Iterable<?> i = (Iterable<?>) items;
				Iterator<?> iterator = i.iterator();
				SimpleSet<Object> itemsSet = new SimpleSet<Object>();
				while (iterator.hasNext()) {
					Object item = iterator.next();
					String sname = getTaskName(item);
					if (edges.add(tname+":"+sname)) {
						// Generate edge between two nodes
						buffer.withLine("\"" + tname + "\" -> \"" + sname + "\";");
					}
					itemsSet.add(item);
				}
				q.addAll(itemsSet);
			}
			buffer.with("\""+tname+"\"");
			buffer.with(" [");
			buffer.with("shape=\"");
			Object field = ReflectionLoader.call(ti, "getDependencyPredecessors");
			if(field != null  && ((Set<?>)field).isEmpty()) {
				buffer.with("hexagon");
			}else {
				field = ReflectionLoader.call(ti, "getDependencySuccessors");
				if(field != null  && ((Set<?>)field).isEmpty()) {
					buffer.with("doubleoctagon");
				} else {
					buffer.with("box");
				}
			}
			buffer.with("\"]");
	        buffer.withLine(";");
		}
	}

	public boolean writeGradle(String path, String projectName, String licence) {
		if (Os.isReflectionTest() || path == null) {
			return false;
		}
		File file = new File(path + "gradle.zip");
		if (file.exists()) {
			return true;
		}
		CharacterBuffer sb = new CharacterBuffer();
		sb.withLine("**/build");
		sb.withLine("**/bin");
		sb.withLine("**/gen");
		sb.withLine("**/.settings");
		sb.withLine("**/.gradle");
		sb.withLine("*.*~");
		sb.withLine("gradle.properties");
		FileBuffer.writeFile(path + ".gitignore", sb.toString());

		HTMLEntity http = NodeProxyTCP.getHTTP("https://services.gradle.org/distributions/");
		String body = http.getBody().toString();
		int pos = body.indexOf("-src.zip");
		int start = body.lastIndexOf('"', pos) + 1;
		int end = body.indexOf('"', pos);
		if(start < 1 || end < 1) {
			return false;
		}
		String ref = body.substring(start, end);
		ByteBuffer binary = NodeProxyTCP.getHTTPBinary("https://services.gradle.org" + ref);

		FileBuffer.writeFile(path + "gradle.zip", binary.array());
		
		// NOW WRITE build.gradle
		
		CharacterBuffer buildGradle = new CharacterBuffer();
		buildGradle.withLine("repositories {");
		buildGradle.withLine("	  jcenter()");
		buildGradle.withLine("	  maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }");
		buildGradle.withLine("}");

		buildGradle.withLine("");
		buildGradle.withLine("dependencies {");
		buildGradle.withLine("	// Use JUnit test framework");
		buildGradle.withLine("	testImplementation 'junit:junit:4.12'");
		buildGradle.withLine("	compile group:\"de.uniks\",name: \"NetworkParser\", version: \"latest.integration\",classifier:\"sources18\",changing: true");
		    	//compile group: "de.uniks", name: "NetworkParser", version: "latest.integration", classifier:"sources18", changing: true
//		    	compile files("NetworkParser-4.7.1351-SNAPSHOT-git.jar")
		buildGradle.withLine("}");
		
		addAtrifact(buildGradle, projectName, licence);
		
		return true;
	}

	public void extractGradleFiles(String path) {
		if (path == null) {
			return;
		}
		JarFile jarFile = null;
		File file = new File(path + "gradle.zip");
		if (file.exists() == false) {
			return;
		}
//		SimpleKeyValueList<String, String> extractFiles = 
				new SimpleKeyValueList<String, String>()
				.withKeyValue("gradlew", "").withKeyValue("gradlew.bat", "")
				.withKeyValue("gradle-wrapper.jar", "gradle/wrapper")
				.withKeyValue("gradle-wrapper.properties", "gradle/wrapper");

		try {
			jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
//				JarEntry entry = entries.nextElement();
//			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
//			      String entryName = (String) it.next();
//			for(JarEntry entry : jarFile.entries()) {
////				if(entry.)
			}
			ZipEntry entry = jarFile.getEntry("gradlew");
			InputStream inputStream = jarFile.getInputStream(entry);
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			File targetFile = new File(path + "gradlew");
			FileOutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();

			entry = jarFile.getEntry("gradlew.bat");
			inputStream = jarFile.getInputStream(entry);
			buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			targetFile = new File(path + "gradlew.bat");
			outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();
		} catch (Exception e) {
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean loadNPM() {
		if (Os.isReflectionTest()) {
			return true;
		}
		JsonObject packageJson = new JsonObject();
		CharacterBuffer buffer = FileBuffer.readFile("package.json");
		packageJson.withValue(buffer);
		if (download) {
			JsonObject dependencies = packageJson.getJsonObject("dependencies");
			for (int i = 0; i < dependencies.size(); i++) {
				String lib = dependencies.getKeyByIndex(i);
				HTMLEntity answer = NodeProxyTCP.getHTTP("https://registry.npmjs.org/" + lib + "/latest");
				JsonObject npmVersion = new JsonObject().withValue(answer.getBody().getValue());
				FileBuffer.writeFile("node_modules/" + lib + ".json", npmVersion.toString(2));
				JsonObject dist = npmVersion.getJsonObject("dist");
				if (dist != null) {
					String url = dist.getString("tarball");
					ByteBuffer httpBinary = NodeProxyTCP.getHTTPBinary(url);
					FileBuffer.writeFile("node_modules/" + lib + ".tgz", httpBinary.array());
					decompress("node_modules/" + lib);
				}
			}
		}
		JsonObject copyJob = packageJson.getJsonObject("//");
		if (copyJob != null) {
			for (int i = 0; i < copyJob.size(); i++) {
				String key = copyJob.getKeyByIndex(i);
				JsonArray job = copyJob.getJsonArray(key);
				for (int j = 0; j < job.size(); j += 2) {
					FileBuffer.copyFile(job.getString(j), job.getString(j + 1));
				}
			}
		}
		return true;
	}

	public boolean decompress(String file) {
		if (Os.isReflectionTest()) {
			return true;
		}
		if (file == null) {
			return false;
		}
		try {
			TarArchiveInputStream tis = TarArchiveInputStream.create(file + ".tgz");
			if (tis == null) {
				return false;
			}
			TarArchiveEntry tarEntry = null;
			while ((tarEntry = tis.getNextTarEntry()) != null) {
				String outputName;
				if (tarEntry.getName().startsWith("package/")) {
					outputName = file + "/" + tarEntry.getName().substring(8);
				} else {
					outputName = "node_modules/" + tarEntry.getName();
				}
				File outputFile = new File(outputName);
				if (tarEntry.isDirectory()) {
//					SSystem.out..println("outputFile Directory ---- "+ outputFile.getAbsolutePath());
					if (!outputFile.exists()) {
						outputFile.mkdirs();
					}
				} else {
//					SSystem.out..println("outputFile File ---- " + outputFile.getAbsolutePath());
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

	@Override
	public boolean update(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public static String getUserEMail() {
		CharacterBuffer userEMail = SimpleController.executeProcess("git config --global user.email");
		if(userEMail != null && userEMail.startsWith("ERROR") == false) {
			return userEMail.trim().toString();
		}
		return "";
	}
		
	public static String getUserName() {
		CharacterBuffer userName = SimpleController.executeProcess("git config --global user.name");
		if(userName != null && userName.startsWith("ERROR") == false) {
			return userName.trim().toString();
		}
		return System.getProperty("user.name");
	}
	
	public CharacterBuffer addAtrifact(CharacterBuffer sb, String projectName, String licence) {
		if(sb == null) {
			sb = new CharacterBuffer();
		}
		if(projectName == null) {
			return sb;
		}
		sb.withLine("ext{");
		sb.withLine("	sharedManifest = manifest {");
		sb.withLine("		attributes");
		sb.withLine("		'Specification-Version': gitVersion.major+\".\"+gitVersion.minor+\".\"+gitVersion.revision,");
		sb.withLine("		'Implementation-Title': '"+projectName+"',");
		sb.withLine("		'Specification-Title': '"+projectName+"',");
		sb.withLine("		'Built-Time': gitVersion.buildTime,");
		sb.withLine("		'Created-By': gitVersion.major+\".\"+gitVersion.minor+\".\"+gitVersion.revision+\" \"+System.properties['user.name'],");
		sb.withLine("		'Build': (System.getenv(\"BUILD_NUMBER\") ?: \"IDE\"),");
		sb.withLine("		'Built-By': \"${System.properties['user.name']}\",");
		sb.withLine("		'Location': \"${System.properties['user.language']}\",");
		sb.withLine("		'Version': gitVersion.revision,");
		sb.withLine("		'Author': '"+getUserName()+"',");
		sb.withLine("		'Implementation-Version': gitVersion.major+\".\"+gitVersion.minor+\".\"+gitVersion.revision,");
		sb.withLine("		'GIT-Revision': gitVersion.revision,");
		sb.withLine("		'Hash': gitVersion.hash,");
		sb.withLine("		'Java-Version': JavaVersion.current(),");
		sb.withLine("		'Bundle-Description': '"+projectName+"',");
		sb.withLine("		'Coverage': gitVersion.coverage,");
		if(licence != null) {
			sb.withLine("		'Licence': '"+licence+"',"); 
		}
		sb.withLine("		'Bundle-ClassPath': '.'");
		sb.withLine("		}");
		sb.withLine("	}");
		
		
		HTMLEntity response = NodeProxyTCP.getHTTP("https://opensource.org/licenses/"+licence, new HTMLEntity());
		if(response != null) {
			XMLEntity body = response.getBody();
			Entity content = body.getElementBy("CLASS", "content");
			if(content == null && body.getValue() != null) {
				String items = body.getValue();
				int pos = items.indexOf("<div class=\"field-items\">");
				if(pos>0) {
					content = new XMLEntity().withValue(body.getValue().substring(pos));
//					body = body.getElementBy("CLASS", "content");
				}
			}
			if(content != null && content instanceof XMLEntity) {
				CharacterBuffer text = getLicenceText((XMLEntity) content, new CharacterBuffer(), projectName);
				if(text != null && text.length()>0) {
					FileBuffer.writeFile("licence.txt", text.toString());
				}
			}
		}
		
		/*	'Licence': 'MIT and Apache License 2.0',
					'Homepage': 'https://github.com/fujaba/Networkparser',
					'scm': 'git@github.com/fujaba/Networkparser.git',

					'Main-Class': 'de.uniks.networkparser.ext.DiagramEditor',
					*/
		return sb;
	}
	
	public static CharacterBuffer getLicenceText(XMLEntity entity, CharacterBuffer buffer, String projectName) {
		if(entity.sizeChildren()>0) {
			for(int c=0;c<entity.sizeChildren();c++) {
				getLicenceText((XMLEntity) entity.getChild(c), buffer, projectName);
			}
		} else {
			if("p".equals(entity.getTag()) && entity.size() <1) {
				String value = entity.getValue();
				value = value.replace("<COPYRIGHT HOLDER>", projectName);
				value = value.replace("&lt;COPYRIGHT HOLDER&gt;", projectName);
				
				value = value.replace("<YEAR>", ""+new DateTimeEntity().get(DateTimeEntity.YEAR));
				value = value.replace("&lt;YEAR&gt;", ""+new DateTimeEntity().get(DateTimeEntity.YEAR));
				buffer.withLine(value);
			}
		}
		return buffer;
	}
}
