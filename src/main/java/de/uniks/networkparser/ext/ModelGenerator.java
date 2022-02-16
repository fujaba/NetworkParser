package de.uniks.networkparser.ext;

import java.io.File;
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
import java.util.Iterator;

import de.uniks.networkparser.NetworkParserLog;
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
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SimpleGenerator;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;
import de.uniks.networkparser.parser.cpp.CppClazz;
import de.uniks.networkparser.parser.java.JavaClazz;
import de.uniks.networkparser.parser.java.JavaCreatorCreator;
import de.uniks.networkparser.parser.java.JavaSetCreator;
import de.uniks.networkparser.parser.typescript.TypescriptClazz;

/**
 * Model Generator.
 *
 * @author Stefan Lindel
 */
public class ModelGenerator extends SimpleGenerator {
	private FeatureSet features = Feature.createAll();
	private GraphModel defaultModel;
	private boolean useSDMLibParser = true;
	private String defaultRootDir;
	private SimpleKeyValueList<String, Template> templates;
	private String lastGenRoot;
	private NetworkParserLog logger;

	/**
	 * Gets the templates.
	 *
	 * @param filter the filter
	 * @return the templates
	 */
	public SimpleList<Template> getTemplates(String filter) {
		if (templates == null) {
			templates = new SimpleKeyValueList<String, Template>();
			addTemplate(new JavaCreatorCreator("Set"), true);
			addTemplate(new JavaClazz(), true);
			addTemplate(new JavaSetCreator(), true);
			addTemplate(new TypescriptClazz(), true);
			addTemplate(new CppClazz(), true);
		}
		return getTemplates(filter, children);
	}

	private SimpleList<Template> getTemplates(String filter, SimpleList<Template> owner) {
		if (filter == null) {
			return this.getChildren();
		}
		if (filter.equals(".") || filter.isEmpty()) {
			return owner;
		}
		SimpleList<Template> result = new SimpleList<Template>();
		int pos = filter.indexOf(".");
		if (pos < 0) {
			/* java */
			SimpleList<Template> possible = new SimpleList<Template>();
			String sub = filter + ".";
			for (Template child : owner) {
				String childid = child.getId(false);
				if (filter.equals(childid)) {
					result.add(child);
				} else if (childid != null && childid.startsWith(sub) && childid.indexOf(".", sub.length() + 1) < 1) {
					possible.add(child);
				}
			}
			if (result.size() < 1 && possible.size() > 0) {
				result.addAll(possible);
			}
		} else if (pos == filter.length() - 1) {
			/* java. */
			String id = filter.substring(0, pos - 1);
			for (Template child : owner) {
				if (id.equals(child.getId(false))) {
					result.addAll(child.getChildren());
				}
			}
		} else {
			/* java.set.attribute */
			String id = filter.substring(0, pos);
			for (Template child : children) {
				String childId = child.getId(false);
				if(childId == null) {
					continue;
				}
				int childPos = childId.indexOf(".");
				if (childPos > 0) {
					if (filter.startsWith(childId)) {
						String sub = filter.substring(childId.length());
						/* Sub must be start with poitn or empty */
						if (sub.isEmpty() || sub.startsWith(".")) {
							result.addAll(getTemplates(sub, child.getChildren()));
						}
					}
				}
				if (id.equals(childId)) {
					result.addAll(getTemplates(filter.substring(pos + 1), child.getChildren()));
				}
			}
		}
		return result;
	}

	/**
	 * Adds the template.
	 *
	 * @param template the template
	 * @param addOwner the add owner
	 * @return true, if successful
	 */
	public boolean addTemplate(Template template, boolean addOwner) {
		if (!super.addTemplate(template, addOwner)) {
			return false;
		}
		String id2 = template.getId(true);
		if (id2 == null) {
			return false;
		}
		templates.add(id2, template);
		SimpleList<Template> children = template.getChildren();
		if (children != null) {
			for (Template child : children) {
				addTemplate(child, false);
			}
		}
		return true;
	}

	private String getJavaPath() {
		if (new File("src/main/java").exists()) {
			return "src/main/java";
		}
		return "src";
	}

	/**
	 * Generate.
	 *
	 * @param rootDir the root dir
	 * @param item the item
	 * @return the sendable entity creator
	 */
	public SendableEntityCreator generate(String rootDir, GraphMember item) {
		if (!(item instanceof GraphModel)) {
			return null;
		}
		return generating(rootDir, (GraphModel) item, null, null, true, true);
	}

