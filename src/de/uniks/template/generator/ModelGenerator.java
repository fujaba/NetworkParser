package de.uniks.template.generator;

import java.util.Iterator;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ForeachCondition;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.TemplateResultModel;
import de.uniks.template.generator.condition.JavaListCondition;
import de.uniks.template.generator.condition.JavaMethodBodyCondition;
import de.uniks.template.generator.java.JavaClazz;
import de.uniks.template.generator.java.JavaSet;

public class ModelGenerator extends BasicGenerator{
	private FeatureSet features = Feature.getAll();
	public SimpleKeyValueList<String, ParserCondition> customTemplate;

	public SimpleKeyValueList<String, ParserCondition> getTemplates() {
		if(customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
			addParserCondition(new FeatureCondition());
			addParserCondition(new ImportCondition());
			addParserCondition(new ForeachCondition());
			addParserCondition(new TemplateFragmentCondition());
			addParserCondition(new IfCondition());
			addParserCondition(new IfCondition().withKey(IfCondition.IFNOT));
			addParserCondition(new JavaMethodBodyCondition());
			addParserCondition(new JavaListCondition());
		}
		return customTemplate;
	}
	
	protected void addParserCondition(ParserCondition condition) {
		customTemplate.add(condition.getKey(), condition);
	}
	
	public SendableEntityCreator generate(GraphMember item) {
		if(item instanceof GraphModel == false) {
			return null;
		}
		return generate("src", (GraphModel)item);
	}

	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof GraphModel == false) {
			return null;
		}
		return generateJava("src", (GraphModel)item, parameters);
	}
	
	
	public SendableEntityCreator generate(String rootDir, GraphModel model) {
		return generateJava(rootDir, model, null);
	}
	
	
	public SendableEntityCreator generateJava(String rootDir, GraphModel model, TextItems parameters) {
		SimpleList<BasicGenerator> templates = new SimpleList<BasicGenerator>();

		templates.add(new JavaClazz());
		templates.add(new JavaSet());
		return generating(rootDir, model, parameters, templates, true);
	}

	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model) {
		return generateTypescript(rootDir, model, null);
	}
	
	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model, TextItems parameters) {
		SimpleList<BasicGenerator> templates = new SimpleList<BasicGenerator>();

//		templates.add(new JavaClazz());
		return generating(rootDir, model, parameters, templates, true);
	}

	
	public SendableEntityCreator generating(String rootDir, GraphModel model, TextItems parameters, SimpleList<BasicGenerator> templates, boolean writeFiles) {
		if(rootDir == null) {
			rootDir = "";
		}else if(rootDir.endsWith("/") == false) {
			rootDir = rootDir + "/";
		}
		String name = model.getName();
		if(name == null) {
			name="i.love.sdmlib";
		}
		rootDir += name.replaceAll("\\.", "/")+"/";
		
		TemplateResultModel result = new TemplateResultModel();
		result.withTemplate(this.getTemplates());
		result.withFeatures(this.features);
		if(parameters == null) {
			parameters = new TextItems();
			parameters.withDefaultLabel(false);
		}
		result.withLanguage(parameters);
		
		for(BasicGenerator template : templates) {
			template.withOwner(this);
		}
		
		for(Clazz clazz : model.getClazzes()) {
			for(BasicGenerator template : templates) {
				TemplateResultFile resultFile = template.executeClazz(clazz, result);

				template.executeTemplate(resultFile, result, clazz);
				result.add(resultFile);
			}
		}
		if(writeFiles) {
			for(TemplateResultFile file : result) {
				FileBuffer.writeFile(rootDir + file.getFileName(), file.toString());
			}
		}
		return result;
	}
	
	@Override
	public FeatureProperty getFeature(Feature value, Clazz... values) {
		if(this.features != null) {
			for(Iterator<FeatureProperty> i = this.features.iterator();i.hasNext();) {
				FeatureProperty item = i.next();
				if(item.equals(value)) {
					return item;
				}
			}
		}
		return null;
	}
	
	@Override
	public Class<?> getTyp() {
		return GraphModel.class;
	}
}
