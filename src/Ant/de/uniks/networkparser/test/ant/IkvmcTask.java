package de.uniks.networkparser.test.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;

import de.uniks.networkparser.test.ant.ikvm.OutputFilter;

/**
 * Ant task wrapper for IKVMC compiler.
 *
 * All IKVMC parameters correspond to task attributes list of classes and jar-files is specified as nested fileset elements.
 *
 *
 * See also <a href='http://www.ikvm.net/userguide/ikvmc.html'>IKVMC manual<a>

 * @author Andy Malakov
 */
public class IkvmcTask extends Task {

	public static enum Target {
		exe, winexe, library, module;
	};

	private static final String IKVMC_PROC_NAME = "ikvmc.exe";

	private File ikvmcFile = new File (IKVMC_PROC_NAME);
	private String recurse;
	private List<FileSet> filesets;
	private Target target = Target.library;
	private String out;
	private String classloader;
	private String assembly;
	private String keyfile;
	private String main;
	private String version;
	private boolean debug;
	private boolean verbose;
	private boolean noglobbing;
	private boolean nostacktraceinfo;
	private boolean noJNI;
	private File srcPathFile;
	private File excludesFile;
	private File remapFile;
	private List<Reference> moduleRefs = new ArrayList<Reference>();
	private List<Resource> resourceRefs = new ArrayList<Resource>();
	private List<Arg> extraArguments = new ArrayList<Arg>();
	private OutputFilter outputFilter;

	public IkvmcTask () {
	}

	public void addFileSet(FileSet fileset) {
		if (filesets == null)
			filesets = new ArrayList<FileSet>();

		filesets.add (fileset);
	}

	public void setTarget (Target target) {
		this.target = target;
	}

	public void setOut (String out) {
		this.out = out;
	}

	public void setAssembly (String assembly) {
		this.assembly = assembly;
	}

	public void setKeyfile (String keyfile) {
		this.keyfile = keyfile;
	}

	public void setVerbose (boolean verbose) {
		this.verbose = verbose;
	}

	public void setMain (String main) {
		this.main = main;
	}

	public void setVersion (String version) {
		this.version = version;
	}

	public void setClassloader (String classloader) {
		this.classloader = classloader;
	}

	public void setDebug (boolean debug) {
		this.debug = debug;
	}

	public void setNoglobbing (boolean noglobbing) {
		this.noglobbing = noglobbing;
	}

	public void setNostacktraceinfo (boolean nostacktraceinfo) {
		this.nostacktraceinfo = nostacktraceinfo;
	}

	public void setNojni (boolean nojni) {
		this.noJNI = nojni;
	}

	public void setSrcpath (File srcPath) {
		this.srcPathFile = srcPath;
	}

	public void setRemap (File remapFile) {
		if (remapFile != null && remapFile.getName().length() > 0 && remapFile.isFile()) {
			this.remapFile = remapFile;
		} else {
			if (verbose)
				System.out.println("Remap parameter is not a file: " + remapFile + " - ignoring");
		}
	}


	public void setRecurse (String recurse) {
		this.recurse = recurse;
	}

	public void setExclude (File exclude) {
		this.excludesFile = exclude;
	}

	public Reference createReference() {
		Reference reference = new Reference();
		moduleRefs.add(reference);
		return reference;
	}

	public Resource createResource() {
		Resource reference = new Resource();
		resourceRefs.add(reference);
		return reference;
	}

	public Arg createArg() {
		Arg arg = new Arg();
		extraArguments.add(arg);
		return arg;
	}

	public void addOutputFilter (OutputFilter filter) {
		outputFilter = filter;
	}

	public void setHome (File home) {

		ikvmcFile = new File (new File (home, "bin"), IKVMC_PROC_NAME);

		if ( !ikvmcFile.exists())
			throw new IllegalArgumentException ("Path specified by 'home' attribute does not contain " + IKVMC_PROC_NAME);
	}

	public void setIKVMC (File ikvmc) {
		ikvmcFile = ikvmc;
		if ( !ikvmcFile.exists())
			throw new IllegalArgumentException ("Can't find executable specified by 'ikvmc' attribute: \"" + ikvmc + "\"");
	}
	
