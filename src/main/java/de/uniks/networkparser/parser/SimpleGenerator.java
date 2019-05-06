package de.uniks.networkparser.parser;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ForeachCondition;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.TemplateFragmentCondition;

public class SimpleGenerator extends Template {
	public SimpleKeyValueList<String, ParserCondition> customTemplate;

	protected void addParserCondition(ParserCondition condition) {
		String key = condition.getKey();
		if (key != null) {
			customTemplate.add(key.toLowerCase(), condition);
		}
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

	public TemplateResultModel generate(TemplateItem model) {
		return generate(model, new TextItems(), null);
	}
	public TemplateResultModel generate(TemplateItem model, LocalisationInterface parameters, SendableEntityCreator creator) {
		TemplateResultModel resultModel = new TemplateResultModel();
		resultModel.withLanguage(parameters);
		resultModel.withTemplate(this.getCondition());
		
		TemplateResultFile executeEntity = executeEntity(model, parameters, true);
		resultModel.add(executeEntity);
		executeEntity.add(generate(resultModel, creator, model));
		return resultModel;
	}
}
