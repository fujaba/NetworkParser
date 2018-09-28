package de.uniks.networkparser.ext;

import java.io.File;
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
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.graph.SourceCode;
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
import de.uniks.networkparser.parser.DebugCondition;
import de.uniks.networkparser.parser.JavaListCondition;
import de.uniks.networkparser.parser.JavaMethodBodyCondition;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;
import de.uniks.networkparser.parser.cpp.CppClazz;
import de.uniks.networkparser.parser.java.JavaClazz;
import de.uniks.networkparser.parser.java.JavaSetCreator;
import de.uniks.networkparser.parser.java.JavaSetCreatorCreator;
import de.uniks.networkparser.parser.typescript.TypescriptClazz;

public class ModelGenerator extends Template {
	private FeatureSet features = Feature.getAll();
	private GraphModel defaultModel;
	public SimpleKeyValueList<String, ParserCondition> customTemplate;
	private boolean useSDMLibParser = true;
	private String defaultRootDir;
	private SimpleKeyValueList<String, Template> templates;
	private String lastGenRoot;

	public SimpleList<Template> getTemplates(String filter) {
		if (templates == null) {
			templates = new SimpleKeyValueList<String, Template>();
//			addTemplate(new JavaCreatorCreator(), true);
			addTemplate(new JavaSetCreatorCreator(), true);
			addTemplate(new JavaClazz(), true);
			addTemplate(new JavaSetCreator(), true);
//			addTemplate(new JavaCreator(), true);
			addTemplate(new TypescriptClazz(), true);
			addTemplate(new CppClazz(), true);
		}
		return getTemplates(filter, children);
	}
	
	private SimpleList<Template> getTemplates(String filter, SimpleList<Template> owner) {
		if(filter == null) {
			return this.getChildren();
		}
		if(filter.equals(".") || filter.isEmpty()) {
			return owner;
		}
		SimpleList<Template> result = new SimpleList<Template>();
		int pos = filter.indexOf(".");
		if(pos<0) {
			//java
			SimpleList<Template> possible = new SimpleList<Template>();
			String sub = filter+".";
			for(Template child : owner ) {
				String childid=child.getId(false);
				if(filter.equals(childid)) {
					result.add(child);
				} else if(childid.startsWith(sub) && childid.indexOf(".", sub.length()+1)<1) {
					possible.add(child);
				}
			}
			if(result.size()<1 && possible.size()>0) {
				result.addAll(possible);
			}
		} else if(pos == filter.length()-1) {
			// java.
			String id = filter.substring(0, pos-1);
			for(Template child : owner ) {
				if(id.equals(child.getId(false))) {
					result.addAll(child.getChildren());
				}
			}
		} else {
			//java.set.attribute
			String id = filter.substring(0, pos);
			for(Template child : children ) {
				String childId = child.getId(false);
				int childPos = childId.indexOf(".");
				if(childPos>0) {
					if(filter.startsWith(childId)) {
						String sub = filter.substring(childId.length());
						// Sub must be start with poitn or empty
						if(sub.isEmpty() || sub.startsWith(".")) {
							result.addAll(getTemplates(sub, child.getChildren()));
						}
					}
				}
				if(id.equals(childId)) {
					result.addAll(getTemplates(filter.substring(pos+1), child.getChildren()));
				}
			}
		}
		return result;
	}

	public boolean addTemplate(Template template, boolean addOwner) {
		if(super.addTemplate(template, addOwner) == false) {
			return false;
		}
		String id2 = template.getId(true);
		if(id2 == null) {
			return false;
		}
		templates.add(id2, template);
		SimpleList<Template> children = template.getChildren();
		if(children != null) {
			for(Template child : children) {
				addTemplate(child, false);
			}
		}
		return true;
	}

