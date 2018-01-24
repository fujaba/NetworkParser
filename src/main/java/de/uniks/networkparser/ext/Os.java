package de.uniks.networkparser.ext;

import java.io.File;

import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class Os {
	public static final String WINDOWS="windows";
	public static final String MAC="mac";
	public static final String UNIX="unix";
	public static final String ANDROID="android";
	public static final String UNKNOWN="unknown";

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}

	public static boolean isIOS() {
		String os = System.getProperty("os.name").toLowerCase();
		// IOS
		return (os.indexOf("ios") >= 0);
	}
	
	public static boolean isReflectionTest() {
		return System.getProperty("Tester") != null;
	}

	public static boolean isAndroid() {
		String javafxPlatform = System.getProperty("javafx.platform").toLowerCase();
		String vmName = System.getProperty("java.vm.name").toLowerCase();
		return ("android".equals(javafxPlatform) || "dalvik".equals(vmName));
	}

	public static boolean isEclipse(){
		String fileName=new Os().getFilename().toLowerCase();
		if(!fileName.endsWith(".jar")){
			// Eclipse
			return true;
		}
		return false;
	}

	public static boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public static boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}

	public static String getCurrentPlatform() {
		if ( Os.isWindows() ) return WINDOWS;
		if ( Os.isMac() )	 return MAC;
		if ( Os.isUnix() )	return UNIX;
		if ( Os.isAndroid() )	return ANDROID;
		return UNKNOWN;
	}

	public String getFilename() {
		File jar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation()
				.getPath());
		return jar.getAbsoluteFile().getName();
	}

	public static boolean isUTF8(){
		return ("UTF-8".equals(System.getProperty("file.encoding"))||"UTF8".equals(System.getProperty("file.encoding")));
	}

	public static boolean isNotFirstThread(String[] args) {
		for(String item : args){
			if("-XstartOnFirstThread".equalsIgnoreCase(item)){
				return true;
			}
		}
		return false;
	}

	public static boolean checkSystemTray() {
		Object value = ReflectionLoader.call("isSupported", ReflectionLoader.SYSTEMTRAY);
		if(value != null) {
			return (Boolean)value;
		}
		return false;
	}
}
