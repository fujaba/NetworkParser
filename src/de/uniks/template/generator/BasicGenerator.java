 package de.uniks.template.generator;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.template.Template;
import de.uniks.template.TemplateInterface;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.TemplateResultFragment;

public abstract class BasicGenerator {
	protected BasicGenerator parentFactory;
	protected String extension;

	protected SimpleList<Template> templates=new SimpleList<Template>();
	protected SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>> children=new SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>>();

	public Template createTemplate(String name, int type, String... templates) {
		Template template = new Template(name).withType(type);
		template.withTemplate(templates);
		this.templates.add(template);
		return template;
	}
	
	public abstract TemplateInterface generate(GraphMember item);
	public abstract TemplateInterface generate(GraphMember item, TextItems parameters);

	public BasicGenerator withOwner(BasicGenerator parentFactory) {
		this.parentFactory = parentFactory;
		return this;
	}
	public FeatureProperty getFeature(Feature value, Clazz... values) {
		if(this.parentFactory != null) {
			return this.parentFactory.getFeature(value, values);
		}
		return null;
	}
	
	public boolean hasFeature(Feature feature, Clazz... values) {
		FeatureProperty property = getFeature(feature);
		return hasFeatureProperty(property, values);
	}
	public boolean hasFeatureProperty(FeatureProperty property, Clazz... values) {
		if(property != null) {
			if(values == null) {
				return true;
			}
			for(int i=0;i<values.length;i++) {
				if(property.match(values[i]) == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public void executeTemplate(TemplateInterface templateResult, LocalisationInterface parameters, GraphMember member) {
		for(Template template : templates) {
			TemplateResultFragment result = template.generate(parameters, templateResult, member);
			if(result != null) {
				templateResult.add(result);
			}
		}
	}
	
// FileTemplate	
	protected TemplateResultFile executeChildren(TextItems parameters, GraphMember member) {
		Clazz clazz = GraphUtil.getParentClazz(member);
		TemplateResultFile templateResult = getNewResult(clazz);
		SimpleList<BasicGenerator> templateList = children.get(member.getClass());
		for(BasicGenerator template : templateList) {
			template.executeTemplate(templateResult, parameters, member);
		}
		return templateResult;
	}
	
	protected TemplateResultFile getNewResult(Clazz clazz) {
		FeatureProperty codeStyle = getFeature(Feature.CODESTYLE, clazz);
		boolean isStandard = Feature.CODESTYLE_STANDARD.equals(codeStyle.getStringValue()); 
		TemplateResultFile templateResult = new TemplateResultFile(clazz, isStandard);
		return templateResult;
	}
	
	protected TemplateResultFile executeClazz(Clazz clazz, LocalisationInterface parameters) {
		TemplateResultFile templateResult = getNewResult(clazz);
		if(parameters instanceof TemplateInterface) {
			templateResult.setParent((TemplateInterface)parameters);
		}
		templateResult.withExtension(this.extension);
		
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
