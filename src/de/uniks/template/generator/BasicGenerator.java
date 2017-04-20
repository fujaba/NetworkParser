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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.template.Template;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.TemplateResultFragment;

public abstract class BasicGenerator {
	protected String extension;
	protected BasicGenerator owner;

	protected SimpleList<Template> templates=new SimpleList<Template>();
	protected SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>> children=new SimpleKeyValueList<Class<?>, SimpleList<BasicGenerator>>();
	
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
	
	public SendableEntityCreator generate(GraphMember item) {
		return generate(item, null);
	}
	
	public abstract SendableEntityCreator generate(GraphMember item, TextItems parameters);

	protected FeatureProperty getFeature(Feature value, Clazz... values) {
		if(this.owner != null) {
			return this.owner.getFeature(value, values); 
		}
		return null;
	}
	
	public void executeTemplate(SendableEntityCreator templateResult, LocalisationInterface parameters, GraphMember member) {
		for(Template template : templates) {
			TemplateResultFragment fragment = template.generate(parameters, templateResult, member);
			if(template.getType()==Template.DECLARATION) {
				parameters.put(template.getName(), fragment.getResult().toString());
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
		TemplateResultFile templateResult = owner.getNewResult(clazz);
		if(parameters instanceof SendableEntityCreator) {
			templateResult.setParent((SendableEntityCreator)parameters);
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
