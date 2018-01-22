package de.uniks.networkparser.ext.story;

import java.io.File;
import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class StoryStepBlackBox implements ObjectCondition {
	private ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
	private NetworkParserLog logger = new NetworkParserLog().withListener(this);
	private String packageName = null;
	
	public boolean writeHTML(String executeData, String outputFile) {
		Object loader = ReflectionLoader.newInstance("org.jacoco.core.tools.ExecFileLoader");
		if(loader == null) {
			return false;
		}
		ReflectionLoader.call("load", loader, File.class, new File(executeData));
		
		Object storage = ReflectionLoader.call("getExecutionDataStore", loader);
		
		Object bundle = analyze(storage,  new File("bin"), "Report");
		writeReports(bundle, loader, new File("testHTML"));
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

			Object output = ReflectionLoader.newInstance("org.jacoco.report.FileMultiReportOutput", File.class, html);
			
			Object visitor = ReflectionLoader.callStr("createVisitor", formater, "org.jacoco.report.IMultiReportOutput", output);
			
			Object info = ReflectionLoader.callChain(loader, "getSessionInfoStore", "getInfos");
			Object content = ReflectionLoader.callChain(loader, "getExecutionDataStore", "getContents");
			ReflectionLoader.call("visitInfo", visitor, List.class, info, Collection.class, content);
			
			ReflectionLoader.callStr("visitBundle", visitor, 
					"org.jacoco.core.analysis.IBundleCoverage", bundle, 
					"org.jacoco.report.ISourceFileLocator", getSourceLocator());
//			visitor.visitBundle(bundle, getSourceLocator());
			ReflectionLoader.call("visitEnd", visitor);
//			visitor.visitEnd();
		}
		
		int tabwidth = 4;
		
		private Object getSourceLocator() {
			List<File> sourcefiles = new SimpleList<File>();
			sourcefiles.add(new File("src/main/java"));
			String encoding = System.getProperty("file.encoding");
			
			Object multi = ReflectionLoader.newInstance("org.jacoco.report.MultiSourceFileLocator", int.class, tabwidth);
			for (final File f : sourcefiles) {
				Object sourceFile = ReflectionLoader.newInstance("org.jacoco.report.DirectorySourceFileLocator", File.class, f, String.class, encoding, int.class, tabwidth);
				ReflectionLoader.callStr("add", multi,"org.jacoco.report.ISourceFileLocator" , sourceFile); 
			}
			return multi;
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
			
		}
		if(packageName == null) {
			return false;
		}
		try {
			tester.test(packageName, logger);
		} catch (Exception e) {
			return false;
		}
		
		
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		Story story = (Story) evt.getSource();
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
	public StoryStepBlackBox withLogger(NetworkParserLog logger) {
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
	public StoryStepBlackBox withPackageName(String packageName) {
		this.packageName = packageName;
		return this;
	}

}
