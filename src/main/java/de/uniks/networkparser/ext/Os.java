package de.uniks.networkparser.ext;

import java.io.File;

import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class Os {
	public enum PlatformType{ windows, mac, unix, android, unknown };
	public boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}

	public boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}

	public boolean isIOS() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("ios") >= 0);
	}

	public boolean isAndroid() {
		String javafxPlatform = System.getProperty("javafx.platform").toLowerCase();
		String vmName = System.getProperty("java.vm.name").toLowerCase();
		return ("android".equals(javafxPlatform) || "dalvik".equals(vmName));
	}

	public boolean isEclipse(){
		String fileName=new Os().getFilename().toLowerCase();
		if(!fileName.endsWith(".jar")){
			// Eclipse
			return true;
		}
		return false;
	}

	public boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}

	public static PlatformType getCurrentPlatform() {
		Os os = new Os();
		if ( os.isWindows() ) return PlatformType.windows;
		if ( os.isMac() )	 return PlatformType.mac;
		if ( os.isUnix() )	return PlatformType.unix;
		if ( os.isAndroid() )	return PlatformType.android;
		return PlatformType.unknown;
	}

	public String getFilename() {
		File jar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation()
				.getPath());
		return jar.getAbsoluteFile().getName();
	}

	public boolean isUTF8(){
		return ("UTF-8".equals(System.getProperty("file.encoding"))||"UTF8".equals(System.getProperty("file.encoding")));
	}

	public boolean isNotFirstThread(String[] args) {
		for(String item : args){
			if("-XstartOnFirstThread".equalsIgnoreCase(item)){
				return true;
			}
		}
		return false;
	}

	public boolean checkSystemTray() {
		Object value = ReflectionLoader.call("isSupported", ReflectionLoader.SYSTEMTRAY);
		if(value != null) {
			return (Boolean)value;
		}
		return false;
	}
}
