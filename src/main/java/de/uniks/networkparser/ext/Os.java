package de.uniks.networkparser.ext;

/*
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
import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.list.SimpleList;

public class Os {
	public static final String WINDOWS = "windows";
	public static final String MAC = "mac";
	public static final String UNIX = "unix";
	public static final String ANDROID = "android";
	public static final String UNKNOWN = "unknown";

	public static final boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}

	public static final boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}

	public static final boolean isIOS() {
		String os = System.getProperty("os.name").toLowerCase();
		// IOS
		return (os.indexOf("ios") >= 0);
	}

	public static final boolean isReflectionTest() {
		return System.getProperty("Tester") != null;
	}

	public static final boolean isJavaFX() {
		if (ReflectionLoader.PLATFORM == null || ReflectionLoader.PANE == null) {
			return false;
		}
		try {
			ReflectionLoader.newInstance(ReflectionLoader.PANE);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	public static final boolean isGenerator() {
		SimpleList<String> allowUser = new SimpleList<String>().with("Stefan");
		return isEclipse() && allowUser.contains(System.getProperty("user.name")) && isJUnitTest();
	}

	public static final boolean isJUnitTest() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		List<StackTraceElement> list = Arrays.asList(stackTrace);
		for (StackTraceElement element : list) {
			if (element.getClassName().startsWith("org.junit.")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAndroid() {
		String javafxPlatform = System.getProperty("javafx.platform").toLowerCase();
		String vmName = System.getProperty("java.vm.name").toLowerCase();
		return ("android".equals(javafxPlatform) || "dalvik".equals(vmName));
	}

	public static boolean isEclipseAndNoReflection() {
		if (isReflectionTest()) {
			return false;
		}
		return isEclipse();
	}

	public static final boolean isEclipse() {
		String fileName = Os.getFilename().toLowerCase();
		if (!fileName.endsWith(".jar")) {
			// Eclipse
			return true;
		}
		return false;
	}

	public static final boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public static final boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}

	public static final String getCurrentPlatform() {
		if (Os.isWindows())
			return WINDOWS;
		if (Os.isMac())
			return MAC;
		if (Os.isUnix())
			return UNIX;
		if (Os.isAndroid())
			return ANDROID;
		return UNKNOWN;
	}

	public static final String getFilename() {
		File jar = new File(Os.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		return jar.getAbsoluteFile().getName();
	}

	public static final boolean isUTF8() {
		return ("UTF-8".equals(System.getProperty("file.encoding"))
				|| "UTF8".equals(System.getProperty("file.encoding")));
	}

	public static final boolean isNotFirstThread(String[] args) {
		for (String item : args) {
			if ("-XstartOnFirstThread".equalsIgnoreCase(item)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean checkSystemTray() {
		Object value = ReflectionLoader.call(ReflectionLoader.SYSTEMTRAY, "isSupported");
		if (value != null) {
			return (Boolean) value;
		}
		return false;
	}
	
	public static final boolean isFXThread() {
		Object result = ReflectionLoader.call(ReflectionLoader.PLATFORM, "isFxApplicationThread");
		if (Boolean.TRUE.equals(result) ) {
			return true;
		}
		return false;
	}
}
