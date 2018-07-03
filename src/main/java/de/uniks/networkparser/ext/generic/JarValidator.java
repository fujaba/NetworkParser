package de.uniks.networkparser.ext.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.io.FileBuffer;

public class JarValidator {
	private int minCoverage=0;
	private String path;
	private String file = "build/jacoco/html/index.html";
	public static final String JARFILE = ".jar";
	public static final String CLASSFILESUFFIX = ".class";
	private ArrayList<String> warnings = new ArrayList<String>();
	private ArrayList<String> errors = new ArrayList<String>();
	
	public JarValidator withMinCoverage(int no) {
		this.minCoverage = no;
		return this;
	}
	
	public JarValidator withPath(String path) {
		this.path = path;
		return this;
	}
	
	public void validate() {
		CharacterBuffer script = FileBuffer.readFile("build.gradle");
		script.withLine("task showDependency() {");
		script.withLine("def list=new TreeSet();");
		script.withLine("sourceSets.each{ it.java.each {");
		script.withLine("	list.add( it.getParentFile().getAbsolutePath().toString())");
		script.withLine("} }");
		script.withLine("def dep = new ArrayList<String>()");
		script.withLine("dep.addAll(list)");
		script.withLine("for(int i=dep.size()-1;i>=0;i--) {");
		script.withLine("   if(i>0) {");
		script.withLine("      for(int z=i-1;z>=0;z--) {");
		script.withLine("         if(dep.get(i).startsWith(dep.get(z))) {");
		script.withLine("            dep.remove(i)");
		script.withLine("            z=0;");
		script.withLine("         }");
		script.withLine("      }");
		script.withLine("   }");
		script.withLine("}");
		script.withLine("println \"##DEPENDENCY##\"");
		script.withLine("dep.each{ println it; }");
		script.withLine("println \"##ENDDEPENDENCY##\"");
		script.withLine("}");

		FileBuffer.writeFile("test.gradle", script.toString());

		CharacterBuffer executeProcess = SimpleController.executeProcess("gradlew", "showDependency", "-b", "test.gradle");
		int pos = executeProcess.indexOf("##DEPENDENCY##");
		ArrayList<String> packages=new ArrayList<String>();
		if(pos>0) {
			int end = executeProcess.indexOf("##ENDDEPENDENCY##", pos);
			if(end>0) {
				CharacterBuffer subSequence = executeProcess.subSequence(pos+14, end);
				end = subSequence.indexOf('\n');
				pos = 0;
				while(end>0) {
					String p = subSequence.subSequence(pos, end).toString().trim();
					if(p.isEmpty() == false) {
						packages.add(p);
					}
					pos = end;
					end = subSequence.indexOf('\n', pos+1);
				}
			}
		}
		script = new CharacterBuffer(); 
		script.withLine("repositories { jcenter() }");
		script.withLine("apply plugin: 'java'");
		script.withLine("apply plugin: 'maven'");
		script.withLine("apply plugin: 'jacoco'");
		script.withLine("jacoco.toolVersion = \"0.8.+\"");
		script.withLine("test {");
		script.withLine("	useJUnit{");
		script.withLine("		dependencies{");
		script.withLine("			implementation(group: 'junit', name: 'junit', version: '[4,)')");
		script.withLine("		}");
		script.withLine("	}");
		script.withLine("	testLogging {");
		script.withLine("		events \"FAILED\", \"SKIPPED\"");
		script.withLine("		exceptionFormat \"short\"");
		script.withLine("		showStackTraces	true");
		script.withLine("		showStandardStreams true");
		script.withLine("		showCauses true");
		script.withLine("	}");
		script.withLine("	reports.junitXml {");
		script.withLine("		enabled true");
		script.withLine("		destination new File(\"${buildDir}/reports/\")");
		script.withLine("	}");
		script.withLine("}");
		script.withLine("jacocoTestReport {");
		script.withLine("	group = \"Reporting\"");
		script.withLine("	description = \"Generate Jacoco coverage reports after running tests.\"");
		script.withLine("	executionData = files(\"${buildDir}/jacoco/test.exec\")");
		script.withLine("	reports {");
		script.withLine("		csv.enabled false");
		script.withLine("		xml.enabled = true");
		script.withLine("		xml.destination = new File(\"${buildDir}/test-results/jacoco.xml\")");
		script.withLine("		html.enabled = true");
		script.withLine("		html.destination = new File(\"${buildDir}/jacoco/html\")");
		script.withLine("	}");
		script.withLine("}");
		
		if(packages.size()>0) {
			script.withLine("sourceSets.main.java.srcDirs(");
			for(String item : packages) {
				script.withLine("	\""+item+"\",");
			}
			script.withLine(")");
		}
		
		script.withLine("defaultTasks 'test', 'jacocoTestReport'");
		FileBuffer.writeFile("jacoco.gradle", script.toString());
		
		executeProcess = SimpleController.executeProcess("gradlew", "-b", "jacoco.gradle");
		System.out.println(executeProcess);
	}
	
