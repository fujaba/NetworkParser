package de.uniks.networkparser.parser;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * Class for Simple Codegenertation
 * @author Stefan Lindel
 */
public class SimpleGenerator extends Template {
	public SimpleKeyValueList<String, ParserCondition> customTemplate;

	protected boolean addParserCondition(ParserCondition condition) {
		if (condition == null || customTemplate == null) {
			return false;
		}
		String key = condition.getKey();
		if (key != null) {
			return customTemplate.add(key.toLowerCase(), condition);
		}
		return false;
	}

	public SimpleKeyValueList<String, ParserCondition> getCondition() {

		if (customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
			SimpleList<ParserCondition> defaultTemplates = Template.getTemplateCondition();
			for (ParserCondition condition : defaultTemplates) {
				addParserCondition(condition);
			}
		}
		return customTemplate;
	}

	public TemplateResultModel generate(TemplateItem model) {
		return generate(model, new TextItems(), null);
	}

	public TemplateResultModel generate(TemplateItem model, LocalisationInterface parameters,
			SendableEntityCreator creator) {
		TemplateResultModel resultModel = new TemplateResultModel();
		resultModel.withLanguage(parameters);
		resultModel.withTemplate(this.getCondition());

		TemplateResultFile executeEntity = executeEntity(model, parameters, true);
		if (executeEntity != null) {
			resultModel.add(executeEntity);
			executeEntity.add(generate(resultModel, creator, model));
		}
		return resultModel;
	}
}
