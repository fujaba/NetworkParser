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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class ErrorHandler implements Thread.UncaughtExceptionHandler {
	public static final String TYPE = "ERROR";
	private String path;
	private Object stage;
	private DateTimeEntity startDate = new DateTimeEntity();
	private SimpleList<ObjectCondition> list = new SimpleList<ObjectCondition>();

	public String getIP() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getMac() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			return sb.toString();
		} catch (SocketException e) {
		} catch (UnknownHostException e) {
		}
		return null;
	}

	public boolean saveErrorFile(String prefix, String fileName, String filePath, Throwable e) {
		boolean success;
		try {
			File file = getFileName(filePath, prefix, fileName);
			if (file == null) {
				return false;
			}
			FileOutputStream networkFile = new FileOutputStream(file);

			PrintStream ps = new PrintStream(networkFile);
			ps.println("Error: " + e.getMessage());
			ps.println("Start: " + getJVMStartUp().toString("dd.mm.yyyy HH:MM:SS"));
			ps.println("Startdate: " + startDate.toString("dd.mm.yyyy HH:MM:SS"));
			ps.println("Date: " + new DateTimeEntity().toString("dd.mm.yyyy HH:MM:SS"));
			ps.println("Thread: " + Thread.currentThread().getName());
			ps.println("PID: " + getPID());
			ps.println("Version: " + SimpleController.getVersion());
			ps.println("IP: " + getIP());
			ps.println("MAC: " + getMac());

			ps.println("------------ SYSTEM-INFO ------------");
			printProperty(ps, "java.class.version");
			printProperty(ps, "java.runtime.version");
			printProperty(ps, "java.specification.version");
			printProperty(ps, "java.version");
			printProperty(ps, "java.home");
			printProperty(ps, "os.arch");
			printProperty(ps, "os.name");
			printProperty(ps, "os.version");
			printProperty(ps, "user.dir");
			printProperty(ps, "user.home");
			printProperty(ps, "user.language");
			printProperty(ps, "user.name");
			printProperty(ps, "user.timezone");
			ps.println("");

			Runtime r = Runtime.getRuntime();
			ps.println("Prozessoren :       " + r.availableProcessors());
			ps.println("Freier Speicher JVM:    " + r.freeMemory());
			ps.println("Maximaler Speicher JVM: " + r.maxMemory());
			ps.println("Gesamter Speicher JVM:  " + r.totalMemory());

			ps.println();
			/* SubErrors */
			printSubTrace(ps, "", 1, e);

			ps.close();
			if ("Java heap space".equals(e.getMessage())) {
				saveHeapSpace(prefix);
			}
			success = true;
		} catch (FileNotFoundException exception) {
			success = false;
		} catch (IOException exception) {
			success = false;
		}
		return success;
	}

	public boolean saveHeapSpace(String prefix) {
		String filepath = createDir(this.path);
		if (filepath == null) {
			return false;
		}
		if (filepath.length() > 0 && filepath.endsWith("/") == false) {
			filepath += "/";
		}
		String fullfilename = filepath + prefix + "heap.bin";
		SimpleList<String> commandList = new SimpleList<String>();
		commandList.with("jmap", "-dump:live,format=b,file=" + fullfilename, "" + getPID());
		String[] list = commandList.toArray(new String[commandList.size()]);
		ProcessBuilder processBuilder = new ProcessBuilder(list);
		Process process;
		try {
			process = processBuilder.start();
			process.waitFor();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public DateTimeEntity getJVMStartUp() {
		DateTimeEntity item = new DateTimeEntity();
		if (ReflectionLoader.MANAGEMENTFACTORY == null) {
			return item;
		}
		Object runTime = ReflectionLoader.call(ReflectionLoader.MANAGEMENTFACTORY, "getRuntimeMXBean");
		if (runTime != null) {
			Object returnValue = ReflectionLoader.getField(runTime, "vmStartupTime");
			if (returnValue instanceof Long) {
				item.withTime((Long) returnValue);
			}
		}
		return item;

	}

	public int getPID() {
		int pid = -1;
		if (ReflectionLoader.MANAGEMENTFACTORY == null) {
			return pid;
		}
		Object runTime = ReflectionLoader.call(ReflectionLoader.MANAGEMENTFACTORY, "getRuntimeMXBean");
		if (runTime == null) {
			return pid;
		}
		Object jvm = ReflectionLoader.getField(runTime, "jvm");
		if (jvm != null) {
			Object returnValue = ReflectionLoader.call(jvm, "getProcessId");
			if (returnValue instanceof Integer) {
				pid = (Integer) returnValue;
			}
		}
		return pid;
	}

	public static void printProperty(PrintStream ps, String property) {
		ps.println(property + ": " + System.getProperty(property));
	}

	public static void printSubTrace(PrintStream ps, String prefix, int index, Throwable e) {
		if (prefix == null) {
			return;
		}
		if (prefix.length() > 0) {
			prefix += ":" + index;
			ps.println(prefix);
		} else {
			prefix = "Sub";
		}
		e.printStackTrace(ps);

		Throwable[] suppressed = e.getSuppressed();
		if (suppressed != null) {
			for (int number = 0; number < suppressed.length; number++) {
				printSubTrace(ps, prefix, number, suppressed[number]);
			}
		}
	}

	public static String createDir(String path) {
		if (path == null) {
			return "";
		}
		File dirPath = new File(path);
		dirPath = new File(dirPath.getPath());
		if (!dirPath.exists()) {
			if (dirPath.mkdirs()) {
				return path;
			}
		} else {
			return path;
		}
		return null;
	}

	public ErrorHandler withPath(String value) {
		this.path = value;
		return this;
	}

	public File getFileName(String filepath, String prefix, String fileName) throws IOException {
		if (filepath == null) {
			return null;
		}
		if (fileName == null) {
			return null;
		}
		filepath = createDir(filepath);
		if (filepath == null) {
			return null;
		}
		if (filepath.length() > 0 && filepath.endsWith("/") == false) {
			filepath += "/";
		}
		String fullfilename = null;
		if (prefix != null) {
			fullfilename = filepath + prefix + fileName;
		} else {
			fullfilename = filepath + fileName;
		}

		File file = new File(fullfilename);
		if (file.exists() == false) {
			if (file.createNewFile() == false) {
				return null;
			}
		}
		return file;
	}

	public Exception saveScreenShoot(String prefix, String fileName, String filePath, Object currentStage) {
		/* Save Screenshot */
		if (currentStage == null) {
			currentStage = stage;
		}
		try {
			File target = getFileName(filePath, prefix, fileName);
			if (target == null) {
				return null;
			}
			Object rect = ReflectionLoader.newInstance(ReflectionLoader.RECTANGLE, ReflectionLoader.DIMENSION,
					ReflectionLoader.callChain(ReflectionLoader.TOOLKIT, "getDefaultToolkit", "getScreenSize"));
			writeScreen(target, rect);
			if (currentStage != null) {
				Double x = (Double) ReflectionLoader.call(currentStage, "getX");
				Double y = (Double) ReflectionLoader.call(currentStage, "getY");
				Double width = (Double) ReflectionLoader.call(currentStage, "getWidth");
				Double height = (Double) ReflectionLoader.call(currentStage, "getHeight");

				String windowName = currentStage.getClass().getSimpleName();
				target = getFileName(filePath, prefix + windowName, fileName);
				rect = ReflectionLoader.newInstance(ReflectionLoader.RECTANGLE, int.class, x.intValue(), int.class,
						y.intValue(), int.class, width.intValue(), int.class, height.intValue());
				writeScreen(target, rect);
			}
		} catch (Exception e1) {
			return e1;
		}
		return null;
	}

	private boolean writeScreen(File file, Object rectangle) {
		Object robot = ReflectionLoader.newInstance(ReflectionLoader.ROBOT);
		Object bi = ReflectionLoader.call(robot, "createScreenCapture", ReflectionLoader.RECTANGLE, rectangle);

		Boolean result = (Boolean) ReflectionLoader.call(ReflectionLoader.IMAGEIO, "write",
				ReflectionLoader.RENDEREDIMAGE, bi, String.class, "jpg", File.class, file);
		return result;
	}

	public void saveException(Throwable e) {
		saveException(e, this.stage, true);
	}

	public void saveException(Throwable e, boolean throwException) {
		saveException(e, this.stage, throwException);
	}

	public boolean writeOutput(String output, boolean error) {
		String fullFileName = "";
		if (this.path != null) {
			fullFileName = this.path;
			if (fullFileName.length() > 0 && fullFileName.endsWith("/") == false) {
				fullFileName += "/";
			}
		}
		File file;
		createDir(this.path);
		if (error) {
			file = new File(fullFileName + "error.txt");
		} else {
			file = new File(fullFileName + "output.txt");
		}
		try {
			if (file.exists() == false) {
				if (file.createNewFile() == false) {
					return false;
				}
			}
			FileOutputStream stream = new FileOutputStream(file, true);
			stream.write(output.getBytes());
			stream.close();
		} catch (IOException e) {

		}
		return true;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		saveException(e, stage, true);
	}

	public String getPrefix() {
		return new DateTimeEntity().toString("yyyymmdd_HHMMSS_");
	}

	public boolean saveException(Throwable e, Object stage, boolean throwException) {
		/* Generate Error.txt */
		if (e == null) {
			return false;
		}
		String prefixName = getPrefix();
		boolean success = saveErrorFile(prefixName, "error.txt", this.path, e);
		saveScreenShoot(prefixName, "Full.jpg", this.path, stage);
		if (list.size() > 0) {
			SimpleEvent event = new SimpleEvent(this, prefixName, null, e);
			event.withType(TYPE);

			for (ObjectCondition child : list) {
				child.update(event);
			}
		}
		if (Os.isEclipse()) {
			e.printStackTrace();
			throw new SimpleException(e);
		}
		return success;
	}

	public Object getStage() {
		return stage;
	}

	public ErrorHandler withStage(Object value) {
		this.stage = value;
		return this;
	}

	public void addListener(ObjectCondition world) {
		list.add(world);
	}

	public String getPath() {
		return path;
	}
}
