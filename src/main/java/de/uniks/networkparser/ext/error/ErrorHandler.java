package de.uniks.networkparser.ext.error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class ErrorHandler implements Thread.UncaughtExceptionHandler{
	private String path;

	public ErrorHandler(String path) {
		this.path = path;
	}
	public boolean writeErrorFile(String fileName, String filepath, Throwable e, Object extra){
		boolean success;
		try {
			filepath=createDir(filepath);
			if(!filepath.endsWith("/")){
				filepath+="/";
			}
			String fullfilename=filepath+fileName;

			File file=new File(fullfilename);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream networkFile = new FileOutputStream(filepath+"/"+fileName);
			
			PrintStream ps = new PrintStream( networkFile );
			ps.println("Error: "+e.getMessage());
			if(extra!=null){
				ps.println("Extra: "+extra.toString());
			}
			ps.println("Thread: "+Thread.currentThread().getName());
			ps.println("------------ SYSTEM-INFO ------------");
			printProperty(ps, "java.class.version");
			printProperty(ps, "java.runtime.version");
			printProperty(ps, "java.specification.version");
			printProperty(ps, "java.version");
			printProperty(ps, "os.arch");
			printProperty(ps, "os.name");
			printProperty(ps, "os.version");
			printProperty(ps, "user.dir");
			printProperty(ps, "user.home");
			printProperty(ps, "user.language");
			printProperty(ps, "user.name");
			printProperty(ps, "user.timezone");
			ps.println("");
			
			Runtime r=Runtime.getRuntime();
			ps.println("Prozessoren :       " + r.availableProcessors());
			ps.println("Freier Speicher JVM:    " + r.freeMemory());
			ps.println("Maximaler Speicher JVM: " + r.maxMemory());
			ps.println("Gesamter Speicher JVM:  " + r.totalMemory());
//			ps.println("Gesamter Speicher Java:  " + ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalSwapSpaceSize() );

			ps.println("***  ***");
			
			ps.println();
			e.printStackTrace(ps);
			ps.close();
			success=true;
		} catch (FileNotFoundException exception) {
			success=false;
		} catch (IOException exception) {
			success=false;
		}
		return success;
	}
	
	public static void printProperty(PrintStream ps, String property){
		ps.println(property+": "+System.getProperty(property));
	}
	
	public static String createDir(String path){
		File dirPath = new File(path);
		dirPath = new File(dirPath.getPath());
		if(!dirPath.exists()){
			if(dirPath.mkdirs()){
				return path;
			}
		}else{
			return path;
		}
		return null; 
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		GregorianCalendar temp=new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String prefixName=formatter.format(temp.getTime())+"_";
		writeErrorFile(prefixName+"error.txt", this.path, e, null);
	}
}
