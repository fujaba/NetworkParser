package de.uniks.networkparser.ext.generic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.io.BufferedByteInputStream;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.ext.petaf.SimpleTimerTask;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.ArtifactFile;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class JarValidator.
 *
 * @author Stefan
 */
public class JarValidator
{
   private String path;
   private String file = "build/jacoco/html/index.html";
   
   /** The Constant JARFILE. */
   public static final String JARFILE = ".jar";
   
   /** The Constant CLASSFILESUFFIX. */
   public static final String CLASSFILESUFFIX = ".class";
   
   /** The Constant MAINTAG. */
   public static final String MAINTAG = "JARVALIDATOR";
   private ArrayList<String> warnings = new ArrayList<String>();
   private TreeSet<String> warningsPackages = new TreeSet<String>();
   private ArrayList<String> errors = new ArrayList<String>();
   private ArrayList<String> mergePackages = new ArrayList<String>();
   private boolean isExistFullJar;
   private boolean isUseJUnit;
   SimpleKeyValueList<String, JsonObject> projects = new SimpleKeyValueList<String, JsonObject>();
   private String rootPath = "";
   private int count;
   private int classes;


   private int minCoverage = 0;
   private boolean isAnalyse = false;
   
   /** The is analyse jar. */
   public boolean isAnalyseJar = false;
   
   /** The is licence. */
   public boolean isLicence = false;
   
   /** The is error. */
   public boolean isError = true;
   
   /** The is warning. */
   public boolean isWarning = true;
   
   /** The is instance. */
   public boolean isInstance = true;
   
   /** The is validate. */
   public boolean isValidate = false;
   
   /** The instance package. */
   public String instancePackage;
   private NetworkParserLog logger = new NetworkParserLog().withListener(new StringPrintStream());

   /**
    * With min coverage.
    *
    * @param value the value
    * @return the jar validator
    */
   public JarValidator withMinCoverage(int value)
   {
      this.minCoverage = value;
      this.isValidate = true;
      return this;
   }

   /**
    * With path.
    *
    * @param path the path
    * @return the jar validator
    */
   public JarValidator withPath(String path)
   {
      this.path = path;
      this.isAnalyseJar = true;
      return this;
   }

   private TreeSet<String> getDependency(CharacterBuffer executeProcess, String search)
   {
      int pos = executeProcess.indexOf("##" + search + "##");
      TreeSet<String> packages = new TreeSet<String>();
      if (pos > 0)
      {
         logger.print(this, "FOUND DEPENDENCY:" + search);
         int end = executeProcess.indexOf("##" + search + "END##", pos);
         if (end > 0)
         {
            CharacterBuffer subSequence = executeProcess.subSequence(pos + search.length() + 4, end);
            end = subSequence.indexOf('\n');
            pos = 0;
            while (end > 0)
            {
               String p = subSequence.subSequence(pos, end).toString().trim();
               if (!p.isEmpty())
               {
                  packages.add(p);
               }
               pos = end;
               end = subSequence.indexOf('\n', pos + 1);
            }
         }
      }
      return packages;
   }