	/**
	 * Generating.
	 *
	 * @param rootDir the root dir
	 * @param model the model
	 * @param parameters the parameters
	 * @param type the type
	 * @param writeFiles the write files
	 * @param enableParser the enable parser
	 * @return the sendable entity creator
	 */
	public SendableEntityCreator generating(String rootDir, GraphModel model, TextItems parameters, String type,
			boolean writeFiles, boolean enableParser) {
		if(model == null) {
			return null;
		}
		this.lastGenRoot = rootDir;
		/* Set DefaultValue */
		if (type == null) {
			type = TYPE_JAVA;
		}
		if (rootDir == null) {
			if (type == TYPE_JAVA) {
				rootDir = getJavaPath();
			} else {
				return null;
			}
		}

		if (!model.fixClassModel()) {
		    if(logger != null) {
		        logger.info("Reparing of Model failed");
		    }
			return null;
		}
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
			if (clazz == null || GraphUtil.isExternal(clazz)) {
				continue;
			}
			for (Template template : templates) {
				boolean isStandard = codeStyle.match(clazz);
				TemplateResultFile resultFile = template.executeClazz(clazz, resultModel, isStandard);
				if (resultFile == null) {
					continue;
				}
				resultFile.withMetaModel(template.isMetaModel());
				template.generate(resultModel, resultFile, clazz);
				resultModel.add(resultFile);
			}
		}

		for (Template template : templates) {
			TemplateResultFile resultFile = template.executeEntity(model, resultModel, true);
			if (resultFile == null) {
				continue;
			}
			resultFile.withMetaModel(template.isMetaModel());
			template.executeTemplate(resultModel, resultFile, model);
			resultModel.add(resultFile);
		}

