package de.uniks.networkparser.ext.generic;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class JarValidator {
	private int minCoverage=0;
	private String path;
	private String file = "build/jacoco/html/index.html";
	public static final String JARFILE = ".jar";
	public static final String CLASSFILESUFFIX = ".class";
	private ArrayList<String> warnings = new ArrayList<String>();
	private TreeSet<String> warningsPackages = new TreeSet<String>();
	private ArrayList<String> errors = new ArrayList<String>();
	private ArrayList<String> mergePackages = new ArrayList<String>();
	private boolean isExistFullJar;
	private boolean isUseJUnit;
	SimpleKeyValueList<String, JsonObject> projects = new SimpleKeyValueList<String, JsonObject>();
	private String rootPath = "";
	
	public JarValidator withMinCoverage(int no) {
		this.minCoverage = no;
		return this;
	}
	
	public JarValidator withPath(String path) {
		this.path = path;
		return this;
	}
	
	private TreeSet<String> getDependency(CharacterBuffer executeProcess, String search) {
		int pos = executeProcess.indexOf("##"+search+"##");
		TreeSet<String> packages=new TreeSet<String>();
		if(pos>0) {
			System.out.println("FOUND DEPENDENCY:" +search);
			int end = executeProcess.indexOf("##"+search+"END##", pos);
			if(end>0) {
				CharacterBuffer subSequence = executeProcess.subSequence(pos+search.length() + 4, end);
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
		return packages;
	}
	
	public void validate() {
		CharacterBuffer script = FileBuffer.readFile(rootPath+"build.gradle");
		this.isUseJUnit = script.indexOf("useJUnitPlatform()")>0;

		script.newLine();
		script.newLine();
		script.withLine("task showDependency() {");
		script.withLine("def listMain=new TreeSet();");
		script.withLine("def listTest=new TreeSet();");
		script.withLine("sourceSets.each{ if(it ==sourceSets.test) {");
		script.withLine("		it.java.each { listTest.add( it.getParentFile().getAbsolutePath().toString()) }");
		script.withLine("	} else {");
		script.withLine("		it.java.each { listMain.add( it.getParentFile().getAbsolutePath().toString()) }");
		script.withLine("	}");
		script.withLine("}");
		script.withLine("println \"##SRCDEPENDENCY##\"");
		script.withLine("listMain.each{ println it; }");
		script.withLine("sourceSets.main.compileClasspath.each { println it }");
		script.withLine("println \"##SRCDEPENDENCYEND##\"");
		
		script.withLine("println \"##TESTDEPENDENCY##\"");
		script.withLine("listTest.each{ println it; }");
		script.withLine("sourceSets.test.compileClasspath.each { println it }");
		script.withLine("println \"##TESTDEPENDENCYEND##\"");
		script.withLine("}");

		FileBuffer.writeFile(rootPath+"test.gradle", script.toString());

		CharacterBuffer executeProcess = SimpleController.executeProcess(rootPath+"gradlew", "showDependency", "-b", "test.gradle");
		FileBuffer.writeFile(rootPath+"test.out", executeProcess.toString());
		if(executeProcess.length()>0) {
			System.out.println("CHECK DEPENDENCY: "+new File(rootPath+"test.gradle").exists());
		}else {
			System.out.println("ERROR DEPENDENCY: "+new File(rootPath+"test.gradle").exists());
			System.out.println(executeProcess.toString());
		}
		ArrayList<String> packages = this.mergePacking(getDependency(executeProcess, "SRCDEPENDENCY"));
		ArrayList<String> testPackages = this.mergePacking(getDependency(executeProcess, "TESTDEPENDENCY"));

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
		
		TreeSet<String> dependency = new TreeSet<String>();
		if(packages.size()>0) {
			script.withLine("sourceSets.main.java.srcDirs(");
			for(String item : packages) {
				item = item.replace('\\', '/');
				if(item.toLowerCase().endsWith(".jar")) {
					dependency.add(item);
				} else {
					script.withLine("	\""+item +"\",");
				}
			}
			script.withLine("	\"src/main/java/\"");
			script.withLine(")");
			
		}
		
		if(testPackages.size()>0) {
			script.withLine("sourceSets.test.java.srcDirs(");
			for(String item : testPackages) {
				item = item.replace('\\', '/');
				if(item.toLowerCase().endsWith(".jar")) {
					dependency.add(item);
				} else {
					script.withLine("	\""+item +"\",");
				}
			}
			script.withLine("	\"src/test/java/\"");
			script.withLine(")");
		}

		script.withLine("dependencies {");
		script.withLine("	// Test framework");
		script.withLine("	compile 'org.junit.jupiter:junit-jupiter-api:5.+'");
		script.withLine("	compile 'org.junit.jupiter:junit-jupiter-engine:5.+'");
		for(String item : dependency) {
			script.withLine("compile files(\""+item+"\")");
		}
		script.withLine("}");
		script.withLine("test {");
		if(this.isUseJUnit) {
			script.withLine("	useJUnitPlatform()");
		}
		script.withLine("	finalizedBy jacocoTestReport");
		script.withLine("}");
		
		script.withLine("defaultTasks 'clean' 'test'");
		FileBuffer.writeFile(rootPath+"jacoco.gradle", script.toString());
		executeProcess = SimpleController.executeProcess(rootPath+"gradlew", "-b", "jacoco.gradle");
		FileBuffer.writeFile(rootPath+"jacoco.out", executeProcess.toString());
		if(executeProcess.length()>0) {
			System.out.println("CHECK TEST: "+new File(rootPath+"jacoco.gradle").exists());
		}else {
			System.out.println("ERROR TEST: "+new File(rootPath+"jacoco.gradle").exists());
			System.out.println(executeProcess.toString());
		}
	}
	
	public boolean analyseReport() {
		File file = new File(rootPath+this.file);
		if(file.exists()) {
			
			CharacterBuffer content = FileBuffer.readFile(file);
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
		} else {
			System.out.println("File not found:" +this.file);
		}
		return false;
	}
	
	public int searchFiles(boolean output, boolean isLicence) {
		if(this.path == null) {
			return -1;
		}
		return searching(new File(rootPath+this.path), output, isLicence);
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
	
	public int searching(File file, boolean output, boolean isLicence) {
		if(file == null) {
			return -1;
		}
		int result = 0;
		File[] listFiles = file.listFiles();
		if(listFiles == null) {
			return -1;
		}
		for(File child : listFiles) {
			if(child.isDirectory()) {
				int subresult = searching(child, output, isLicence);
				if(subresult < 0 ) {
					result += subresult;
				}
			} else {
				// Analyse File
				// Check if it is a java file
				String fileName = child.getName().toLowerCase();
				if (fileName.endsWith(JARFILE)) {
					System.out.println("FOUND: " + child.toString() + " ("+child.length()+")");
					if (analyseFile(child) == false) {
						if(isError() == false) {
							System.out.println("Everything is ok ("+file+")");
						} else {
							this.mergePackages.clear();
							this.mergePackages.addAll(mergePacking(warningsPackages));
							if(this.mergePackages.size()<1) {
								System.out.println("May be not the fatJar ("+child.toString()+")");
							} else {
								this.isExistFullJar = true;
								if(output){
									System.err.println("There are "+errors.size()+" Errors in Jar ("+child.toString()+")");
									for(String entry : errors) {
										System.err.println("- Can't create instance of "+entry);
									}
									System.out.println("There are "+warnings.size()+" Warnings in Jar ("+child.toString()+")");
									for(String entry : warnings) {
										System.out.println("- Not necessary file "+entry);
									}
								}
							}
						}
						if(isLicence) {
							SimpleKeyValueList<String, JsonObject> projects = mergePackages();
							for(int i=0;i<projects.size();i++) {
								System.out.print(projects.getKeyByIndex(i));
								JsonObject elements = projects.getValueByIndex(i);
								JsonObject last = (JsonObject) elements.getJsonArray("docs").first();
								String group = last.getString("g").replace('.', '/');
								String url = group+"/"+last.getString("a")+"/"+last.getString("v")+"/";
								url+=last.getString("a")+"-" + last.getString("v")+".pom";
								HTMLEntity pom = NodeProxyTCP.getHTTP("http://search.maven.org/remotecontent?filepath="+url);
								XMLEntity body = pom.getBody();
								Entity nameTag = body.getElementBy(XMLEntity.PROPERTY_TAG, "name");
								if(nameTag!= null) {
									System.out.print(" - "+((XMLEntity)nameTag).getValue());
								}
								XMLEntity licences = (XMLEntity) body.getElementBy(XMLEntity.PROPERTY_TAG, "licenses");
								if(licences != null) {
									for(int l=0;l<licences.sizeChildren();l++) {
										XMLEntity licence = (XMLEntity) licences.getChild(l);
										if("license".equalsIgnoreCase(licence.getTag())) {
											System.out.print(" - ");
											System.out.print(((XMLEntity)licence.getElementBy(XMLEntity.PROPERTY_TAG, "name")).getValue());
											System.out.print(" - ");
											System.out.print(((XMLEntity)licence.getElementBy(XMLEntity.PROPERTY_TAG, "url")).getValue());
										}
									}
								}
								System.out.print(BaseItem.CRLF);
							}
						}
						result = count() * -1;
					}
				}
			}
		}
		return result;
	}
	
	public boolean isExistFullJar() {
		return isExistFullJar;
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
	
	public void clear() {
		this.warnings.clear();
		this.warningsPackages.clear();
		this.mergePackages.clear();
		this.errors.clear();
	}
	
	public ArrayList<String> mergePacking(TreeSet<String> sources) {
		ArrayList<String> dep = new ArrayList<String>();
		dep.addAll(sources);
		for(int i=dep.size()-1;i>0;i--) {
			for(int z=i-1;z>=0;z--) {
				if(dep.get(i).startsWith(dep.get(z))) {
					dep.remove(i);
					z=0;
				}
			}
		}
		boolean copy=false;
		if(dep.size()>1) {
			copy=true;
		}else if (dep.size() == 1) {
			if(dep.get(0).equalsIgnoreCase("de")|| dep.get(0).equalsIgnoreCase("de.uniks") || dep.get(0).equalsIgnoreCase("de.uniks.networkparser")) {
				copy = true;
			}
		}
		if(copy == false) {
			dep.clear();
		}
		return dep;
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
			clear();
	
			for (Enumeration<? extends JarEntry> jarEntries = jarFile.entries(); jarEntries.hasMoreElements();) {
				JarEntry jarEntry = jarEntries.nextElement();
				String name = jarEntry.getName().toLowerCase(); 
				if (name.endsWith(".class")) {
					if(name.indexOf("$")<0 && name.split("/").length>2) {
						int pos = name.lastIndexOf("/");
						warningsPackages.add(name.substring(0, pos).replaceAll("/", "."));
					}
				} else {
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
					
					// Find Constructor
					Object newInstance = ReflectionLoader.newInstanceSimple(wantedClass);
					if(newInstance == null) {
						errors.add(jarEntry.getName());
					}
				} catch (Exception e) {
					errors.add(jarEntry.getName() + "-"+e.getMessage());
				}
			}
		} catch (Throwable e) {
			errors.add(file.getName() + "-"+e.getMessage());
//			e.printStackTrace();
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

	private boolean isProject(String item) {
		for(int i=0;i<projects.size();i++) {
			if(item.startsWith(projects.getKeyByIndex(i))) {
				// SUB PACKAGE OF FOUND PROJECT
				return false;
			}
		}
		return true;
	}
	private JsonObject getHTTPJson(String value) {
		HTMLEntity http = NodeProxyTCP.getHTTP("http://search.maven.org/solrsearch/select?rows=20&wt=json&q=fc:"+value);
		XMLEntity body = http.getBody();
		if(body != null && body.getValue() != null) {
			return JsonObject.create(body.getValue());
		}
		return null;
	}
	
	public SimpleKeyValueList<String, JsonObject> mergePackages() {
//		list.addAll(this.warningsPackages);
		projects.clear();
		TreeSet<String> cache = new TreeSet<String>();
		cache.add("de.uniks");
		cache.add("de");
		for(String item : this.warningsPackages) {
			if(isProject(item)) {
				JsonObject json = getHTTPJson(item);
				if(json != null) {
					JsonObject responseJson = json.getJsonObject("response");
					if(responseJson.getInt("numFound")>0) {
						// FOUND IT
						projects.add(item, responseJson);
					} else {
						// Search for SubPackage???
						String[] split = item.split("\\.");
						for(int i=split.length-1;i>0;i--) {
							String search=split[0];
							for(int z=1;z<i;z++) {
								search+="."+split[z];
							}
							if(cache.contains(search)) {
								continue;
							}
							json = getHTTPJson(search);
							if(json != null) {
								responseJson = json.getJsonObject("response");
								if(responseJson.getInt("numFound")>0) {
									// FOUND IT
									projects.add(search, responseJson);
									break;
								} else {
									cache.add(search);
								}
							}
						}
					}
				}
			}
		}
		return projects;
		
	}

	public int getMinCoverage() {
		return this.minCoverage;
	}

	public JarValidator withRootPath(String param) {
		this.rootPath = param;
		return this;
	}
}
