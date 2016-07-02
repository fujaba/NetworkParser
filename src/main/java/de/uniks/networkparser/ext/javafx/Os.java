package de.uniks.networkparser.ext.javafx;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

import java.awt.SystemTray;
import java.io.File;

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
		return SystemTray.isSupported();
	}
}
