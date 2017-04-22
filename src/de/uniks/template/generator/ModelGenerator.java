package de.uniks.template.generator;

import java.util.Iterator;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ForeachCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.TemplateCondition;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.TemplateResultFragment;
import de.uniks.template.TemplateResultModel;
import de.uniks.template.generator.java.JavaClazz;

public class ModelGenerator extends BasicGenerator{
	private SimpleSet<FeatureProperty> features = Feature.getAll();
	
	public SimpleKeyValueList<String, ParserCondition> customTemplate;
//	private 
//	public void generate(String rootDir, GraphModel model) {
//		
//		this.creatorFactory = new JavaCreatorFactory(this);
//		this.setFactory =  new JavaSetFactory(this);
//		this.creatorcreatorFactory =  new JavaCreatorCreatorFactory();
//		BasicTemplate
//	}
	
	public SimpleKeyValueList<String, ParserCondition> getTemplates() {
		if(customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
			FeatureCondition featureCondition = new FeatureCondition();
			customTemplate.add(featureCondition.getKey(), featureCondition);
			ImportCondition importCondition = new ImportCondition();
			customTemplate.add(importCondition.getKey(), importCondition);
			ForeachCondition foreach = new ForeachCondition();
			customTemplate.add(foreach.getKey(), foreach);
			TemplateFragmentCondition templateCondition = new TemplateFragmentCondition();
			customTemplate.add(templateCondition.getKey(), templateCondition);
		}
		return customTemplate;
	}
	
	@Override
	public SendableEntityCreator generate(GraphMember item) {
		if(item instanceof GraphModel == false) {
			return null;
		}
		return generate("src", (GraphModel)item);
	}
	@Override
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
