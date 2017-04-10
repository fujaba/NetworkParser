package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SortedList;

public class TemplateResultGroup {

	private SortedList<TemplateResultFragment> extras = new SortedList<TemplateResultFragment>(true);
	
	private CharacterBuffer result = null;
	
	public void execute(Template rootTemplate, SimpleKeyValueList<String, String> parameters, boolean isStandard, GraphMember member) {
		result = new CharacterBuffer();
		String templateResult = "";
		Template template = rootTemplate;
		TemplateResultFragment templateFragment = null;
		while (template != null) {
			templateResult = template.generate(parameters, member);
			if (templateResult != null && templateResult.length() > 0) {
				if (isStandard) {
					result.with(templateResult).with("\n");
				} else {
					templateFragment = new TemplateResultFragment()
							.withKey(template.getType())
							.withValue(templateResult + "\n");
					extras.add(templateFragment);
				}
			}
			template = template.getNextTemplate();
		}
	}
	
	public SortedList<TemplateResultFragment> getExtras() {
		return extras;
	}
	
	public void addExtra(int key, String value) {
		for (TemplateResultFragment templateFragment : extras) {
			if (templateFragment.getValue().equals(value)) {
				return;
			}
		}
		TemplateResultFragment templateFragment = new TemplateResultFragment()
				.withKey(key)
				.withValue(value);
		extras.add(templateFragment);
	}
	
	public void addExtra(TemplateResultFragment templateFragment) {
		for (TemplateResultFragment templateResultFragment : extras) {
			if (templateResultFragment.getValue().equals(templateFragment.getValue())) {
				return;
			}
		}
		extras.add(templateFragment);
	}
	
	public void joinData(TemplateResultGroup otherTemplateResult) {
		if (result == null) {
			result = new CharacterBuffer();
		}
		result.with(otherTemplateResult.getResult());
		for (TemplateResultFragment templateFragment : otherTemplateResult.getExtras()) {
			addExtra(templateFragment);
		}
	}
	
	public void joinExtras(TemplateResultGroup otherTemplateResult) {
		boolean found = false;
		for (TemplateResultFragment templateFragment : otherTemplateResult.getExtras()) {
			found = false;
			for (TemplateResultFragment ownTemplateFragment : extras) {
				if (templateFragment.getValue().equals(ownTemplateFragment.getValue())) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (!extras.contains(templateFragment)) {
					extras.add(templateFragment);
				}
			}
		}
	}
	
	public CharacterBuffer getResult() {
		return result;
	}
	
}