	public SimpleKeyValueList<String, ParserCondition> getCondition() {
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

	private String getJavaPath() {
		if(new File("src/main/java").exists()) {
			return "src/main/java";
		}
		return "src";
	}
	
	public SendableEntityCreator generate(String rootDir, GraphMember item) {
		if (item instanceof GraphModel == false) {
			return null;
		}
		return generating(rootDir, (GraphModel) item, null, null, true, true);
	}

	public SendableEntityCreator generating(String rootDir, GraphModel model, TextItems parameters, String type, boolean writeFiles, boolean enableParser) {
		this.lastGenRoot = rootDir;
		// Set DefaultValue
		if(type == null) {
			type = TYPE_JAVA;
		}
		if(rootDir == null) {
			if(type == TYPE_JAVA) {
				rootDir = getJavaPath();
			} else {
				return null;
			}
		}

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
		SimpleList<Template> templates = getTemplates(type);

		for (Template template : templates) {
			template.withOwner(this);
		}

		Feature codeStyle = getFeature(Feature.CODESTYLE);
		ClazzSet clazzes = model.getClazzes();

		for (Clazz clazz : clazzes) {
			for (Template template : templates) {
				boolean isStandard = codeStyle.match(clazz);
				TemplateResultFile resultFile = template.executeClazz(clazz, resultModel, isStandard);
				if(resultFile == null) {
					continue;
				}
//				TemplateResultFragment fragment = 
				resultFile.withMetaModel(template.isMetaModel());
				template.generate(resultModel, resultFile, clazz);
				resultModel.add(resultFile);
			}
		}
		
		for (Template template : templates) {
			TemplateResultFile resultFile = template.executeEntity(model, resultModel, true);
			if(resultFile == null) {
				continue;
			}
			resultFile.withMetaModel(template.isMetaModel());
			template.executeTemplate(resultModel, resultFile, model);
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

	public TemplateResultModel getResultModel() {
		TemplateResultModel resultModel = new TemplateResultModel();
		resultModel.withTemplate(this.getCondition());
		resultModel.withFeatures(this.features);
		return resultModel;
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


	public boolean write(String rootPath, TemplateResultFile entity) {
		if(rootPath.endsWith("/") == false) {
			rootPath += "/";
		}
		String newContent = entity.toString();
		CharacterBuffer oldContent = null;
		if(entity.isMetaModell() == false) {
			oldContent =FileBuffer.readFile(rootPath + entity.getFileName());
		} else {
			SourceCode code = entity.getCode();
			if(newContent == null) {
				return true;
			}
			if(code != null) {
				oldContent = code.getContent();
			}
		}
		if(oldContent != null && newContent.equals(oldContent.toString())) {
			return true;
		}
		return FileBuffer.writeFile(rootPath + entity.getFileName(), newContent)>=0;
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
	public Feature getFeature(Feature value, Clazz... clazzes) {
		if (this.features != null) {
			for (Iterator<Feature> i = this.features.iterator(); i.hasNext();) {
				Feature item = i.next();
				if (item.equals(value)) {
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

	public ModelGenerator withoutFeature(Feature feature) {
		this.features.without(feature);
		return this;
	}

	public ModelGenerator withFeature(FeatureSet featureSet) {
		this.features.clear();
		this.features.addAll(featureSet);
		return this;
	}
	public ModelGenerator withFeature(Feature feature) {
		this.features.with(feature);
		return this;
	}

	public ModelGenerator withDefaultModel(GraphModel model) {
		this.defaultModel = model;
		return this;
	}

	/**
	 * @param param	List of Params 
	 * 	First is Type like TYPE_JAVA
	 * 	Second rootDir 
	 */
	
	public void testGeneratedCode(String... param) {
		String type=null;
		String rootDir = null;
		if (this.defaultModel != null) {
			if(param != null) {
				if(param.length>0) {
					if(param[0] instanceof String) {
						if(TYPE_JAVA.equalsIgnoreCase(param[0]) ||
							TYPE_TYPESCRIPT.equalsIgnoreCase(param[0]) ||
							TYPE_CPP.equalsIgnoreCase(param[0])) {
							type = param[0];
						} else if(param.length<2) {
							rootDir = param[0];
						}
					}
				}
				if(param.length>1) {
					if(param[1] instanceof String) {
						rootDir = param[1]; 
					}
				}
			}
			if(type == null) {
				type = TYPE_JAVA;
			}
			if(rootDir == null) {
				if(TYPE_JAVA.equalsIgnoreCase(type)) {
					rootDir = "build/gen/java";
				} else if(TYPE_TYPESCRIPT.equalsIgnoreCase(type)) {
					rootDir = "build/gen/js";
				} else if(TYPE_CPP.equalsIgnoreCase(type)) {
					rootDir = "build/gen/cpp";
				}
			}
			if(rootDir != null) {
				removeAllGeneratedCode(defaultModel, rootDir, type );
				generating(rootDir, this.defaultModel, null, type, true, true);
			}
		}
	}

	public String getLastGenRoot() {
		return lastGenRoot;
	}
	
	public void removeAllGeneratedCode(GraphModel model, String rootDir, String type) {
		// now remove class file, creator file, and modelset file for each class
		// and the CreatorCreator
		Feature codeStyle = getFeature(Feature.CODESTYLE);
		if(rootDir.endsWith("/") == false) {
			rootDir = rootDir+"/";
		}

		SimpleList<Template> templates = getTemplates(type);
		for (Clazz clazz : model.getClazzes()) {
			boolean isStandard = codeStyle.match(clazz);
			for(Template generator : templates) {
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
		model.withTemplate(this.getCondition());
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
		generating(defaultRootDir, this.defaultModel, null, null, true, true);
	}
}
