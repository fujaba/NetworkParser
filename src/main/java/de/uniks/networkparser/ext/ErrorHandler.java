package de.uniks.networkparser.ext;

/*
 * The MIT License
 * 
 * Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class ErrorHandler.
 *
 * @author Stefan
 */
public class ErrorHandler implements Thread.UncaughtExceptionHandler {
  
  /** The Constant TYPE. */
  public static final String TYPE = "ERROR";
  private String path;
  private Object stage;
  private DateTimeEntity startDate = new DateTimeEntity();
  private SimpleList<ObjectCondition> list = new SimpleList<ObjectCondition>();

  /**
   * Save error file.
   *
   * @param prefix the prefix
   * @param fileName the file name
   * @param filePath the file path
   * @param e the e
   * @return true, if successful
   */
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
      ps.println("IP: " + NodeProxyTCP.getIpAdress());
      ps.println("MAC: " + NodeProxyTCP.getMacAdress());

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

  /**
   * Save heap space.
   *
   * @param prefix the prefix
   * @return true, if successful
   */
  public boolean saveHeapSpace(String prefix) {
    String filepath = createDir(this.path);
    if (filepath == null) {
      return false;
    }
    if (filepath.length() > 0 && !filepath.endsWith("/")) {
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

  /**
   * Gets the JVM start up.
   *
   * @return the JVM start up
   */
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

  /**
   * Gets the pid.
   *
   * @return the pid
   */
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

  /**
   * Prints the property.
   *
   * @param ps the ps
   * @param property the property
   */
  public static void printProperty(PrintStream ps, String property) {
    ps.println(property + ": " + System.getProperty(property));
  }

  /**
   * Prints the sub trace.
   *
   * @param ps the ps
   * @param prefix the prefix
   * @param index the index
   * @param e the e
   */
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

  /**
   * Creates the dir.
   *
   * @param path the path
   * @return the string
   */
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

  /**
   * With URL.
   *
   * @param value the value
   * @return the error handler
   */
  public ErrorHandler withURL(String value) {
    this.path = value;
    return this;
  }

  /**
   * Gets the file name.
   *
   * @param filepath the filepath
   * @param prefix the prefix
   * @param fileName the file name
   * @return the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
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
    if (filepath.length() > 0 && !filepath.endsWith("/")) {
      filepath += "/";
    }
    String fullfilename = null;
    if (prefix != null) {
      fullfilename = filepath + prefix + fileName;
    } else {
      fullfilename = filepath + fileName;
    }

    File file = new File(fullfilename);
    if (!file.exists() && !file.createNewFile()) {
        return null;
    }
    return file;
  }

  public Object getScreen() {
      return ReflectionLoader.newInstance(ReflectionLoader.RECTANGLE, ReflectionLoader.DIMENSION,
              ReflectionLoader.callChain(ReflectionLoader.TOOLKIT, "getDefaultToolkit", "getScreenSize"));
  }
  
  /**
   * Save screen shoot.
   *
   * @param prefix the prefix
   * @param fileName the file name
   * @param filePath the file path
   * @param currentStage the current stage
   * @return the exception
   */
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
      
      Object rect = getScreen();
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

  /**
   * Save exception.
   *
   * @param e the e
   */
  public void saveException(Throwable e) {
    saveException(e, this.stage, true);
  }

  /**
   * Save exception.
   *
   * @param e the e
   * @param throwException the throw exception
   */
  public void saveException(Throwable e, boolean throwException) {
    saveException(e, this.stage, throwException);
  }

  /**
   * Write output.
   *
   * @param output the output
   * @param error the error
   * @return true, if successful
   */
  public boolean writeOutput(String output, boolean error) {
    String fullFileName = "";
    if (this.path != null) {
      fullFileName = this.path;
      if (fullFileName.length() > 0 && !fullFileName.endsWith("/")) {
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
      if (!file.exists() && !file.createNewFile()) {
        return false;
      }
    } catch (IOException e1) {
      return false;
    }
    FileOutputStream stream = null;
    boolean result = true;
    try {
      stream = new FileOutputStream(file, true);
      stream.write(output.getBytes());
    } catch (IOException e) {
      return false;
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          result = false;
        }
      }
    }
    return result;
  }

  /**
   * Uncaught exception.
   *
   * @param t the t
   * @param e the e
   */
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    saveException(e, stage, true);
  }

  /**
   * Gets the prefix.
   *
   * @return the prefix
   */
  public String getPrefix() {
    return new DateTimeEntity().toString("yyyymmdd_HHMMSS_");
  }

  /**
   * Save exception.
   *
   * @param e the e
   * @param stage the stage
   * @param throwException the throw exception
   * @return true, if successful
   */
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

  /**
   * Gets the stage.
   *
   * @return the stage
   */
  public Object getStage() {
    return stage;
  }

  /**
   * With stage.
   *
   * @param value the value
   * @return the error handler
   */
  public ErrorHandler withStage(Object value) {
    this.stage = value;
    return this;
  }

  /**
   * Adds the listener.
   *
   * @param world the world
   */
  public void addListener(ObjectCondition world) {
    list.add(world);
  }

  /**
   * Gets the path.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }
}
