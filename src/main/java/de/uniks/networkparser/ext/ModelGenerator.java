package de.uniks.networkparser.ext;

import java.util.Iterator;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ForeachCondition;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;
import de.uniks.networkparser.parser.generator.BasicGenerator;
import de.uniks.networkparser.parser.generator.java.JavaClazz;
import de.uniks.networkparser.parser.generator.java.JavaCreator;
import de.uniks.networkparser.parser.generator.java.JavaSet;
import de.uniks.networkparser.parser.generator.logic.DebugCondition;
import de.uniks.networkparser.parser.generator.logic.JavaListCondition;
import de.uniks.networkparser.parser.generator.logic.JavaMethodBodyCondition;

public class ModelGenerator extends BasicGenerator {
	private FeatureSet features = Feature.getAll();
	private GraphModel defaultModel;
	public SimpleKeyValueList<String, ParserCondition> customTemplate;

	private SimpleList<BasicGenerator> javaGeneratorTemplates = new SimpleList<BasicGenerator>().with(new JavaClazz(),
			new JavaSet(), new JavaCreator());

	public SimpleKeyValueList<String, ParserCondition> getTemplates() {
		if (customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
			addParserCondition(new FeatureCondition());
			addParserCondition(new ImportCondition());
			addParserCondition(new ForeachCondition());
			addParserCondition(new TemplateFragmentCondition());
			addParserCondition(new IfCondition());
			addParserCondition(new IfCondition().withKey(IfCondition.IFNOT));
			addParserCondition(new JavaMethodBodyCondition());
			addParserCondition(new JavaListCondition());
			addParserCondition(new And());
			addParserCondition(new Or());
			addParserCondition(new DebugCondition());
			addParserCondition(new Not());
		}
		return customTemplate;
	}

	protected void addParserCondition(ParserCondition condition) {
		String key = condition.getKey();
		if(key != null) {
			customTemplate.add(key.toLowerCase(), condition);
		}
	}

	public SendableEntityCreator generate(GraphMember item) {
		if (item instanceof GraphModel == false) {
			return null;
		}
		return generate("src", (GraphModel) item);
	}

	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if (item instanceof GraphModel == false) {
			return null;
		}
		return generateJava("src", (GraphModel) item, parameters);
	}

	public SendableEntityCreator generate(String rootDir, GraphModel model) {
		return generateJava(rootDir, model, null);
	}

	public SendableEntityCreator generateJava(String rootDir, GraphModel model, TextItems parameters) {
		return generating(rootDir, model, parameters, javaGeneratorTemplates, true);
	}

	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model) {
		return generateTypescript(rootDir, model, null);
	}

	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model, TextItems parameters) {
		SimpleList<BasicGenerator> templates = new SimpleList<BasicGenerator>();

		// templates.add(new JavaClazz());
		return generating(rootDir, model, parameters, templates, true);
	}

	public SendableEntityCreator generating(String rootDir, GraphModel model, TextItems parameters,
			SimpleList<BasicGenerator> templates, boolean writeFiles) {
		if (rootDir == null) {
			rootDir = "";
		} else if (rootDir.endsWith("/") == false) {
			rootDir = rootDir + "/";
		}
		String name = model.getName();
		if (name == null) {
			name = "i.love.sdmlib";
		}
		rootDir += name.replaceAll("\\.", "/") + "/";

		TemplateResultModel result = new TemplateResultModel();
		result.withTemplate(this.getTemplates());
		result.withFeatures(this.features);
		if (parameters == null) {
			parameters = new TextItems();
			parameters.withDefaultLabel(false);
		}
		result.withLanguage(parameters);

		for (BasicGenerator template : templates) {
			template.withOwner(this);
		}
		FeatureProperty codeStyle = getFeature(Feature.CODESTYLE);
		 ClazzSet clazzes = model.getClazzes();
		for (Clazz clazz : clazzes) {
			for (BasicGenerator template : templates) {
				boolean isStandard = codeStyle.match(clazz);
				TemplateResultFile resultFile = template.executeClazz(clazz, result, isStandard);

				template.executeTemplate(resultFile, result, clazz);
				result.add(resultFile);
			}
		}
		if (writeFiles) {
			ModelWriter writer = new ModelWriter();
			writer.writeModel(rootDir, result);
		}
		return result;
	}

	@Override
	public FeatureProperty getFeature(Feature value, Clazz... clazzes) {
		if (this.features != null) {
			for (Iterator<FeatureProperty> i = this.features.iterator(); i.hasNext();) {
				FeatureProperty item = i.next();
				if (item.getName().equals(value)) {
					if(clazzes == null) {
						return item;
					}
					
					if(item.match(clazzes)) {
						return item;
					}
				}
			}
		}
		return null;
	}

	@Override
	public Class<?> getTyp() {
		return GraphModel.class;
	}

	public ModelGenerator withoutFeature(Feature feature) {
		this.features.without(feature);
		return this;
	}

	public ModelGenerator withDefaultModel(GraphModel model) {
		this.defaultModel = model;
		return this;
	}

	public void testGeneratedCode() {
		if (this.defaultModel != null) {
			String rootDir = "build/gen/java";
			removeAllGeneratedCode(defaultModel, rootDir);
			generateJava(rootDir, this.defaultModel, null);
		}
	}

	public void removeAllGeneratedCode(GraphModel model, String rootDir) {
		// now remove class file, creator file, and modelset file for each class
		// and the CreatorCreator
		FeatureProperty codeStyle = getFeature(Feature.CODESTYLE);
		if(rootDir.endsWith("/") == false) {
			rootDir = rootDir+"/";
		}
		
		for (Clazz clazz : model.getClazzes()) {
			boolean isStandard = codeStyle.match(clazz);
			for(BasicGenerator generator : javaGeneratorTemplates) {
				TemplateResultFile templateResult = generator.createResultFile(clazz, isStandard);
				FileBuffer.deleteFile(rootDir+templateResult.getFileName());
			}
		}

		String path = rootDir + (model.getName() + "/util").replaceAll("\\.", "/") + "/";

		String fileName = path + "CreatorCreator.java";

		FileBuffer.deleteFile(fileName);
	}

	public TemplateResultFragment parseTemplate(String templateString, GraphMember member) {
		Template template = new Template().withTemplate(templateString);
		return parseTemplate(template, member);
	}

	public TemplateResultFragment parseTemplate(Template template, GraphMember member) {
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(this.getTemplates());
		model.withFeatures(this.features);
		TextItems parameters = new TextItems();
		parameters.withDefaultLabel(false);
		model.withLanguage(parameters);

		TemplateResultFragment generate = template.generate(model, parameters, member);
		return generate;
	}
}
