package de.uniks.networkparser.ext.error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class ErrorHandler implements Thread.UncaughtExceptionHandler{
	private String path;
	private Object stage;

	public boolean writeErrorFile(String fileName, String filepath, Throwable e){
		boolean success;
		try {
			filepath=createDir(filepath);
			if(filepath == null) {
				return false;
			}
			if(!filepath.endsWith("/")){
				filepath+="/";
			}
			String fullfilename=filepath+fileName;

			File file=new File(fullfilename);
			if(file.exists() == false){
				if(file.createNewFile() == false) {
					return false;
				}
			}
			FileOutputStream networkFile = new FileOutputStream(filepath+"/"+fileName);
			
			PrintStream ps = new PrintStream( networkFile );
			ps.println("Error: "+e.getMessage());
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
	
	public ErrorHandler withPath(String value) {
		this.path = value;
		return this;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		GregorianCalendar temp=new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String prefixName=formatter.format(temp.getTime())+"_";
		writeErrorFile(prefixName+"error.txt", this.path, e);
		saveScreenShoot(prefixName+"Full.jpg", null);
	}
	
	public Exception saveScreenShoot(String fullScreenFileName, Object currentStage) {
		// Save Screenshot
		if(currentStage == null) {
			currentStage = stage;
		}
		try {
			
			if (fullScreenFileName != null) {
				Object rect = ReflectionLoader.newInstance(ReflectionLoader.RECTANGLE, ReflectionLoader.DIMENSION, ReflectionLoader.callChain(ReflectionLoader.TOOLKIT, "getDefaultToolkit", "getScreenSize"));
				writeScreen(fullScreenFileName, rect);
			}
			if (currentStage != null) {
				Double x = (Double) ReflectionLoader.call("getX", currentStage);
				Double y= (Double) ReflectionLoader.call("getY", currentStage);
				Double width = (Double) ReflectionLoader.call("getWidth", currentStage);
				Double height = (Double) ReflectionLoader.call("getHeight", currentStage);
				
				String windowName = currentStage.getClass().getSimpleName();
				
				Object rect = ReflectionLoader.newInstance(ReflectionLoader.RECTANGLE, int.class, x.intValue(), int.class, y.intValue(), int.class, width.intValue(), int.class, height.intValue());
				writeScreen(windowName, rect);
			}
		} catch (Exception e1) {
			return e1;
		}
		return null;
	}
	
	private boolean writeScreen(String fileName, Object rectangle) {
		Object robot = ReflectionLoader.newInstance(ReflectionLoader.ROBOT);
		Object bi = ReflectionLoader.call("createScreenCapture", robot, ReflectionLoader.RECTANGLE, rectangle);
		
		Boolean result = (Boolean) ReflectionLoader.call("write", ReflectionLoader.IMAGEIO, ReflectionLoader.RENDEREDIMAGE, bi, String.class, "jpg", File.class, new File(fileName));
		return result;
	}
	public void saveException(Throwable e) {
		saveException(e, this.stage);
	}
	public void saveException(Throwable e, Object stage) {
		// Generate Error.txt
		GregorianCalendar temp = new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String prefixName = formatter.format(temp.getTime()) + "_";
		writeErrorFile(prefixName+"error.txt", this.path, e);
		saveScreenShoot(prefixName+"Full.jpg", stage);
	}

	public Object getStage() {
		return stage;
	}

	public ErrorHandler withStage(Object value) {
		this.stage = value;
		return this;
	}
}