		if (writeFiles) {
			/*
			 * IF FILE EXIST AND Switch is Enable only add missing value Add missed value to
			 * Metamodel
			 */
			if (useSDMLibParser && enableParser) {
				for (TemplateResultFile file : resultModel) {
					if (!file.isMetaModell()) {
						continue;
					}
					ParserEntity parser = parse(rootDir, file);
					if (parser != null) {
						parser.addMemberToModel(false);
					}
				}
			}
			for (TemplateResultFile file : resultModel) {
				write(rootDir, file);
			}
		}
		return resultModel;
	}

	/**
	 * Gets the result model.
	 *
	 * @return the result model
	 */
	public TemplateResultModel getResultModel() {
		TemplateResultModel resultModel = new TemplateResultModel();
		resultModel.withTemplate(this.getCondition());
		resultModel.withFeatures(this.features);
		return resultModel;
	}

	private String getFileName(String path, String file) {
		if (path == null) {
			path = "";
		} else if (!path.endsWith("/")) {
			path = path + "/";
		}
		if(file != null) {
			path += file.replaceAll("\\.", "/") + "/";
		}
		return path;
	}

	/**
	 * Write.
	 *
	 * @param rootPath the root path
	 * @param entity the entity
	 * @return true, if successful
	 */
	public boolean write(String rootPath, TemplateResultFile entity) {
		if(rootPath == null || entity == null) {
			return false;
		}
		if (!rootPath.endsWith("/")) {
			rootPath += "/";
		}
		String newContent = entity.toString();
		CharacterBuffer oldContent = null;
		if (!entity.isMetaModell()) {
			oldContent = FileBuffer.readFile(rootPath + entity.getFileName());
		} else {
			SourceCode code = entity.getCode();
			if (newContent == null) {
				return true;
			}
			if (code != null) {
				oldContent = code.getContent();
			}
		}
		if (oldContent != null && newContent.equals(oldContent.toString())) {
			return true;
		}
		return FileBuffer.writeFile(rootPath + entity.getFileName(), newContent) >= 0;
	}

	/**
	 * Parses the.
	 *
	 * @param rootPath the root path
	 * @param entity the entity
	 * @return the parser entity
	 */
	public ParserEntity parse(String rootPath, TemplateResultFile entity) {
		/* check for each clazz, if a matching file already exists */
		if (entity == null || !(entity.getMember() instanceof Clazz)) {
			return null;
		}
		String fileName = entity.getFileName();
		if (!rootPath.endsWith("/")) {
			rootPath += "/";
		}
		CharacterBuffer content = FileBuffer.readFile(rootPath + fileName);
		/* check existing file for possible changes */
		if (content != null) {
			ParserEntity parser = new ParserEntity();
			try {
				parser.withFile(fileName, (Clazz) entity.getMember());
				parser.parse(content);
				return parser;
			} catch (Exception e) {
				if (logger != null) {
					logger.error(this, "parse", "Cant parse File:" + fileName, e);
				}
			}
		}
		return null;
	}

	/**
	 * Checks if is SDM lib parser.
	 *
	 * @return true, if is SDM lib parser
	 */
	public boolean isSDMLibParser() {
		return useSDMLibParser;
	}

	/**
	 * With enable SDM lib parser.
	 *
	 * @param value the value
	 */
	public void withEnableSDMLibParser(boolean value) {
		this.useSDMLibParser = value;
	}

	/**
	 * Gets the feature.
	 *
	 * @param value the value
	 * @param clazzes the clazzes
	 * @return the feature
	 */
	@Override
	public Feature getFeature(Feature value, Clazz... clazzes) {
		if (this.features != null) {
			for (Iterator<Feature> i = this.features.iterator(); i.hasNext();) {
				Feature item = i.next();
				if (item.equals(value)) {
					if (clazzes == null) {
						return item;
					}

					if (item.match(clazzes)) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Without feature.
	 *
	 * @param feature the feature
	 * @return the model generator
	 */
	public ModelGenerator withoutFeature(Feature feature) {
		this.features.without(feature);
		return this;
	}

	/**
	 * With feature.
	 *
	 * @param featureSet the feature set
	 * @return the model generator
	 */
	public ModelGenerator withFeature(FeatureSet featureSet) {
		this.features.clear();
		this.features.addAll(featureSet);
		return this;
	}

	/**
	 * With feature.
	 *
	 * @param feature the feature
	 * @return the model generator
	 */
	public ModelGenerator withFeature(Feature... feature) {
		this.features.with(feature);
		return this;
	}

	/**
	 * With default model.
	 *
	 * @param model the model
	 * @return the model generator
	 */
	public ModelGenerator withDefaultModel(GraphModel model) {
		this.defaultModel = model;
		return this;
	}

	/**
	 * Removes the and generate.
	 *
	 * @param param List of Params First is Type like TYPE_JAVA Second rootDir
	 */

	public void removeAndGenerate(String... param) {
		String type = null;
		String rootDir = null;
		if (this.defaultModel != null) {
			if (param != null) {
				if (param.length > 0) {
					if (TYPE_JAVA.equalsIgnoreCase(param[0]) || TYPE_TYPESCRIPT.equalsIgnoreCase(param[0])
							|| TYPE_CPP.equalsIgnoreCase(param[0])) {
						type = param[0];
					} else if (param.length < 2) {
						rootDir = param[0];
					}
				}
				if (param.length > 1) {
					if (param[1] instanceof String) {
						rootDir = param[1];
					}
				}
			}
			if (type == null) {
				type = TYPE_JAVA;
			}
			if (rootDir == null) {
				if (TYPE_JAVA.equalsIgnoreCase(type)) {
					rootDir = "build/gen/java";
				} else if (TYPE_TYPESCRIPT.equalsIgnoreCase(type)) {
					rootDir = "build/gen/js";
				} else if (TYPE_CPP.equalsIgnoreCase(type)) {
					rootDir = "build/gen/cpp";
				}
			}
			if (rootDir != null) {
				remove(defaultModel, rootDir, type);
				generating(rootDir, this.defaultModel, null, type, true, true);
			}
		}
	}

	/**
	 * Gets the last gen root.
	 *
	 * @return the last gen root
	 */
	public String getLastGenRoot() {
		return lastGenRoot;
	}

	/**
	 * Removes the.
	 *
	 * @param model the model
	 * @param rootDir the root dir
	 * @param type the type
	 * @return true, if successful
	 */
	public boolean remove(GraphModel model, String rootDir, String type) {
		if(rootDir == null || model == null) {
			return false;
		}
		/*
		 * now remove class file, creator file, and modelset file for each class and the
		 * CreatorCreator
		 */
		Feature codeStyle = getFeature(Feature.CODESTYLE);
		if (!rootDir.endsWith("/")) {
			rootDir = rootDir + "/";
		}

		SimpleList<Template> templates = getTemplates(type);
		for (Clazz clazz : model.getClazzes()) {
			boolean isStandard = codeStyle.match(clazz);
			for (Template generator : templates) {
				TemplateResultFile templateResult = generator.createResultFile(clazz, isStandard);
				templateResult.withPath(model.getName().replaceAll("\\.", "/"));
				FileBuffer.deleteFile(rootDir + templateResult.getFileName());
			}
		}

		String path = rootDir + (model.getName() + "/util").replaceAll("\\.", "/") + "/";

		String fileName = path + "CreatorCreator.java";

		return FileBuffer.deleteFile(fileName);
	}

	/**
	 * Parses the template.
	 *
	 * @param templateString the template string
	 * @param member the member
	 * @return the template result fragment
	 */
	public TemplateResultFragment parseTemplate(String templateString, GraphMember member) {
		Template template = new Template().withTemplate(templateString);
		return parseTemplate(template, member);
	}

	/**
	 * Parses the template.
	 *
	 * @param template the template
	 * @param member the member
	 * @return the template result fragment
	 */
	public TemplateResultFragment parseTemplate(Template template, GraphMember member) {
		if(template == null) {
			return null;
		}
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(this.getCondition());
		model.withFeatures(this.features);
		TextItems parameters = new TextItems();
		parameters.withDefaultLabel(false);
		model.withLanguage(parameters);

		TemplateResultFragment generate = template.generate(model, parameters, member);
		return generate;
	}

	/**
	 * Parses the source code.
	 *
	 * @param content the content
	 * @return the clazz
	 */
	public Clazz parseSourceCode(CharacterBuffer content) {
		Clazz clazz = ParserEntity.create(content);
		return clazz;
	}

	/**
	 * With root dir.
	 *
	 * @param rootDir the root dir
	 * @return the model generator
	 */
	public ModelGenerator withRootDir(String rootDir) {
		this.defaultRootDir = rootDir;
		return this;
	}

	/**
	 * Gets the root dir.
	 *
	 * @return the root dir
	 */
	public String getRootDir() {
		return defaultRootDir;
	}

	/**
	 * Find clazz.
	 *
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the clazz
	 */
	public Clazz findClazz(String name, boolean defaultValue) {
		if(defaultModel == null) {
			return null;
		}
		Clazz clazz = (Clazz) this.defaultModel.getChildByName(name, Clazz.class);
		if (clazz != null) {
			return clazz;
		}

		if (this.defaultRootDir == null) {
			if (defaultValue) {
				return new Clazz("");
			}
			return null;
		}
		String fileName = getFileName(this.defaultRootDir, name);
		if(this.fileType != null && fileName.endsWith("/")) {
			fileName = fileName.substring(0, fileName.length()-1)+"."+this.fileType;
		}
		CharacterBuffer buffer = FileBuffer.readFile(fileName);
		clazz = parseSourceCode(buffer);
		if (clazz != null) {
			return clazz;
		}
		if (defaultValue) {
			return new Clazz("");
		}
		return null;
	}

	/**
	 * Find attribute.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the attribute
	 */
	public Attribute findAttribute(Clazz clazz, String name, boolean defaultValue) {
		if (name == null || clazz == null) {
			if (defaultValue) {
				return new Attribute("", DataType.VOID);
			}
			return null;
		}
		AttributeSet attributes = clazz.getAttributes();
		for (Attribute a : attributes) {
			if (name.equals(a.getName())) {
				return a;
			}
		}
		/* Update from Code and find the Clazz from Model */
		if (defaultValue) {
			return new Attribute("", DataType.VOID);
		}
		return null;
	}

	/**
	 * Find association.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the association
	 */
	public Association findAssociation(Clazz clazz, String name, boolean defaultValue) {
		if (name == null || clazz == null) {
			if (defaultValue) {
				return new Association(null);
			}
			return null;
		}
		AssociationSet assocs = clazz.getAssociations();
		for (Association a : assocs) {
			if (name.equals(a.getName())) {
				return a;
			}
		}
		/* Update from Code and find the Clazz from Model */
		if (defaultValue) {
			return new Association(null);
		}
		return null;
	}

	/**
	 * Find method.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the method
	 */
	public Method findMethod(Clazz clazz, String name, boolean defaultValue) {
		if (name == null) {
			return null;
		}
		MethodSet methods = clazz.getMethods();
		for (Method m : methods) {
			if (name.equals(m.getName())) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Find parameter.
	 *
	 * @param method the method
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the parameter
	 */
	public Parameter findParameter(Method method, String name, boolean defaultValue) {
		if (name == null) {
			return null;
		}
		ParameterSet parameters = method.getParameters();
		for (Parameter p : parameters) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Creates the clazz.
	 *
	 * @param name the name
	 * @return the clazz
	 */
	public Clazz createClazz(String name) {
		if(defaultModel != null) {
			return this.defaultModel.createClazz(name);
		}
		return null;
	}

	/**
	 * Removes the clazz.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	public boolean removeClazz(Clazz clazz) {
		if(defaultModel != null) {
			return this.defaultModel.remove(clazz);
		}
		return false;
	}

	/**
	 * Apply change.
	 */
	public void applyChange() {
		if(defaultModel != null) {
			generating(defaultRootDir, this.defaultModel, null, null, true, true);
		}
	}

	/**
	 * With logger.
	 *
	 * @param logger the logger
	 * @return the model generator
	 */
	public ModelGenerator withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}
}
