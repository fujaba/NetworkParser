package de.uniks.networkparser.ext.story;

import java.io.File;
import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.javafx.SimpleController;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

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
		ReflectionLoader.call("load", loader, File.class, new File(executeData));
		
//		Object storage = ReflectionLoader.call("getExecutionDataStore", loader);
		
		writeReports(loader, new File(outputFile));
		
//		Object bundle = analyze(storage,  new File("bin"), label);
		
		
//		writeReports(bundle, loader, new File(outputFile));
		return true;
	}
		
	private Object analyze(Object data, File classfiles, String name) {
		Object builder = ReflectionLoader.newInstance("org.jacoco.core.analysis.CoverageBuilder");
		if(builder == null) {
			return null;
		}
		Object analyzer = ReflectionLoader.newInstanceStr("org.jacoco.core.analysis.Analyzer", 
							"org.jacoco.core.data.ExecutionDataStore", data,
							"org.jacoco.core.analysis.ICoverageVisitor", builder);
		
		ReflectionLoader.call("analyzeAll", analyzer, File.class, classfiles);
		
		return ReflectionLoader.call("getBundle", builder, name);
	}
	
	private void writeReports(Object bundle, Object loader, File html) {
        Object formater = ReflectionLoader.newInstance("org.jacoco.report.html.HTMLFormatter");
		
		Object table = ReflectionLoader.call("getTable", formater);
		
		for(int i=0;i<columns.size();i++) {
			ReflectionLoader.callStr("add", table, 
					String.class, columns.getKeyByIndex(i),
					String.class, "ctr2",
					"org.jacoco.report.internal.html.table.IColumnRenderer", columns.getValueByIndex(i).getProxy(),
					boolean.class, false);
		}
		
		Object output = ReflectionLoader.newInstance("org.jacoco.report.FileMultiReportOutput", File.class, html);
		
		Object visitor = ReflectionLoader.callStr("createVisitor", formater, "org.jacoco.report.IMultiReportOutput", output);
		
		Object info = ReflectionLoader.callChain(loader, "getSessionInfoStore", "getInfos");
		Object content = ReflectionLoader.callChain(loader, "getExecutionDataStore", "getContents");

		ReflectionLoader.call("visitInfo", visitor, List.class, info, Collection.class, content);
		
		
		
		if (groups.isEmpty()) {
		
		} else {
			final IReportGroupVisitor groupVisitor = visitor
					.visitGroup(group.name);
			for (final GroupElement child : group.children) {
				createReport(groupVisitor, child);
			}
		}

		ReflectionLoader.call("visitEnd", visitor);
	}
	private Object createBundle(Object executionDataStore, GroupElement group) {
		Object builder = ReflectionLoader.newInstance("org.jacoco.core.analysis.CoverageBuilder");
		if(builder == null) {
			return null;
		}
		Object analyzer = ReflectionLoader.newInstanceStr("org.jacoco.core.analysis.Analyzer", 
							"org.jacoco.core.data.ExecutionDataStore", executionDataStore,
							"org.jacoco.core.analysis.ICoverageVisitor", builder);
		
		
		for (final Iterator<?> i = group.classfiles.iterator(); i.hasNext();) {
			final Resource resource = (Resource) i.next();
			if (resource.isDirectory() && resource instanceof FileResource) {
				analyzer.analyzeAll(((FileResource) resource).getFile());
			} else {
				final InputStream in = resource.getInputStream();
				analyzer.analyzeAll(in, resource.getName());
				in.close();
			}
		}
		final IBundleCoverage bundle = builder.getBundle(group.name);
		
		
		
		
		ReflectionLoader.call("analyzeAll", analyzer, File.class, classfiles);
		
		return ReflectionLoader.call("getBundle", builder, name);
		
		
		final Analyzer analyzer = new Analyzer(executionDataStore, builder);
		for (final Iterator<?> i = group.classfiles.iterator(); i.hasNext();) {
			final Resource resource = (Resource) i.next();
			if (resource.isDirectory() && resource instanceof FileResource) {
				analyzer.analyzeAll(((FileResource) resource).getFile());
			} else {
				final InputStream in = resource.getInputStream();
				analyzer.analyzeAll(in, resource.getName());
				in.close();
			}
		}
		final IBundleCoverage bundle = builder.getBundle(group.name);
		return bundle;
	}
	private createReport(Object visitor, Object group) {
		ReflectionLoader.callStr("visitBundle", visitor, 
				"org.jacoco.core.analysis.IBundleCoverage", bundle, 
				"org.jacoco.report.ISourceFileLocator", getSourceLocator());
	}
	
	private Object getSourceLocator() {
		List<File> sourcefiles = new SimpleList<File>();
		sourcefiles.add(new File("src/main/java"));
		sourcefiles.add(new File("src"));
		String encoding = System.getProperty("file.encoding");
		
		Object multi = ReflectionLoader.newInstance("org.jacoco.report.MultiSourceFileLocator", int.class, tabwidth);
		for (final File f : sourcefiles) {
			Object sourceFile = ReflectionLoader.newInstance("org.jacoco.report.DirectorySourceFileLocator", File.class, f, String.class, encoding, int.class, tabwidth);
			ReflectionLoader.callStr("add", multi,"org.jacoco.report.ISourceFileLocator" , sourceFile); 
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
		FileBuffer.writeFile(this.path+BLACKBOXFILE, event.toString()+BaseItem.CRLF, true);
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
		
//		HTMLEntity element = (HTMLEntity) evt.getNewValue();
//		Story story = (Story) evt.getSource();
//		if(this.value != null) {
//			int counter = story.getCounter();
//			XMLEntity textItem = element.createBodyTag("p");
//			textItem.add("class", "step");
//			String textValue = "";
//			if(counter>=0) {
//				textValue = "Step "+ counter+": ";
//			}
//			textValue += this.value;
//			
//			textItem.withValueItem(textValue);
//		}
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