	private List<String> buildArguments() throws BuildException {
		List<String> result = new ArrayList<String>();

		String processName = (ikvmcFile == null) ? IKVMC_PROC_NAME : ikvmcFile.getAbsolutePath();
		result.add(processName);



		if (out != null) {
			result.add ("-out:" + out);
		}

		if (assembly != null) {
			result.add ("-assembly:" + assembly);
		}

		if (target != null) {
			result.add ("-target:"+target.name());
		}

		if (keyfile != null) {
			result.add ("-keyfile:"+keyfile);
		}

		if (version != null) {
			result.add ("-version:"+version);
		}

		if (main != null) {
			result.add ("-main:"+main);
		}

		for (Reference ref : moduleRefs) {
			if (ref.path != null && ref.path.length () > 0) {
				for (String refPath : ref.path.split (",")) {
					String	  fullPath = refPath;

					if (ref.base != null)
						fullPath = ref.base + "/" + fullPath;

					result.add ("-reference:" + fullPath);
				}
			}
		}

		if (recurse != null) {
			result.add ("-recurse:"+recurse);
		}

		if (noJNI) {
			result.add ("-nojni");
		}

		if (noglobbing) {
			result.add ("-noglobbing");
		}

		if (nostacktraceinfo)
			result.add ("-nostacktraceinfo");

		if (classloader != null) {
			result.add ("-classloader:"+classloader);
		}

		List<String> classesAndJars = collectClassesJarsAndResources(); // also appends to resourceRefs
		for (Resource ref : resourceRefs) {
			if (verbose)
				System.out.println(ref.name);
			result.add ("-resource:" + ref.name + '=' + ref.path);
		}

		if (excludesFile != null) {
			result.add ("-exclude:" + excludesFile);
		}

		if (debug) {
			result.add ("-debug");
		}

		if (remapFile != null && remapFile.length () > 0)
			result.add ("-remap:" +remapFile);

		if (srcPathFile != null) {
			result.add ("-srcpath:" + srcPathFile.getAbsolutePath());
		}

		for (Arg arg : extraArguments) {
			result.add (arg.value);
		}

		//TODO: Xtrace
		//TODO: Xmethodtrace


		result.addAll(classesAndJars);

		return result;
	}



	/** Also modifies resourceReferences collection */
	@SuppressWarnings("unchecked")
	private List<String> collectClassesJarsAndResources() {
		Resources filesToProcess = new Resources();
		List<String> result = new ArrayList<String> (filesToProcess.size());
		filesToProcess.setProject(getProject());
		if (filesets == null)
			throw new BuildException("Specify files to process using nested <fileset> element");

		for (FileSet fileset : filesets) {
			filesToProcess.add(fileset);
		}

		Iterator<org.apache.tools.ant.types.Resource> iter = (Iterator<org.apache.tools.ant.types.Resource>) filesToProcess.iterator();
		while (iter.hasNext()) {
			appendClassOrJarOrResource(result, (FileResource) iter.next());
		}
		return result;
	}

	private void appendClassOrJarOrResource(List<String> result, FileResource r) {
		String relativeName = r.getName();
		String fullFileName = r.getFile().getAbsolutePath();

		if (!r.isExists())
			throw new BuildException ("Missing input file: " + fullFileName);


		boolean classOrJar = relativeName.endsWith(".class") || relativeName.endsWith(".jar") || relativeName.endsWith(".zip");
		if (classOrJar) {
			if (verbose)
				System.out.println("\t+file: " + relativeName);

			result.add(fullFileName);
		} else {
			if (verbose)
				System.out.println("\t+resource: " + relativeName);
			String resourceName = replaceNTPathChar(relativeName);
			createResource().set (resourceName, fullFileName);

			result.add ("-resource:" + resourceName + '=' + fullFileName);
		}
	}

	private static class ProcessShutdown extends Thread {
		private final Process process;

		ProcessShutdown (Process p) {
			process = p;
		}

		public void run() {
			process.destroy();
		}
	}


	@Override
	public void execute () throws BuildException {
		List<String> arguments = buildArguments ();
		if (verbose)
			printArguments(arguments);


		try {
			ProcessBuilder		  pb = new ProcessBuilder (arguments);

			Process				 process = pb.start ();

			new StreamPump (process.getInputStream (), System.out, outputFilter).start ();
			new StreamPump (process.getErrorStream (), System.err, outputFilter).start ();

			ProcessShutdown shutdownHook = new ProcessShutdown (process);
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			process.waitFor ();
			Runtime.getRuntime().removeShutdownHook(shutdownHook);

			if (process.exitValue () != 0) {
				throw new BuildException (IKVMC_PROC_NAME + " returned non-0 error code");
			}
		} catch (Exception e) {
			throw new BuildException ("Error running " + IKVMC_PROC_NAME + ":" + e.getMessage(), e);
		}
	}

	private static void printArguments(List<String> arguments) {
		System.out.print("IKVMC Arguments:");
		for (String s : arguments) {
			System.out.print (" ");
			System.out.print (s);
		}
		System.out.println ();
	}

	private static class StreamPump extends Thread {
		private final InputStream is;
		private final PrintStream out;
		private final OutputFilter outputFilter;

		StreamPump(InputStream is, PrintStream out, OutputFilter outputFilter) {
			this.is = is;
			this.out = out;
			this.outputFilter = outputFilter;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					if (outputFilter != null && outputFilter.suppress(line))
						continue;
					out.println(line);
				}

			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/** IKVMC will convert '/' characters into .NET resource separator '!' */
	private static String replaceNTPathChar(String relativeName) {
		return relativeName.replace('\\', '/');
	}

	public static class Reference {
		String path;
		String base = null;
		public void setPath(String path) { this.path = path; }
		public String getPath() { return path; }
		public void setBase(String base) { this.base = base; }
		public String getBase() { return base; }
	}

	public static class Resource {
		String name;
		String path;

		public void set (String name, String path) {
			setName(name);
			setPath (path);
		}

		public void setName(String name) { this.name = name; }
		public String getName() { return name; }

		public void setPath(String path) { this.path = path; }
		public String getPath() { return path; }
	}

	public static class Arg {
		String value;
		public void setValue(String name) { this.value = name; }
		public String getValue() { return value; }
	}

}
