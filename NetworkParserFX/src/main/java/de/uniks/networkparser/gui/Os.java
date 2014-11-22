package de.uniks.networkparser.gui;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.awt.SystemTray;
import java.io.File;
import com.sun.javafx.Utils;

public class Os {
    public enum PlatformType{ windows, mac, unix, unknown };
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
		return ("android".equals(javafxPlatform) ||  "dalvik".equals(vmName));
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
        if ( Utils.isWindows() ) return PlatformType.windows;
        if ( Utils.isMac() )     return PlatformType.mac;
        if ( Utils.isUnix() )    return PlatformType.unix;
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
