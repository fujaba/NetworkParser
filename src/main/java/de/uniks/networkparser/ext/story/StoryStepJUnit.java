package de.uniks.networkparser.ext.story;

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
import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.javafx.SimpleController;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepJUnit implements ObjectCondition {
	private static final String BLACKBOXFILE="backbox.txt";
	private ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
	private NetworkParserLog logger = new NetworkParserLog().withListener(this);
	private String packageName = null;
	private String path = "lib/jacocoagent.jar";
	private SimpleSet<String> testClasses;
	private SimpleKeyValueList<String, JacocoColumn> columns = new SimpleKeyValueList<String, JacocoColumn>();
	private SimpleList<FeatureProperty> groups=new SimpleList<FeatureProperty>();
	int tabwidth = 4;
	private JacocoColumn column;
	public StoryStepJUnit() {
		this.column = JacocoColumn.create();
		this.addColumn("BBT", column);
	}

	public FeatureProperty addGroup(String label) {
		FeatureProperty feature = new FeatureProperty(Feature.JUNIT);
		feature.withStringValue(label);

		this.groups.add(feature);
		return feature;
	}

	public boolean writeHTML(String executeData, String outputFile, String label) {
		Object loader = ReflectionLoader.newInstance("org.jacoco.core.tools.ExecFileLoader");
		if(loader == null) {
			return false;
		}
		ReflectionLoader.call(loader, "load", File.class, new File(executeData));

		if(this.groups.size() < 1) {
			addGroup(label);
		}
		File htmlFile = new File(outputFile);

		// HTML Formatter
		Object formatter = createFormater();
		// Analyse Jacaco.exec
		Object output = ReflectionLoader.newInstance("org.jacoco.report.FileMultiReportOutput", File.class, htmlFile);
		Object visitor = ReflectionLoader.callStr(formatter, "createVisitor", "org.jacoco.report.IMultiReportOutput", output);
		Object info = ReflectionLoader.callChain(loader, "getSessionInfoStore", "getInfos");
		Object content = ReflectionLoader.callChain(loader, "getExecutionDataStore", "getContents");
		ReflectionLoader.call(visitor, "visitInfo", List.class, info, Collection.class, content);

		// Create Files
		for(FeatureProperty group : groups) {
			Object bundle = writeReports(loader, formatter, htmlFile, group);
			ReflectionLoader.callStr(visitor,"visitBundle", 
					"org.jacoco.core.analysis.IBundleCoverage", bundle,
					"org.jacoco.report.ISourceFileLocator", getSourceLocator());
		}
		ReflectionLoader.call(visitor, "visitEnd");
		return true;
	}

	private Object createFormater() {
		Object formater = ReflectionLoader.newInstance("org.jacoco.report.html.HTMLFormatter");

		Object table = ReflectionLoader.call(formater, "getTable");

		for(int i=0;i<columns.size();i++) {
			ReflectionLoader.callStr(table, "add",
					String.class, columns.getKeyByIndex(i),
					String.class, "ctr2",
					"org.jacoco.report.internal.html.table.IColumnRenderer", columns.getValueByIndex(i).getProxy(),
					boolean.class, false);
		}
		return formater;
	}

	private Object writeReports(Object loader, Object formatter, File outputFile, FeatureProperty group) {
		Object builder = ReflectionLoader.newInstance("org.jacoco.core.analysis.CoverageBuilder");
		if(builder == null) {
			return null;
		}
		Object data = ReflectionLoader.call(loader, "getExecutionDataStore");


		Object analyzer = ReflectionLoader.newInstanceStr("org.jacoco.core.analysis.Analyzer",
							"org.jacoco.core.data.ExecutionDataStore", data,
							"org.jacoco.core.analysis.ICoverageVisitor", builder);
		String rootBin ="bin/";
		for(Clazz clazz : group.getClazzes()) {
			CharacterBuffer buffer= new CharacterBuffer().with(rootBin, clazz.getName());
			buffer.replace('*', (char)0);
			buffer.replace('.', '/');
			File classfiles = new File(buffer.toString());
			try {
				ReflectionLoader.call(analyzer, "analyzeAll", File.class, classfiles);
			}catch (Exception e) {
//				e.printStackTrace();
				System.out.println("ERROR");
			}
		}
		return ReflectionLoader.call(builder, "getBundle", group.getStringValue());
	}

	private Object getSourceLocator() {
		List<File> sourcefiles = new SimpleList<File>();
		File file = new File("src/main/java");
		if(file.exists()) {
			sourcefiles.add(file);
		} else {
			sourcefiles.add(new File("src"));
		}
		String encoding = System.getProperty("file.encoding");

		Object multi = ReflectionLoader.newInstance("org.jacoco.report.MultiSourceFileLocator", int.class, tabwidth);
		for (final File f : sourcefiles) {
			Object sourceFile = ReflectionLoader.newInstance("org.jacoco.report.DirectorySourceFileLocator", File.class, f, String.class, encoding, int.class, tabwidth);
			ReflectionLoader.callStr(multi, "add", "org.jacoco.report.ISourceFileLocator" , sourceFile);
		}
		return multi;
	}

	public boolean executeBlackBoxTest(String path) {
		this.path = path;
		try {
			tester.test(packageName, logger);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean executeBlackBoxEvent(SimpleEvent event) {
		FileBuffer.writeFile(this.path+BLACKBOXFILE, event.toString()+BaseItem.CRLF, FileBuffer.APPEND);
		return true;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		if(NetworkParserLog.INFO.equalsIgnoreCase(evt.getType()) ||
			NetworkParserLog.WARNING.equalsIgnoreCase(evt.getType()) ||
			NetworkParserLog.DEBUG.equalsIgnoreCase(evt.getType()) ||
			NetworkParserLog.ERROR.equalsIgnoreCase(evt.getType())) {
			// Event from BlackBoxTester
			return this.executeBlackBoxEvent(evt);
		}

		if(packageName == null) {
			return false;
		}
		// EXECUTE JUNIT AND JACOCO

		// PATH IS "doc/"
		String path = "doc/";
		String label = "JUnit - Jacoco";
		if(evt.getSource() instanceof Story) {
			Story story = (Story) evt.getSource();

			path = story.getPath();
			if(story.getLabel() != null) {
				label = story.getLabel();
			}
		}


		if(new File(this.path).exists() == false) {
			return false;
		}
		SimpleController controller = SimpleController.create();
		String[] list = null;
		if(testClasses != null) {
			list = testClasses.toArray(new String[testClasses.size()]);
		}
		controller.withAgent(this.path, packageName, list);
		controller.withErrorPath(path);

//		controller.withOutput("t.txt");
		controller.start();

		// Now Add


		// ADD RESULT TO STORY DOCUMENTATION FOR BLACKBOX AND JACOCO
		this.writeHTML(path+"jacoco.exec", path+"jacoco", label);
		CharacterBuffer indexFile = FileBuffer.readFile(path+"jacoco/index.html");
		if(indexFile != null) {
			String search = "<tfoot><tr><td>Total</td><td class=\"bar\">";
			int pos = indexFile.indexOf(search);
			if(pos>0) {
				pos +=search.length();
				int end = indexFile.indexOf("<", pos);
				if(end>0) {
					String name = indexFile.substring(pos, end);
					HTMLEntity output = (HTMLEntity) evt.getNewValue();
					XMLEntity div = output.createTag("div", output.getBody());
					XMLEntity p = output.createTag("p", div);
					p.withCloseTag();
					int level=0;
					try {
						String[] split = name.split("of");
						if(split.length==2) {
							Integer no = Integer.valueOf(split[0].trim());
							Integer sum = Integer.valueOf(split[1].trim());
							int proz = ((no/sum) * 100);
							if(proz<50) {
								level = 2;
							} else if(proz<80) {
								level = 1;
							}
						}
					}catch (Exception e) {
					}
					XMLEntity textnode = output.createTag("div", div);
					textnode.add("class", "notify-text");
					if(level == 0) {
						div.add("class", "notify notify-red");
						p.add("class", "symbol icon-error");
					} else if(level == 1) {
						div.add("class", "notify notify-yellow");
						p.add("class", "symbol icon-info");
					} else {
						div.add("class", "notify notify-green");
						p.add("class", "symbol icon-tick");
					}
					textnode.withValueItem("MISSED INDUDUCTION: "+name);
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
	 * @return ThisComponent
	 */
	public StoryStepJUnit withPackageName(String packageName) {
		this.packageName = packageName;
		return this;
	}

	/**
	 * @param classNames ClassNames of Tests
	 * @return ThisComponent
	 */
	public StoryStepJUnit withTestClasses(String... classNames) {
		if(classNames == null) {
			return this;
		}
		if(this.testClasses == null) {
			this.testClasses = new SimpleSet<String>();
		}
		for(String item : classNames) {
			this.testClasses.add(item);
		}
		return this;
	}

	public StoryStepJUnit withAgentPath(String path) {
		this.path = path;
		return this;
	}

	public StoryStepJUnit addColumn(String name, JacocoColumn callback) {
		this.columns.add(name, callback);
		return this;
	}

	public void addValueToList(String key, int no) {
		this.column.addValueToList(key, no);
	}
}
