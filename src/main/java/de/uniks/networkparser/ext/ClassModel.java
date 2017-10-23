package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.xml.HTMLEntity;

public class ClassModel extends GraphModel {
	public static final String DEFAULTPACKAGE = "i.love.sdmlib";
	private ModelGenerator generator = new ModelGenerator().withDefaultModel(this);

	public ClassModel() {
		name = DEFAULTPACKAGE;
		setAuthorName(System.getProperty("user.name"));
	}

	/**
	 * Constructor
	 * 
	 * @param packageName
	 *            PackageName of ClassModel
	 */
	public ClassModel(String packageName) {
		this();
		with(packageName);
	}

	public ClassModel withoutFeature(Feature feature) {
		this.generator.withoutFeature(feature);
		return this;
	}
	
	public ModelGenerator getGenerator() {
		return generator;
	}

	public FeatureProperty getFeature(Feature feature, Clazz... clazzes) {
		return this.generator.getFeature(feature, clazzes);
	}
	
	@Override
	public boolean dumpHTML(String diagramName) {
		HTMLEntity html = new HTMLEntity();
		html.withGraph(this);
		if(diagramName == null) {
			diagramName = this.getName();
		}
		if(diagramName == null) {
			diagramName = "Model";
		}
		return FileBuffer.writeFile("doc/"+diagramName+".html", html.toString());
	}
	
	@Override
	public ClassModel generate() {
		getGenerator().generate(this);
		return this;
	}
	
	@Override
	public ClassModel generate(String rootDir) {
		getGenerator().generate(rootDir, this);
		return this;
	}
	
	@Override
	public boolean add(Object... values) {
		if(values == null) {
			return true;
		}
		boolean add=true;
		for(Object item : values) {
			if(item instanceof Annotation) {
				super.withAnnotation((Annotation) item);
			} else if(item instanceof Clazz) {
				Clazz clazz = (Clazz) item;
				clazz.setClassModel(this);
			} else {
				add = false;
			}
		}
		return add;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ClassModel();
	}
	// Override some Method because change ReturnValue
	@Override
	public ClassModel with(String name) {
		super.with(name);
		return this;
	}
	
	@Override
	public ClassModel without(GraphMember... values) {
		super.without(values);
		return this;
	}
	
	@Override
	public ClassModel withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}

	@Override
	public ClassModel with(Annotation value) {
		super.with(value);
		return this;
	}
}
