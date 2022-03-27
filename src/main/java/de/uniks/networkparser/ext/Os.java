package de.uniks.networkparser.ext;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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

/**
 * AbstractClass for Os Detection.
 *
 * @author Stefan Lindel
 */
public class Os {
	
	/** The Constant WINDOWS. */
	public static final String WINDOWS = "windows";
	
	/** The Constant MAC. */
	public static final String MAC = "mac";
	
	/** The Constant UNIX. */
	public static final String UNIX = "unix";
	
	/** The Constant ANDROID. */
	public static final String ANDROID = "android";
	
	/** The Constant UNKNOWN. */
	public static final String UNKNOWN = "unknown";

	/**
	 * Checks if is windows.
	 *
	 * @return true, if is windows
	 */
	public static final boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		/* windows */
		return (os.indexOf("win") >= 0);
	}

	/**
	 * Checks if is mac.
	 *
	 * @return true, if is mac
	 */
	public static final boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		/* Mac */
		return (os.indexOf("mac") >= 0);
	}

	/**
	 * Checks if is ios.
	 *
	 * @return true, if is ios
	 */
	public static final boolean isIOS() {
		String os = System.getProperty("os.name").toLowerCase();
		/* IOS */
		return (os.indexOf("ios") >= 0);
	}

	/**
	 * Checks if is reflection test.
	 *
	 * @return true, if is reflection test
	 */
	public static final boolean isReflectionTest() {
		return System.getProperty("Tester") != null;
	}

	/**
	 * Gets the tester.
	 *
	 * @return the tester
	 */
	public static String getTester() {
		return System.getProperty("Tester");
	}

	/**
	 * Checks if is java FX.
	 *
	 * @return true, if is java FX
	 */
	public static final boolean isJavaFX() {
		if (ReflectionLoader.PLATFORM == null || ReflectionLoader.PANE == null) {
			return false;
		}
		if (isReflectionTest()) {
			return false;
		}
		try {
			Object item = ReflectionLoader.newInstance(ReflectionLoader.PANE);
			if (item == null) {
				return false;
			}
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if is generator.
	 *
	 * @return true, if is generator
	 */
	public static final boolean isGenerator() {
		SimpleList<String> allowUser = new SimpleList<String>().with("Stefan");
		return isEclipse() && allowUser.contains(System.getProperty("user.name")) && isJUnitTest();
	}

	/**
	 * Checks if is j unit test.
	 *
	 * @return true, if is j unit test
	 */
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

	/**
	 * Checks if is android.
	 *
	 * @return true, if is android
	 */
	public static boolean isAndroid() {
		String property = System.getProperty("javafx.platform");
		if (property == null) {
			return false;
		}
		String javafxPlatform = System.getProperty("javafx.platform").toLowerCase();
		String vmName = System.getProperty("java.vm.name").toLowerCase();
		return ("android".equals(javafxPlatform) || "dalvik".equals(vmName));
	}

	/**
	 * Checks if is eclipse and no reflection.
	 *
	 * @return true, if is eclipse and no reflection
	 */
	public static boolean isEclipseAndNoReflection() {
		if (isReflectionTest()) {
			return false;
		}
		return isEclipse();
	}

	/**
	 * Checks if is eclipse.
	 *
	 * @return true, if is eclipse
	 */
	public static final boolean isEclipse() {
		String fileName = Os.getFilename().toLowerCase();
		if (!fileName.endsWith(".jar")) {
			/* Eclipse */
			return true;
		}
		return false;
	}

	/**
	 * Checks if is unix.
	 *
	 * @return true, if is unix
	 */
	public static final boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		/* linux or unix */
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}

	/**
	 * Checks if is solaris.
	 *
	 * @return true, if is solaris
	 */
	public static final boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		/* Solaris */
		return (os.indexOf("sunos") >= 0);
	}

	/**
	 * Gets the current platform.
	 *
	 * @return the current platform
	 */
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

	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public static final String getFilename() {
		File jar = new File(Os.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		return jar.getAbsoluteFile().getName();
	}

	/**
	 * Checks if is utf8.
	 *
	 * @return true, if is utf8
	 */
	public static final boolean isUTF8() {
		return ("UTF-8".equals(System.getProperty("file.encoding"))
				|| "UTF8".equals(System.getProperty("file.encoding")));
	}
	
	/**
	 * Checks if is not first thread.
	 *
	 * @param args the args
	 * @return true, if is not first thread
	 */
	public static final boolean isNotFirstThread(String[] args) {
		if (args != null) {
			for (String item : args) {
				if ("-XstartOnFirstThread".equalsIgnoreCase(item)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check system tray.
	 *
	 * @return true, if successful
	 */
	public static final boolean checkSystemTray() {
		if (isReflectionTest()) {
			return false;
		}
		Object value = ReflectionLoader.call(ReflectionLoader.SYSTEMTRAY, "isSupported");
		if (value != null) {
			return (Boolean) value;
		}
		return false;
	}

	/**
	 * Checks if is FX thread.
	 *
	 * @return true, if is FX thread
	 */
	public static final boolean isFXThread() {
		if (isReflectionTest()) {
			return false;
		}
		Object result = ReflectionLoader.call(ReflectionLoader.PLATFORM, "isFxApplicationThread");
		if (Boolean.TRUE.equals(result)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is headless.
	 *
	 * @return true, if is headless
	 */
	public static final boolean isHeadless()
	{
		Object result = ReflectionLoader.call(ReflectionLoader.getClass("java.awt.GraphicsEnvironment"), "isHeadless");
		if (Boolean.FALSE.equals(result)) {
			return false;
		}
		return true;
	}

}
