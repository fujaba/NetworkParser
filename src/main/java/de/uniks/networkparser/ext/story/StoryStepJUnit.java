package de.uniks.networkparser.ext.story;

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
import java.net.URL;
import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepJUnit extends StoryElement implements ObjectCondition {
	private static final String BLACKBOXFILE = "backbox.txt";
	private ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
	private NetworkParserLog logger = new NetworkParserLog().withListener(this);
	private String packageName = null;
	private String task = "lib/jacocoagent.jar";
	private SimpleSet<String> testClasses;
	private SimpleKeyValueList<String, JacocoColumn> columns = new SimpleKeyValueList<String, JacocoColumn>();
	private SimpleList<Feature> groups = new SimpleList<Feature>();
	int tabwidth = 4;
	private JacocoColumn column;
	private IdMap map;
	private GraphModel model;

	public StoryStepJUnit() {
		initColumn(false);
	}
	public boolean initColumn(boolean showError) {
		if(this.column != null) {
			return true;
		}
		this.column = JacocoColumn.create(showError);
		if (this.column != null) {
			this.addColumn("BBT", column);
		}
		return this.column != null;
	}

	public Feature addGroup(String label) {
		Feature feature = Feature.JUNIT.create();
		feature.withStringValue(label);

		this.groups.add(feature);
		return feature;
	}

	public boolean writeHTML(String executeData, String outputFile, String label) {
		Object loader = ReflectionLoader.newInstance("org.jacoco.core.tools.ExecFileLoader");
		if (loader == null) {
			return false;
		}
		ReflectionLoader.call(loader, "load", File.class, new File(executeData));

		if (this.groups.size() < 1) {
			addGroup(label);
		}
		File htmlFile = new File(outputFile);

		/* HTML Formatter */
		Object formatter = createFormater();
		/* Analyse Jacaco.exec */
		Object output = ReflectionLoader.newInstance("org.jacoco.report.FileMultiReportOutput", File.class, htmlFile);
		Object visitor = ReflectionLoader.callStr(formatter, "createVisitor", "org.jacoco.report.IMultiReportOutput",
				output);
		Object info = ReflectionLoader.callChain(loader, "getSessionInfoStore", "getInfos");
		Object content = ReflectionLoader.callChain(loader, "getExecutionDataStore", "getContents");
		ReflectionLoader.call(visitor, "visitInfo", List.class, info, Collection.class, content);

		/* Create Files */
		for (Feature group : groups) {
			Object bundle = writeReports(loader, formatter, htmlFile, group);
			ReflectionLoader.callStr(visitor, "visitBundle", "org.jacoco.core.analysis.IBundleCoverage", bundle,
					"org.jacoco.report.ISourceFileLocator", getSourceLocator());
		}
		ReflectionLoader.call(visitor, "visitEnd");
		return true;
	}

	private Object createFormater() {
		Object formater = ReflectionLoader.newInstance("org.jacoco.report.html.HTMLFormatter");

		Object table = ReflectionLoader.call(formater, "getTable");

		for (int i = 0; i < columns.size(); i++) {
			ReflectionLoader.callStr(table, "add", String.class, columns.getKeyByIndex(i), String.class, "ctr2",
					"org.jacoco.report.internal.html.table.IColumnRenderer", columns.getValueByIndex(i).getProxy(),
					boolean.class, false);
		}
		return formater;
	}

	private Object writeReports(Object loader, Object formatter, File outputFile, Feature group) {
		Object builder = ReflectionLoader.newInstance("org.jacoco.core.analysis.CoverageBuilder");
		if (builder == null) {
			return null;
		}
		Object data = ReflectionLoader.call(loader, "getExecutionDataStore");

		Object analyzer = ReflectionLoader.newInstanceStr("org.jacoco.core.analysis.Analyzer",
				"org.jacoco.core.data.ExecutionDataStore", data, "org.jacoco.core.analysis.ICoverageVisitor", builder);
		String rootBin = "bin/";
		for (Clazz clazz : group.getClazzes()) {
			CharacterBuffer buffer = new CharacterBuffer().with(rootBin, clazz.getName());
			buffer.replace('*', (char) 0);
			buffer.replace('.', '/');
			File classfiles = new File(buffer.toString());
			try {
				ReflectionLoader.call(analyzer, "analyzeAll", File.class, classfiles);
			} catch (Exception e) {
				logger.error(this, "writeReports", "ERROR", e);
			}
		}
		return ReflectionLoader.call(builder, "getBundle", group.getStringValue());
	}

	private Object getSourceLocator() {
		List<File> sourcefiles = new SimpleList<File>();
		File file = new File("src/main/java");
		if (file.exists()) {
			sourcefiles.add(file);
		} else {
			sourcefiles.add(new File("src"));
		}
		String encoding = System.getProperty("file.encoding");

		Object multi = ReflectionLoader.newInstance("org.jacoco.report.MultiSourceFileLocator", int.class, tabwidth);
		for (final File f : sourcefiles) {
			Object sourceFile = ReflectionLoader.newInstance("org.jacoco.report.DirectorySourceFileLocator", File.class,
					f, String.class, encoding, int.class, tabwidth);
			ReflectionLoader.callStr(multi, "add", "org.jacoco.report.ISourceFileLocator", sourceFile);
		}
		return multi;
	}

	public boolean executeBlackBoxTest(String path) {
		this.task = path;
		try {
			initColumn(true);
			tester.test(packageName, logger);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean executeBlackBoxEvent(SimpleEvent event) {
		FileBuffer.writeFile(this.task + BLACKBOXFILE, event.toString() + BaseItem.CRLF, FileBuffer.APPEND);
		return true;
	}

	public boolean recompile(String... output) {
		if (this.packageName == null) {
			return false;
		}
		SimpleController controller = SimpleController.create();
		String[] excludes = null;
		if (this.testClasses != null) {
			excludes = this.testClasses.toArray(new String[testClasses.size()]);
		}
		controller.withPackageName(this.packageName, excludes);
		if (output != null && output.length > 0) {
			controller.withOutput(output[0]);
		}
		return controller.start() >= 0;
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		if (NetworkParserLog.INFO.equalsIgnoreCase(evt.getType())
				|| NetworkParserLog.WARNING.equalsIgnoreCase(evt.getType())
				|| NetworkParserLog.DEBUG.equalsIgnoreCase(evt.getType())
				|| NetworkParserLog.ERROR.equalsIgnoreCase(evt.getType())) {
			/* Event from BlackBoxTester */
			System.out.println("WRITE HTML: "+this.task+"::"+packageName +" EXEC");
			return this.executeBlackBoxEvent(evt);
		}

		if (packageName == null) {
			return false;
		}
		/* EXECUTE JUNIT AND JACOCO */

		/* PATH IS "doc/" */
		String path = "doc/";
		String label = "JUnit - Jacoco";
		if (evt.getSource() instanceof Story) {
			Story story = (Story) evt.getSource();

			path = story.getPath();
			if (story.getLabel() != null) {
				label = story.getLabel();
			}
		}

		if (new File(this.task).exists() == false) {
			return false;
		}
		SimpleController controller = SimpleController.create();
		String[] list = null;
		if (testClasses != null) {
			list = testClasses.toArray(new String[testClasses.size()]);
		}
		controller.withAgent(this.task, packageName, list);
		controller.withErrorPath(path);

		controller.start();

		/* Now Add */
		/* ADD RESULT TO STORY DOCUMENTATION FOR BLACKBOX AND JACOCO */
		System.out.println("WRITE HTML");
		this.writeHTML(path + "jacoco.exec", path + "jacoco", label);
		CharacterBuffer indexFile = FileBuffer.readFile(path + "jacoco/index.html");
		if (indexFile != null) {
			String search = "<tfoot><tr><td>Total</td><td class=\"bar\">";
			int pos = indexFile.indexOf(search);
			if (pos > 0) {
				pos += search.length();
				int end = indexFile.indexOf("<", pos);
				if (end > 0) {
					String name = indexFile.substring(pos, end);
					HTMLEntity output = (HTMLEntity) evt.getNewValue();
					XMLEntity div = output.createTag("div", output.getBody());
					XMLEntity p = output.createTag("p", div);
					p.withCloseTag();
					int level = 0;
					try {
						String[] split = name.split("of");
						if (split.length == 2) {
							Integer no = Integer.valueOf(split[0].trim());
							Integer sum = Integer.valueOf(split[1].trim());
							int proz = ((no / sum) * 100);
							if (proz < 50) {
								level = 2;
							} else if (proz < 80) {
								level = 1;
							}
						}
					} catch (Exception e) {
					}
					XMLEntity textnode = output.createTag("div", div);
					textnode.add("class", "notify-text");
					if (level == 0) {
						div.add("class", "notify notify-red");
						p.add("class", "symbol icon-error");
					} else if (level == 1) {
						div.add("class", "notify notify-yellow");
						p.add("class", "symbol icon-info");
					} else {
						div.add("class", "notify notify-green");
						p.add("class", "symbol icon-tick");
					}
					textnode.withValueItem("MISSED INDUDUCTION: " + name);
				}
			}
		}
		return true;
	}

	/**
	 * @return the logger
	 */
	public NetworkParserLog getLogger() {
		return logger;
	}

	/**
	 * @param logger the logger to set
	 * @return ThisComponent
	 */
	public StoryStepJUnit withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName the packageName to set
	 * @param excludes    Exclude Packages
	 * @return ThisComponent
	 */
	public StoryStepJUnit withPackageName(String packageName, String... excludes) {
		this.packageName = packageName;
		if (this.testClasses == null) {
			this.testClasses = new SimpleSet<String>();
		}
		if (excludes != null) {
			for (String item : excludes) {
				this.testClasses.with(item);
			}
		}
		return this;
	}

	public StoryStepJUnit withGradleTask(String task) {
		this.task = task;
		return this;
	}

	/**
	 * @param classNames ClassNames of Tests
	 * @return ThisComponent
	 */
	public StoryStepJUnit withTestClasses(String... classNames) {
		if (classNames == null) {
			return this;
		}
		if (this.testClasses == null) {
			this.testClasses = new SimpleSet<String>();
		}
		for (String item : classNames) {
			this.testClasses.add(item);
		}
		return this;
	}

	public StoryStepJUnit withAgent(String path) {
		this.task = path;
		return this;
	}

	public StoryStepJUnit addColumn(String name, JacocoColumn callback) {
		this.columns.add(name, callback);
		return this;
	}

	public void addValueToList(String key, int no) {
		this.column.addValueToList(key, no);
	}

	public StoryStepJUnit withUseCase(Story story, GraphModel model) {
		this.map = new IdMap();
		this.model = model;
		if (this.column != null) {
			story.add(this);
			/* Check for ReCompile */
			if (this.model != null && this.model instanceof ClassModel) {
				ModelGenerator generator = ((ClassModel) this.model).getGenerator();
				if (generator != null) {
					withPackageName(generator.getLastGenRoot());
					URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
					recompile(location.getPath().substring(1));
				}
			}

			for (Clazz clazz : this.model.getClazzes()) {
				GenericCreator creator = new GenericCreator();
				creator.withClass(clazz.getName(false));
				map.withCreator(creator);
			}
		}

		return this;
	}

	public Object createElement(Clazz element, Object... values) {
		SendableEntityCreator creator = map.getCreator(element.getName(false), true);
		if (creator != null) {
			Object newInstance = null;
			try {
				newInstance = creator.getSendableInstance(false);
				if (values != null && values.length % 2 == 0) {
					for (int i = 0; i < values.length; i += 2) {
						if (values[i] != null && values[i] instanceof String) {
							setting(creator, newInstance, (String) values[i], values[i + 1]);
						}
					}
				}
			} catch (Exception e) {
			}
			return newInstance;
		}
		return null;
	}

	public boolean setValue(Object element, String attribute, String value) {
		if (map != null && element != null) {
			String name = element.getClass().getName();
			SendableEntityCreator creator = map.getCreator(name, true);
			return setting(creator, element, attribute, value);
		}
		return false;
	}

	private boolean setting(SendableEntityCreator creator, Object element, String attribute, Object value) {
		if (creator != null) {
			return creator.setValue(element, attribute, value, SendableEntityCreator.NEW);
		}
		return false;
	}

	public Object createListener() {
		Class<?> class1 = ReflectionLoader.getClass("junit.framework.TestListener");
		if (class1 != null) {
			return ReflectionLoader.createProxy(this, class1);
		}
		return null;
	}
	/* TestListener */
	/**
	 * TestListener
	 * @param test TestMethjod
	 * @param e	Exception
	 */
	public void addError(Object test, Throwable e) {
		if(logger != null) {
			logger.error(test, "addError", e);
		}
	}

	public void addFailure(Object test, Object e) {
		if(logger != null) {
			logger.fatal(test, "addFailure", e);
		}
	}

	public void end(Object test, String method) {
		if(logger != null) {
			logger.end(test, method, "end Method");
		}
	}

	public void startTest(Object test, String method) {
		if(logger != null) {
			logger.start(test, method, "start Method");
		}
	}

	@Override
	public String getOutputFile(boolean calculate) {
		return "jacoco/index.html";
	}

	@Override
	public String getLabel() {
		return "Jacoco";
	}

	@Override
	public boolean writeToFile(String... fileName) {
		return true;
	}
}