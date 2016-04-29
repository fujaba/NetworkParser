package de.uniks.networkparser.ext.javafx.window;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import de.uniks.networkparser.ext.javafx.Os;
import de.uniks.networkparser.list.SimpleKeyValueList;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class SimpleShell extends Application {
	protected String icon;
	private String errorPath;
	protected FXStageController controller;

	protected abstract Parent createContents(FXStageController value, Parameters args);

	public void closeWindow() {
		this.controller.close();
	}

	public SimpleKeyValueList<String, String> getParameterMap() {
		SimpleKeyValueList<String, String> map = new SimpleKeyValueList<String, String>();
		List<String> raw = getParameters().getRaw();
		for (String item : raw) {
			if (item.startsWith("--")) {
				item = item.substring(2);
			}
			int pos = item.indexOf(":");
			int posEnter = item.indexOf("=");
			if (posEnter > 0 && (posEnter < pos || pos == -1)) {
				pos = posEnter;
			}
			if (pos > 0) {
				map.add(item.substring(0, pos), item.substring(pos + 1));
			} else {
				map.add(item, null);
			}
		}
		return map;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		String debugPort = null;
		String outputRedirect = null;
		if (getDefaultString() != null
				&& !getDefaultString().equalsIgnoreCase(System.getProperty("file.encoding"))) {
			System.setProperty("file.encoding", getDefaultString());
			Class<Charset> c = Charset.class;

			java.lang.reflect.Field defaultCharsetField = c.getDeclaredField("defaultCharset");
			defaultCharsetField.setAccessible(true);
			defaultCharsetField.set(null, null);
		}
		SimpleKeyValueList<String, String> params = getParameterMap();
		for (int i = 0; i < params.size(); i++) {
			String key = params.get(i);
			String value = params.getValueByIndex(i);
			if (key.equalsIgnoreCase("debug")) {
				if (value != null) {
					debugPort = value;
				} else {
					debugPort = "4223";
				}
			} else if (key.equalsIgnoreCase("output")) {
				if (value == null) {
					outputRedirect = "INHERIT";
				} else {
					outputRedirect = value;
				}
			} else if (key.equalsIgnoreCase("-?")) {
				System.out.println(getCommandHelp());
				System.exit(1);
				return;
			}
		}
		if (debugPort != null) {
			ArrayList<String> items = new ArrayList<String>();
			if (new Os().isMac()) {
				items.add(System.getProperty("java.home").replace("\\", "/") + "/bin/java");
			} else {
				items.add("\"" + System.getProperty("java.home").replace("\\", "/") + "/bin/java\"");
			}

			items.add("-Xdebug");
			items.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
			items.add("-jar");
			String fileName = new Os().getFilename().toLowerCase();
			items.add(fileName);

			ProcessBuilder processBuilder = new ProcessBuilder(items);
			if (outputRedirect != null) {
				if (outputRedirect.equalsIgnoreCase("inherit")) {
					processBuilder.redirectErrorStream(true);
					processBuilder.redirectOutput(Redirect.INHERIT);
				} else {
					int pos = outputRedirect.lastIndexOf(".");
					if (pos > 0) {
						processBuilder.redirectError(
								new File(outputRedirect.substring(0, pos) + "_error" + outputRedirect.substring(pos)));
						processBuilder.redirectOutput(
								new File(outputRedirect.substring(0, pos) + "_stdout" + outputRedirect.substring(pos)));
					} else {
						processBuilder.redirectError(new File(outputRedirect + "_error.txt"));
						processBuilder.redirectOutput(new File(outputRedirect + "_stdout.txt"));
					}
				}
			}
			processBuilder.start();
			// if(outputRedirect != null) {
			// int waitFor = process.waitFor();
			// System.out.println("Resultstatus: "+waitFor);
			// }
			System.exit(1);
		}
		// long mbMemory = ((com.sun.management.OperatingSystemMXBean)
		// ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize()/(1014*1024);
		// System.out.println("Total:"+
		// ((com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize());
		// params.put("-Xmx", "-Xmx"+mbMemory/4+"m");
		// System.out.println("Set MaxMemory: "+mbMemory/4+"m");
		try {
			this.controller = new FXStageController(primaryStage);
			Parent pane = createContents(this.controller, this.getParameters());
			this.controller.withCenter(pane);
			this.controller.show();
		} catch (Exception e) {
			this.saveException(e);
			if (new Os().isEclipse()) {
				throw e;
			}
		}
	}

	protected String getCommandHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("Help for the Commandline - ");
		sb.append(getCaption());
		sb.append("\n\n");

		sb.append("Debug\t\tDebug with <port> for debugging. Default is 4223\n");
		sb.append("Output\t\tOutput the debug output in standard-outputstream or file\n");

		return sb.toString();
	}

	protected String getDefaultString() {
		return "UTF-8";
	}

	public SimpleShell withIcon(String value) {
		this.controller.withIcon(value);
		this.icon = value;
		return this;
	}

	public SimpleShell withIcon(URL value) {
		withIcon(value.toString());
		return this;
	}

	public SimpleShell withTitle(String value) {
		this.controller.withTitle(value);
		return this;
	}

	public static boolean checkSystemTray() {
		return SystemTray.isSupported();
	}

	public void saveException(Throwable e, Object... extra) {
		// Generate Error.txt
		if (errorPath == null) {
			return;
		}
		GregorianCalendar temp = new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String prefixName = formatter.format(temp.getTime()) + "_";
		writeErrorFile(prefixName + "error.txt", e, extra);
		writeModel(prefixName);
		this.controller.saveScreenShoot(errorPath + prefixName + "Full.jpg", errorPath + prefixName + "App.jpg");
	}

	protected void writeModel(String prefixName) {

	}

	protected boolean writeErrorFile(String fileName, Throwable e, Object... extras) {
		boolean success;
		try {
			errorPath = createDir(errorPath);
			if(errorPath == null) {
				errorPath = "";
			}
			if (!errorPath.endsWith("/")) {
				errorPath += "/";
			}
			String fullfilename = errorPath + fileName;

			File file = new File(fullfilename);
			if (file.exists() == false) {
				if(file.createNewFile() == false) {
					return false;
				}
			}
			FileOutputStream networkFile = new FileOutputStream(errorPath + "/" + fileName);

			OutputStreamWriter ps = new OutputStreamWriter(networkFile,  "UTF-8");
			PrintWriter pw = new PrintWriter(ps);
			pw.println("Error: " + e.getMessage());
			if (extras != null) {
				StringBuilder sb=new StringBuilder();
				for(Object item : extras) {
					if(item != null) {
						sb.append(item.toString()+", ");
					}
				}
				pw.println("Extra: " + sb.toString());
			}
			pw.println("Thread: " + Thread.currentThread().getName());
			pw.println("------------ SYSTEM-INFO ------------");
			printProperty(pw, "java.class.version");
			printProperty(pw, "java.runtime.version");
			printProperty(pw, "java.specification.version");
			printProperty(pw, "java.version");
			printProperty(pw, "os.arch");
			printProperty(pw, "os.name");
			printProperty(pw, "os.version");
			printProperty(pw, "user.dir");
			printProperty(pw, "user.home");
			printProperty(pw, "user.language");
			printProperty(pw, "user.name");
			printProperty(pw, "user.timezone");
			pw.println("");

			Runtime r = Runtime.getRuntime();
			pw.println("Prozessoren :	   " + r.availableProcessors());
			pw.println("Freier Speicher JVM:	" + r.freeMemory());
			pw.println("Maximaler Speicher JVM: " + r.maxMemory());
			pw.println("Gesamter Speicher JVM:  " + r.totalMemory());
			pw.println("Gesamter Speicher Java:  "
					+ ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
							.getTotalSwapSpaceSize());

			pw.println("***  ***");

			pw.println();
			e.printStackTrace(pw);
			ps.close();
			success = true;
		} catch (FileNotFoundException exception) {
			success = false;
		} catch (IOException exception) {
			success = false;
		}
		return success;
	}

	protected String createDir(String path) {
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

	private void printProperty(PrintWriter ps, String property) {
		ps.println(property + ": " + System.getProperty(property));
	}

	protected String getCaptionPrefix() {
		return null;
	}

	public String getCaption() {
		String caption = "";
		String temp = getCaptionPrefix();
		if (temp != null) {
			caption = temp + " ";
		}
		return caption + getVersion() + " (" + System.getProperty("file.encoding") + " - "
				+ System.getProperty("sun.arch.data.model") + "-Bit)";
	}

	protected SimpleShell enableError(String path) {
		this.errorPath = path;
		if(Thread.getDefaultUncaughtExceptionHandler()==null){
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					SimpleShell.this.saveException(e);
				}
			});
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					SimpleShell.this.saveException(e);
				}
			});
		}
		return this;
	}

	protected String getVersion() {
		String result = SimpleShell.class.getPackage().getImplementationVersion();

		if (result == null) {
			result = "0.42.DEBUG";
		}

		return result;
	}
}