   /**
    * Validate.
    */
   public void validate()
   {
      if (ReflectionBlackBoxTester.isTester())
      {
         return;
      }

      CharacterBuffer script = FileBuffer.readFile(rootPath + "build.gradle");
      this.isUseJUnit = script.indexOf("useJUnitPlatform()") > 0;
      int pos = script.indexOf("org.glassfish.jersey.inject:");
      String jerseyInject = script.getLine(pos).toString();
      pos = script.indexOf("org.glassfish.jersey.media:");
      String jerseyMedia = script.getLine(pos).toString();

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

      FileBuffer.writeFile(rootPath + "test.gradle", script.toString());

      CharacterBuffer command = new CharacterBuffer();
      CharacterBuffer executeProcess = SimpleController.executeProcess(
         command,
         rootPath + "gradlew",
         "showDependency",
         "-b",
         rootPath + "test.gradle");
      String value = command.toString() + BaseItem.CRLF + executeProcess.toString();
      FileBuffer.writeFile(rootPath + "test.out", value);
      if (executeProcess.length() > 0)
      {
         logger.print(this, "CHECK DEPENDENCY: " + new File(rootPath + "test.gradle").exists());
      }
      else
      {
         logger.print(this, "ERROR DEPENDENCY: " + new File(rootPath + "test.gradle").exists());
         logger.print(this, executeProcess.toString());
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
      if (this.isUseJUnit)
      {
         script.withLine("	useJUnitPlatform()");
      }
      script.withLine("	finalizedBy jacocoTestReport");
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
      if (packages.size() > 0)
      {
         script.withLine("sourceSets.main.java.srcDirs(");
         for (String item : packages)
         {
            item = item.replace('\\', '/');
            if (item.toLowerCase().endsWith(".jar"))
            {
               dependency.add(item);
            }
            else
            {
               script.withLine("	\"" + item + "\",");
            }
         }
         script.withLine("	\"src/main/java/\"");
         script.withLine(")");

      }

      if (testPackages.size() > 0)
      {
         script.withLine("sourceSets.test.java.srcDirs(");
         for (String item : testPackages)
         {
            item = item.replace('\\', '/');
            if (item.toLowerCase().endsWith(".jar"))
            {
               dependency.add(item);
            }
            else
            {
               script.withLine("	\"" + item + "\",");
            }
         }
         script.withLine("	\"src/test/java/\"");
         script.withLine(")");
      }

      script.withLine("dependencies {");
      script.withLine("	/* Test framework */");
      script.withLine("	compile 'org.junit.jupiter:junit-jupiter-api:5.+'");
      script.withLine("	compile 'org.junit.jupiter:junit-jupiter-engine:5.+'");
      for (String item : dependency)
      {
         script.withLine("compile files(\"" + item + "\")");
      }
      script.withLine(jerseyInject);
      script.withLine(jerseyMedia);

      script.withLine("}");
      script.withLine("defaultTasks 'test'");
      FileBuffer.writeFile(rootPath + "jacoco.gradle", script.toString());

      command = new CharacterBuffer();
      executeProcess = SimpleController.executeProcess(
         command,
         rootPath + "gradlew",
         "-b",
         rootPath + "jacoco.gradle",
         "--stacktrace");
      value = command.toString() + BaseItem.CRLF + executeProcess.toString();
      FileBuffer.writeFile(rootPath + "jacoco.out", value);

      if (executeProcess.length() > 0)
      {
         logger.print(this, "CHECK TEST: " + new File(rootPath + "jacoco.gradle").exists());
      }
      else
      {
         logger.print(this, "ERROR TEST: " + new File(rootPath + "jacoco.gradle").exists());
         logger.print(this, executeProcess.toString());
      }
   }

   /**
    * Analyse report.
    *
    * @return the int
    */
   public int analyseReport()
   {
      File file = new File(rootPath + this.file);
      if (file.exists())
      {
         CharacterBuffer content = FileBuffer.readFile(file);
         String search = "<td class=\"ctr2\">";
         int pos = content.indexOf(search);
         if (pos > 0)
         {
            int end = content.indexOf("</td", pos);
            pos += search.length();
            String cc = content.substring(pos, end);
            cc = cc.replace("&nbsp;", "");
            cc = cc.replace("%", "");
            cc = cc.replace((char) 160, ' ');
            cc = cc.trim();
            logger.print(this, "Found CC: " + cc);
            int no = Integer.parseInt(cc);
            if (no >= this.minCoverage)
            {
               return 0;
            }
            return no;
         }
      }
      else
      {
         logger.print(this, "File not found:" + this.file);
      }
      return -1;
   }

   /**
    * Search files.
    *
    * @param output the output
    * @return the int
    */
   public int searchFiles(PrintStream output)
   {
      if (this.path == null)
      {
         return -1;
      }
      return searching(new File(rootPath + this.path), output);
   }

   /**
    * Checks if is error.
    *
    * @return true, if is error
    */
   public boolean isError()
   {
      return warnings.size() > 0 || errors.size() > 0;
   }

   /**
    * Gets the errors.
    *
    * @return the errors
    */
   public ArrayList<String> getErrors()
   {
      return errors;
   }

   /**
    * Gets the warnings.
    *
    * @return the warnings
    */
   public ArrayList<String> getWarnings()
   {
      return warnings;
   }

   /**
    * Searching.
    *
    * @param file the file
    * @param output the output
    * @return the int
    */
   public int searching(File file, PrintStream output)
   {
      if (file == null)
      {
         if (output != null)
         {
            output.println("NO FILES FOUND");
         }
         return -1;
      }
      int result = 0;
      File[] listFiles = file.listFiles();
      if (listFiles == null)
      {
         if (output != null)
         {
            output.println("NO FILES FOUND: " + file.getPath());
         }
         return -1;
      }
      for (File child : listFiles)
      {
         if (child.isDirectory())
         {
            int subresult = searching(child, output);
            if (subresult < 0)
            {
               result += subresult;
            }
         }
         else
         {
            /* Analyse File - Check if it is a java file */
            String fileName = child.getName().toLowerCase();
            if (fileName.endsWith(JARFILE))
            {
               if (output != null)
               {
                  output.println("FOUND: " + child.toString() + " (" + child.length() + ")");
               }
               if (analyseFile(child))
               {
                  if (!isError())
                  {
                     if (output != null)
                     {
                        output.println("Everything is ok (" + file + ")");
                     }
                  }
                  else
                  {
                     this.mergePackages.clear();
                     this.mergePackages.addAll(mergePacking(warningsPackages));
                     if (this.mergePackages.size() < 1)
                     {
                        if (output != null)
                        {
                           output.println("May be not the fatJar (" + child.toString() + ")");
                        }
                     }
                     else
                     {
                        this.isExistFullJar = true;
                        if (output != null)
                        {
                           output.println(
                              "There are " + errors.size() + " Errors in Jar (" + child.toString() + ")");
                           if (isWarning)
                           {
                              for (String entry : errors)
                              {
                                 output.println("- Can't create instance of " + entry);
                              }
                           }
                           output.println(
                              "There are " + warnings.size() + " Warnings in Jar ("
                                 + child.toString() + ")");
                           if (isWarning)
                           {
                              for (String entry : warnings)
                              {
                                 if (output != null)
                                 {
                                    output.println("- Not necessary file " + entry);
                                 }
                              }
                           }
                        }
                     }
                  }
                  if (isLicence)
                  {
                     logger.print(this, "Classes: " + this.classes + "/" + this.count);
                     SimpleKeyValueList<String, JsonObject> projects = mergePackages();
                     for (int i = 0; i < projects.size(); i++)
                     {
                        if (output != null)
                        {
                           output.print(projects.getKeyByIndex(i));
                        }
                        JsonObject elements = projects.getValueByIndex(i);
                        JsonObject last = (JsonObject) elements.getJsonArray("docs").first();
                        String group = last.getString("g").replace('.', '/');
                        String url = group + "/" + last.getString("a") + "/" + last.getString("v") + "/";
                        url += last.getString("a") + "-" + last.getString("v") + ".pom";
                        HTMLEntity pom = null;
                        try
                        {
                           pom = NodeProxyTCP.getHTTP("http://search.maven.org/remotecontent?filepath=" + url);
                        }
                        catch (Exception e)
                        {}
                        if (pom != null)
                        {
                           XMLEntity body = pom.getBody();
                           Entity nameTag = body.getElementBy(XMLEntity.PROPERTY_TAG, "name");
                           if (nameTag != null)
                           {
                              if (output != null)
                              {
                                 output.print(" - " + ((XMLEntity) nameTag).getValue());
                              }
                           }
                           XMLEntity licences = (XMLEntity) body.getElementBy(
                              XMLEntity.PROPERTY_TAG,
                              "licenses");
                           if (licences != null)
                           {
                              for (int l = 0; l < licences.sizeChildren(); l++)
                              {
                                 XMLEntity licence = (XMLEntity) licences.getChild(l);
                                 if ("license".equalsIgnoreCase(licence.getTag()))
                                 {
                                    if (output != null)
                                    {
                                       output.print(" - ");
                                       output.print(
                                          ((XMLEntity) licence
                                             .getElementBy(XMLEntity.PROPERTY_TAG, "name")).getValue());
                                       output.print(" - ");
                                       output.print(
                                          ((XMLEntity) licence
                                             .getElementBy(XMLEntity.PROPERTY_TAG, "url")).getValue());
                                    }
                                 }
                              }
                           }
                           if (output != null)
                           {
                              output.print(BaseItem.CRLF);
                           }
                        }
                     }
                  }
                  result = count() * -1;
               }
            }
         }
      }
      return result;
   }

   /**
    * Checks if is exist full jar.
    *
    * @return true, if is exist full jar
    */
   public boolean isExistFullJar()
   {
      return isExistFullJar;
   }

   /**
    * Count.
    *
    * @return the int
    */
   public int count()
   {
      int count = 0;
      if (errors != null)
      {
         count += errors.size();
      }
      if (warnings != null)
      {
         count += warnings.size();
      }
      return count;
   }

   /**
    * Clear.
    */
   public void clear()
   {
      this.warnings.clear();
      this.warningsPackages.clear();
      this.mergePackages.clear();
      this.errors.clear();
      this.count = 0;
      this.classes = 0;
   }

   /**
    * Merge packing.
    *
    * @param sources the sources
    * @return the array list
    */
   public ArrayList<String> mergePacking(TreeSet<String> sources)
   {
      ArrayList<String> dep = new ArrayList<String>();
      dep.addAll(sources);
      for (int i = dep.size() - 1; i > 0; i--)
      {
         for (int z = i - 1; z >= 0; z--)
         {
            if (dep.get(i).startsWith(dep.get(z)))
            {
               dep.remove(i);
               z = 0;
            }
         }
      }
      boolean copy = false;
      if (dep.size() > 1)
      {
         copy = true;
      }
      else if (dep.size() == 1)
      {
         if (dep.get(0).equalsIgnoreCase("de") || dep.get(0).equalsIgnoreCase("de.uniks")
            || dep.get(0).equalsIgnoreCase("de.uniks.networkparser"))
         {
            copy = true;
         }
      }
      if (!copy)
      {
         dep.clear();
      }
      return dep;
   }
   
   /**
    * Analyse pom.
    *
    * @param file the file
    * @param params the params
    * @return the simple set
    */
   public SimpleSet<ArtifactFile> analysePom(File file, boolean... params) {
	   SimpleSet<ArtifactFile> result = new SimpleSet<ArtifactFile>();
	   boolean simpleFormat = false;
	   try {
		   if(params != null && params.length >0) {
			   simpleFormat = params[0];
		   }
		   ZipInputStream stream = new ZipInputStream(new FileInputStream(file));
		   analysePom(stream, result, simpleFormat);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	   return result;
   }

   private void analysePom(ZipInputStream inputStream, SimpleSet<ArtifactFile> result, boolean simpleFormat) {
	   try {
	       ZipEntry entry;
	       while ((entry = inputStream.getNextEntry()) != null) {
	    	   String lName = entry.getName().toLowerCase();
	           this.count++;
	           if (lName.endsWith("pom.xml")) {
	        	   ByteBuffer buffer = new ByteBuffer();
	        	   buffer.addStream(inputStream);
	        	   XMLEntity xml =new XMLEntity().withValue(buffer);
	        	   ArtifactFile pomFile = new ArtifactFile().withSimpleFormat(simpleFormat).withValue(xml);
	        	   result.add(pomFile);
	           }else if (lName.endsWith(".jar")) {
	        	   try (BufferedByteInputStream stream = new BufferedByteInputStream()) {
	        		stream.withStream(inputStream);
					ZipInputStream childSubStream = new ZipInputStream(stream);
					analysePom(childSubStream, result, simpleFormat);
	        	   }
	           }
	       }
	   }catch (Exception e) {
		   e.printStackTrace();
	   }
   }

   private boolean analyseFile(File file)
   {
      if (file == null || !file.exists())
      {
         return false;
      }
      JarClassLoader jarClassLoader = null;
      JarFile jarFile = null;
      ZipInputStream zip = null;
      Set<Thread> oldThreads = ReflectionLoader.closeThreads(null);
      Timer timer = null;
      ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
      tester.withTest(ReflectionBlackBoxTester.INSTANCE);
      try
      {
         jarClassLoader = new JarClassLoader(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
         clear();
         FileInputStream fis = new FileInputStream(file);
         BufferedInputStream bis = new BufferedInputStream(fis);
         zip = new ZipInputStream(bis);
         ZipEntry entry;

         while ((entry = zip.getNextEntry()) != null)
         {
            String name = entry.getName();
            String lName = name.toLowerCase();
            this.count++;
            if (lName.endsWith(".class"))
            {
               this.classes++;
               if (name.indexOf("$") < 0 && name.split("/").length > 2)
               {
                  int pos = name.lastIndexOf("/");
                  warningsPackages.add(name.substring(0, pos).replaceAll("/", "."));
               }
            }
            else
            {
               if (lName.endsWith(".jpg"))
               {
                  warnings.add(name);
               }
               else if (lName.endsWith(".png"))
               {
                  if (!lName.equals("de/uniks/networkparser/np.png"))
                  {
                     warnings.add(name);
                  }
               }
               else if (lName.endsWith(".obj"))
               {
                  warnings.add(name);
               }
               else if (lName.endsWith(".mtl"))
               {
                  warnings.add(name);
               }
               else if (lName.endsWith(".jar"))
               {
                  warnings.add(name);
               }
               else if (lName.endsWith(".java"))
               {
                  warnings.add(name);
               }
               else if (lName.endsWith(".html"))
               {
                  warnings.add(name);
               }
               continue;
            }
            if (!isInstance)
            {
               continue;
            }
            String entryName = name.substring(0, name.length() - ".class".length());
            entryName = entryName.replace("/", ".");
            if (instancePackage != null && !entryName.startsWith(instancePackage))
            {
               continue;
            }
            try
            {
               Class<?> wantedClass = jarClassLoader.loadClass(entryName);

               if (Modifier.isAbstract(wantedClass.getModifiers()))
               {
                  continue;
               }
               if (wantedClass.isEnum() || wantedClass.isInterface() || wantedClass.isPrimitive())
               {
                  continue;
               }
               if (!Modifier.isPublic(wantedClass.getModifiers()))
               {
                  continue;
               }
               if (Modifier.isStatic(wantedClass.getModifiers()))
               {
                  continue;
               }

               /* Find Constructor */
               if(isAnalyse) {
	               if (timer == null) {
	                  timer = new Timer();
	               }
	               SimpleTimerTask task = new SimpleTimerTask(Thread.currentThread());
	               timer.schedule(task, 2000);
	
	               Object newInstance = ReflectionLoader.newInstanceSimple(wantedClass);
	               task.withSimpleExit(null);
	               if (newInstance == null)
	               {
	                  errors.add(name);
	               }
	               else
	               {
	                  tester.testClass(newInstance, wantedClass, null);
	               }
               }
            }
            catch (Throwable e)
            {
               errors.add(name + "-" + e.getMessage());
            }
         }
      }
      catch (Throwable e)
      {
         errors.add(file.getName() + "-" + e.getMessage());
         e.printStackTrace();
      }
      finally
      {
         try
         {
        	jarClassLoader.close();
            if (zip != null)
            {
               zip.close();
            }
            if (jarClassLoader != null)
            {
               jarClassLoader.close();
            }
            if (jarFile != null)
            {
               jarFile.close();
            }
         }
         catch (Exception e)
         {}
         
      }
      if (timer != null)
      {
         timer.cancel();
         timer = null;
      }

      ReflectionLoader.closeThreads(oldThreads);
      return false;
   }

   private boolean isProject(String item)
   {
      for (int i = 0; i < projects.size(); i++)
      {
         if (item.startsWith(projects.getKeyByIndex(i)))
         {
            /* SUB PACKAGE OF FOUND PROJECT */
            return false;
         }
      }
      return true;
   }

   private JsonObject getHTTPJson(String value)
   {
      HTMLEntity http = NodeProxyTCP
         .getHTTP("http://search.maven.org/solrsearch/select?rows=20&wt=json&q=fc:" + value);
      XMLEntity body = http.getBody();
      if (body != null && body.getValue() != null)
      {
         return JsonObject.create(body.getValue());
      }
      return null;
   }

   /**
    * Merge packages.
    *
    * @return the simple key value list
    */
   public SimpleKeyValueList<String, JsonObject> mergePackages()
   {
      projects.clear();
      TreeSet<String> cache = new TreeSet<String>();
      cache.add("de.uniks");
      cache.add("de");
      for (String item : this.warningsPackages)
      {
         if (isProject(item))
         {
            JsonObject json = getHTTPJson(item);
            if (json != null)
            {
               JsonObject responseJson = json.getJsonObject("response");
               if (responseJson.getInt("numFound") > 0)
               {
                  /* FOUND IT */
                  projects.add(item, responseJson);
               }
               else
               {
                  /* Search for SubPackage??? */
                  String[] split = item.split("\\.");
                  for (int i = split.length - 1; i > 0; i--)
                  {
                     String search = split[0];
                     for (int z = 1; z < i; z++)
                     {
                        search += "." + split[z];
                     }
                     if (cache.contains(search))
                     {
                        continue;
                     }
                     json = getHTTPJson(search);
                     if (json != null)
                     {
                        responseJson = json.getJsonObject("response");
                        if (responseJson.getInt("numFound") > 0)
                        {
                           /* FOUND IT */
                           projects.add(search, responseJson);
                           break;
                        }
                        else
                        {
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

   /**
    * Gets the min coverage.
    *
    * @return the min coverage
    */
   public int getMinCoverage()
   {
      return this.minCoverage;
   }

   /**
    * With root path.
    *
    * @param param the param
    * @return the jar validator
    */
   public JarValidator withRootPath(String param)
   {
      if (param != null)
      {
         if (param.endsWith("\\") || param.endsWith("/"))
         {
            this.rootPath = param;
            return this;
         }
         this.rootPath = param + "\\";
      }
      return this;
   }
}
