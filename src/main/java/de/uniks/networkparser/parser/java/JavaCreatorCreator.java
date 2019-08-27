package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;

public class JavaCreatorCreator extends Template {
	public JavaCreatorCreator(String creatorPrefix) {
		this.extension = "java";
		this.path = "util";
		this.fileType = "classmodel";
		this.id = TYPE_JAVA + ".creatorcreator";
		this.type = TEMPLATE;
		if(creatorPrefix == null) {
			/* Creator or Set */
			creatorPrefix = "Creator";
		}
		this.withTemplate(
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}",
				"", "{{#template IMPORT}}{{#foreach {{file.headers}}}}",
					"import {{item}};{{#endfor}}{{#endtemplate}}",
				"",
				"{{#import " + IdMap.class.getName() + "}}" + "class CreatorCreator {",
				"",
				"   public static final IdMap createIdMap(String session) {",
				"        IdMap map = new IdMap().withSession(session);",
				"",
					"{{#foreach {{generatedclazz}}}}",
					"	map.withCreator(new {{item.name}}"+creatorPrefix+"());",
					"{{#endfor}}",
				"        return map;",
				"   }",
				"{{#template TEMPLATEEND}}}{{#endtemplate}}");
	}

	@Override
	public TemplateResultFile executeEntity(TemplateItem entity, LocalisationInterface parameters, boolean isStandard) {
		FeatureSet features = getFeatures(parameters);
		if (features != null) {
			if (features.match(Feature.SERIALIZATION, null) == false) {
				return null;
			}
			if (entity instanceof GraphModel) {
				if (((GraphModel) entity).getClazzes().size() < 1) {
					return null;
				}
			}
		}
		return super.executeEntity(entity, parameters, isStandard);
	}

	@Override
	public String getFileName() {
		return "CreatorCreator";
	}
}
