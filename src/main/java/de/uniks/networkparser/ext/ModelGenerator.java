package de.uniks.networkparser.ext;

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
import java.util.Iterator;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.graph.util.ParameterSet;
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
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;
import de.uniks.networkparser.parser.generator.BasicGenerator;
import de.uniks.networkparser.parser.generator.cpp.CppClazz;
import de.uniks.networkparser.parser.generator.java.JavaClazz;
import de.uniks.networkparser.parser.generator.java.JavaCreator;
import de.uniks.networkparser.parser.generator.java.JavaCreatorCreator;
import de.uniks.networkparser.parser.generator.java.JavaSet;
import de.uniks.networkparser.parser.generator.logic.DebugCondition;
import de.uniks.networkparser.parser.generator.logic.JavaListCondition;
import de.uniks.networkparser.parser.generator.logic.JavaMethodBodyCondition;
import de.uniks.networkparser.parser.generator.typescript.TypescriptClazz;

public class ModelGenerator extends BasicGenerator {
	private FeatureSet features = Feature.getAll();
	private GraphModel defaultModel;
	public SimpleKeyValueList<String, ParserCondition> customTemplate;
	private boolean useSDMLibParser = true;
	private String defaultRootDir;

	private SimpleList<BasicGenerator> javaGeneratorTemplates = new SimpleList<BasicGenerator>().with(new JavaCreatorCreator(), new JavaClazz(), new JavaSet(), new JavaCreator());
	private SimpleList<BasicGenerator> typeScriptTemplates = new SimpleList<BasicGenerator>().with(new TypescriptClazz());
	private SimpleList<BasicGenerator> cppScriptTemplates = new SimpleList<BasicGenerator>().with(new CppClazz());

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
		return generating(rootDir, model, parameters, javaGeneratorTemplates, true, true);
	}

	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model) {
		return generateTypescript(rootDir, model, null);
	}

	public SendableEntityCreator generateTypescript(String rootDir, GraphModel model, TextItems parameters) {
		return generating(rootDir, model, parameters, typeScriptTemplates, true, true);
	}

	public TemplateResultModel getResultModel() {
		TemplateResultModel resultModel = new TemplateResultModel();
		resultModel.withTemplate(this.getTemplates());
		resultModel.withFeatures(this.features);
		return resultModel;
	}

	public SendableEntityCreator generating(String rootDir, String type, GraphModel model) {
		if(TYPE_JAVA.equalsIgnoreCase(type)) {
			return generating(rootDir, model, null, javaGeneratorTemplates, rootDir != null, rootDir != null);
		}
		if(TYPE_TYPESCRIPT.equalsIgnoreCase(type)) {
			return generating(rootDir, model, null, typeScriptTemplates, true, false);
		}
		if(TYPE_CPP.equalsIgnoreCase(type)) {
			return generating(rootDir, model, null, cppScriptTemplates, true, true);
		}
		return null;
	}

	private String getFileName(String path, String file) {
		if (path == null) {
			path = "";
		} else if (path.endsWith("/") == false) {
			path = path + "/";
		}
		path += file.replaceAll("\\.", "/") + "/";
		return path;

	}

	public SendableEntityCreator generating(String rootDir, GraphModel model, TextItems parameters,
			SimpleList<BasicGenerator> templates, boolean writeFiles, boolean enableParser) {

		model.fixClassModel();
		String name = model.getName();
		if (name == null) {
			name = "i.love.sdmlib";
		}
		rootDir = getFileName(rootDir, name);

		TemplateResultModel resultModel = getResultModel();
		if (parameters == null) {
			parameters = new TextItems();
			parameters.withDefaultLabel(false);
		}
		resultModel.withLanguage(parameters);

		for (BasicGenerator template : templates) {
			template.withOwner(this);
		}
		FeatureProperty codeStyle = getFeature(Feature.CODESTYLE);
		ClazzSet clazzes = model.getClazzes();

		for (Clazz clazz : clazzes) {
			for (BasicGenerator template : templates) {
				boolean isStandard = codeStyle.match(clazz);
				TemplateResultFile resultFile = template.executeClazz(clazz, resultModel, isStandard);
				if(resultFile == null) {
					continue;
				}

				resultFile.withMetaModel(template.isMetaModel());
				template.executeTemplate(resultFile, resultModel, clazz);
				resultModel.add(resultFile);
			}
		}
		
		for (BasicGenerator template : templates) {
			TemplateResultFile resultFile = template.executeEntity(model, resultModel, true);
			if(resultFile == null) {
				continue;
			}
			resultFile.withMetaModel(template.isMetaModel());
			template.executeTemplate(resultFile, resultModel, model);
			resultModel.add(resultFile);
		}

		if (writeFiles) {
			// IF FILE EXIST AND Switch is Enable only add missing value
			// Add missed value to Metamodel
			if(useSDMLibParser && enableParser) {
				for (TemplateResultFile file : resultModel) {
					if(file.isMetaModell() == false) {
						continue;
					}
					ParserEntity parser = parse(rootDir, file);
					if(parser != null) {
						parser.addMemberToModel();
					}
				}
			}
			for (TemplateResultFile file : resultModel) {
				write(rootDir, file);
			}
		}
		return resultModel;
	}

	public boolean write(String rootPath, TemplateResultFile entity) {
		if(rootPath.endsWith("/") == false) {
			rootPath += "/";
		}
		return FileBuffer.writeFile(rootPath + entity.getFileName(), entity.toString())>=0;
	}

	public ParserEntity parse(String rootPath, TemplateResultFile entity) {
		// check for each clazz, if a matching file already exists
		if(entity == null || entity.getMember() instanceof Clazz == false) {
			return null;
		}
		String fileName = entity.getFileName();
		if(rootPath.endsWith("/") == false) {
			rootPath += "/";
		}
		CharacterBuffer content = FileBuffer.readFile(rootPath + fileName);
		// check existing file for possible changes
		if(content != null) {
			ParserEntity parser = new ParserEntity();
			try {
				parser.parse(content, (Clazz) entity.getMember(), fileName);
				return parser;
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("Cant parse File:"+fileName);
			}
		}
		return null;
	}

	public boolean isSDMLibParser() {
		return useSDMLibParser;
	}
	public void withEnableSDMLibParser(boolean value) {
		this.useSDMLibParser = value;
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
	public Class<?> getType() {
		return GraphModel.class;
	}

	public ModelGenerator withoutFeature(Feature feature) {
		this.features.without(feature);
		return this;
	}

	public ModelGenerator withFeature(FeatureProperty feature) {
		this.features.with(feature);
		return this;
	}

	public ModelGenerator withDefaultModel(GraphModel model) {
		this.defaultModel = model;
		return this;
	}

	public void testGeneratedCode(String type) {
		if (this.defaultModel != null) {
			if(type == null) {
				type = TYPE_JAVA;
			}
			String rootDir = null;
			if(TYPE_JAVA.equalsIgnoreCase(type)) {
				rootDir = "build/gen/java";
			} else if(TYPE_TYPESCRIPT.equalsIgnoreCase(type)) {
				rootDir = "build/gen/js";
			} else if(TYPE_CPP.equalsIgnoreCase(type)) {
				rootDir = "build/gen/cpp";
			}
			if(rootDir != null) {
				removeAllGeneratedCode(defaultModel, rootDir);
				generating(rootDir, type, this.defaultModel);
			}
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
				templateResult.withPath(model.getName().replaceAll("\\.", "/"));
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

	public Clazz parseSourceCode(CharacterBuffer content) {
		Clazz clazz = ParserEntity.create(content);
		return clazz;
	}


	public ModelGenerator withRootDir(String rootDir) {
		this.defaultRootDir = rootDir;
		return this;
	}

	public String getRootDir() {
		return defaultRootDir;
	}

	public Clazz findClazz(String name, boolean defaultValue) {
		Clazz clazz = (Clazz) this.defaultModel.getChildByName(name, Clazz.class);
		if(clazz != null) {
			return clazz;
		}

		if(this.defaultRootDir == null) {
			if(defaultValue) {
				return new Clazz("");
			}
			return null;
		}

		CharacterBuffer buffer = FileBuffer.readFile(getFileName(this.defaultRootDir, name));
		clazz = parseSourceCode(buffer);
		if(clazz != null) {
			return clazz;
		}
		if(defaultValue) {
			return new Clazz("");
		}
		return null;
	}

	public Attribute findAttribute(Clazz clazz, String name, boolean defaultValue) {
		if(name == null) {
			if(defaultValue) {
				return new Attribute("", DataType.VOID);
			}
			return null;
		}
		AttributeSet attributes = clazz.getAttributes();
		for(Attribute a : attributes) {
			if( name.equals(a.getName())) {
				return a;
			}
		}
		// Update from Code and find the Clazz from Model
		if(defaultValue) {
			return new Attribute("", DataType.VOID);
		}
		return null;
	}
	
	public Association findAssociation(Clazz clazz, String name, boolean defaultValue) {
		if(name == null) {
			if(defaultValue) {
				return new Association(null);
			}
			return null;
		}
		AssociationSet assocs = clazz.getAssociations();
		for(Association a : assocs) {
			if(name.equals(a.getName())) {
				return a;
			}
		}
		// Update from Code and find the Clazz from Model
		if(defaultValue) {
			return new Association(null);
		}
		return null;
	}

	public Method findMethod(Clazz clazz, String name, boolean defaultValue) {
		if(name == null) {
			return null;
		}
		MethodSet methods = clazz.getMethods();
		for(Method m : methods) {
			if( name.equals(m.getName())) {
				return m;
			}
		}
		return null;
	}
	
	public Parameter findParameter(Method method, String name, boolean defaultValue) {
		if(name == null) {
			return null;
		}
		ParameterSet parameters = method.getParameters();
		for(Parameter p : parameters) {
			if( name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	public Clazz createClazz(String name) {
		return this.defaultModel.createClazz(name);
	}
	
	public boolean removeClazz(Clazz clazz) {
		return this.defaultModel.remove(clazz);
	}


	public void applyChange() {
		this.generate(defaultRootDir, this.defaultModel);
	}
}
