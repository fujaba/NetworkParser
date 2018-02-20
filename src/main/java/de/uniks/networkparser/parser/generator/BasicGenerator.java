package de.uniks.networkparser.parser.generator;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;

public abstract class BasicGenerator {
	public static final String PROPERTY_FEATURE="features";
	public static final String TYPE_JAVA="java";
	public static final String TYPE_TYPESCRIPT="typescript";
	public static final String TYPE_CPP="cpp";
	protected String extension;
	protected String path;
	protected String postfix;
	protected BasicGenerator owner;

	protected SimpleList<Template> templates=new SimpleList<Template>();
	protected SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>> children=new SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>>();
	protected boolean metaModel;

	public BasicGenerator withMetaModell(boolean value) {
		this.metaModel = value;
		return this;
	}

	public boolean isMetaModel() {
		return metaModel;
	}

	public abstract Class<?> getTyp();

	public boolean addGenerator(BasicGenerator generator) {
		if(generator == null) {
			return false;
		}
		Class<?> typ = generator.getTyp();
		SimpleList<BasicGenerator> list = this.children.get(typ);
		if(list == null) {
			list = new SimpleList<BasicGenerator>();
			this.children.put(typ, list);
		}
		list.add(generator);
		return true;
	}

	public BasicGenerator withOwner(BasicGenerator owner) {
		this.owner = owner;
		return this;
	}

	public Template createTemplate(String name, int type, String... templates) {
		Template template = new Template(name).withType(type);
		template.withTemplate(templates);
		this.templates.add(template);
		return template;
	}

	protected FeatureProperty getFeature(Feature value, Clazz... values) {
		if(this.owner != null) {
			return this.owner.getFeature(value, values);
		}
		return null;
	}

	public void executeTemplate(TemplateResultFile templateResult, LocalisationInterface parameters, GraphMember member) {
		if(member == null || member.getClass() == getTyp() == false) {
			return;
		}
		for(Template template : templates) {
			if(template == null) {
				continue;
			}
			TemplateResultFragment fragment = template.generate(parameters, templateResult, member);
			if(template.getType()==Template.DECLARATION && fragment != null) {
				parameters.put(template.getName(), fragment.getResult().toString());
			}
		}
	}



	protected FeatureSet getFeatures(LocalisationInterface value) {
		if(value instanceof TemplateResultModel) {
			TemplateResultModel model = (TemplateResultModel) value;
			Object features = model.getValue(model, PROPERTY_FEATURE);
			if(features != null) {
				return (FeatureSet) features;
			}
			return null;
		}
		return null;

	}

	public TemplateResultFile createResultFile(Clazz clazz, boolean isStandard) {
		TemplateResultFile templateResult = new TemplateResultFile(clazz, isStandard);
		templateResult.withExtension(this.extension);
		templateResult.withPath(this.path);
		templateResult.withPostfix(this.postfix);
		return templateResult;
	}

	public TemplateResultFile executeClazz(Clazz clazz, LocalisationInterface parameters, boolean isStandard) {
		TemplateResultFile templateResult = createResultFile(clazz, isStandard);
		if(parameters instanceof SendableEntityCreator) {
			templateResult.setParent((SendableEntityCreator)parameters);
		}

		SimpleList<BasicGenerator> templateList;
		AttributeSet attributes = clazz.getAttributes();
		templateList = children.get(attributes.getTypClass());
		if(templateList != null) {
			for (Attribute attribute : attributes) {
				for(BasicGenerator template : templateList) {
					template.executeTemplate(templateResult, parameters, attribute);
				}
			}
		}

		AssociationSet associations = clazz.getAssociations();
		templateList = children.get(associations.getTypClass());
		if(templateList != null) {
			for (Association assoc : associations) {
				for(BasicGenerator template : templateList) {
					template.executeTemplate(templateResult, parameters, assoc);
					if (assoc.getClazz().equals(assoc.getOtherClazz())) {
						template.executeTemplate(templateResult, parameters, assoc.getOther());
					}
				}
			}
		}

		MethodSet methods = clazz.getMethods();
		templateList = children.get(methods.getTypClass());
		if(templateList != null) {
			for (Method method : methods) {
				for(BasicGenerator template : templateList) {
					template.executeTemplate(templateResult, parameters, method);
				}
			}
		}
		return templateResult;
	}
}