	public boolean analyseReport() {
		File file = new File(this.file);
		if(file.exists()) {
			byte[] buffer = new byte[ (int) file.length() ];
			try {
				InputStream in = new FileInputStream( file );
				in.read( buffer );
				in.close();
			} catch (IOException e) {
				buffer = null;
			}
			if(buffer != null) {
				String content = new String(buffer, Charset.forName("UTF-8"));
				String search = "<td class=\"ctr2\">";
				int pos = content.indexOf(search);
				if(pos>0) {
					int end = content.indexOf("</td", pos);
					pos+=search.length();
					String cc = content.substring(pos, end);
					cc = cc.replaceAll("&nbsp;", "");
					cc = cc.replaceAll("%", "");
					cc = cc.replace((char)160, ' ');
					cc = cc.trim();
					System.out.println("Found: "+cc);
					int no = Integer.valueOf(cc);
					if(no >= this.minCoverage) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean searchFiles() {
		if(this.path == null) {
			return false;
		}
		return searching(new File(this.path));
	}
	
	public boolean isError() {
		return warnings.size() > 0 || errors.size() > 0;
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

	public ArrayList<String> getWarnings() {
		return warnings;
	}
	
	public boolean searching(File file) {
		if(file == null) {
			return false;
		}
		boolean result = true;
		for(File child : file.listFiles()) {
			if(child.isDirectory()) {
				if(searching(child) == false) {
					result = false;
				}
			} else {
				// Analyse File
				// Check if it is a java file
				String fileName = child.getName().toLowerCase();
				if (fileName.endsWith(JARFILE)) {
					System.out.println("FOUND: " + child.toString());
					if (analyseFile(child) == false) {
						result = false;
					}
				}
			}
		}
		return result;
	}
	
	public boolean printAnalyse() {
		if(isError() == false) {
			System.out.println("Everything is ok");
			return false;
		}
		System.err.println("There are "+errors.size()+" Errors in Jar");
		for(String entry : errors) {
			System.err.println("- Can't create instance of "+entry);
		}
		System.out.println("There are "+warnings.size()+" Warnings in HeadlessJar");
		for(String entry : warnings) {
			System.out.println("- Not necessary file "+entry);
		}
		return true;
	}

	public int count() {
		int count = 0;
		if(errors != null) {
			count += errors.size();
		}
		if(warnings!= null) {
			count += warnings.size();
		}
		return count;
	}
	
	private boolean analyseFile(File file) {
		if(file == null || file.exists() == false) {
			return false;
		}
		JarClassLoader jarClassLoader = null;
		JarFile jarFile = null;
		try {
			jarClassLoader = new JarClassLoader(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
			jarFile = new JarFile(file);
	
			for (Enumeration<? extends JarEntry> jarEntries = jarFile.entries(); jarEntries.hasMoreElements();) {
				JarEntry jarEntry = jarEntries.nextElement();
				String name = jarEntry.getName().toLowerCase(); 
				if (name.endsWith(".class") == false) {
					if (name.endsWith(".jpg")) {
						warnings.add(jarEntry.getName());
					} else if (name.endsWith(".png")) {
						if(name.equals("de/uniks/networkparser/np.png") == false) {
							warnings.add(jarEntry.getName());
						}
					} else if (name.endsWith(".obj")) {
						warnings.add(jarEntry.getName());
					} else if (name.endsWith(".mtl")) {
						warnings.add(jarEntry.getName());
					} else if (name.endsWith(".jar")) {
						warnings.add(jarEntry.getName());
					} else if (name.endsWith(".java")) {
						warnings.add(jarEntry.getName());
					} else if (name.endsWith(".html")) {
						warnings.add(jarEntry.getName());
					}
					continue;
				}
	
				String entryName = jarEntry.getName();
				entryName = entryName.substring(0, entryName.length() - ".class".length());
				entryName = entryName.replace("/", ".");
				try {
					Class<?> wantedClass = jarClassLoader.loadClass(entryName);
					if(Modifier.isAbstract(wantedClass.getModifiers())) {
						continue;
					}
					if(wantedClass.isEnum() || wantedClass.isInterface() || wantedClass.isPrimitive()) {
						continue;
					}
					if(Modifier.isPublic(wantedClass.getModifiers()) == false) {
						continue;
					}
					if(Modifier.isStatic(wantedClass.getModifiers())) {
						continue;
					}
					for(Method method : wantedClass.getMethods()) {
						method.getName();
//	//					System.out.println(method.getReturnType());
					}
					Constructor<?> emptyConstructor = wantedClass.getConstructor();
					if (emptyConstructor != null) {
						if(Modifier.isPublic(emptyConstructor.getModifiers()) == false) {
							continue;
						}
						Object newInstance = ReflectionLoader.newInstance(wantedClass);
						if(newInstance == null) {
							errors.add(jarEntry.getName());
					}
				}
			} catch (Exception e) {
				errors.add(jarEntry.getName() + "-"+e.getMessage());
			}
		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(jarClassLoader != null) {
					jarClassLoader.close();
				}
				if(jarFile != null) {
					jarFile.close();
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
}
