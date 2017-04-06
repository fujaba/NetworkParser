package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SortedList;

public class TemplateResult {

	private SortedList<TemplateFragment> extras = new SortedList<TemplateFragment>(true);
	
	private CharacterBuffer result = null;
	
	public void execute(Template rootTemplate, SimpleKeyValueList<String, String> parameters, boolean isStandard, GraphMember member) {
		result = new CharacterBuffer();
		String templateResult = "";
		Template template = rootTemplate;
		TemplateFragment templateFragment = null;
		while (template != null) {
			//FIXME
			templateResult = template.generate(parameters, member);
			if (templateResult != null && templateResult.length() > 0) {
				if (isStandard) {
					result.with(templateResult).with("\n");
				} else {
					templateFragment = new TemplateFragment()
							.withKey(template.getType())
							.withValue(templateResult + "\n");
					extras.add(templateFragment);
				}
			}
			template = template.getNextTemplate();
		}
	}
	
	public SortedList<TemplateFragment> getExtras() {
		return extras;
	}
	
	public void addExtra(int key, String value) {
		for (TemplateFragment templateFragment : extras) {
			if (templateFragment.getValue().equals(value)) {
				return;
			}
		}
		TemplateFragment templateFragment = new TemplateFragment()
				.withKey(key)
				.withValue(value);
		extras.add(templateFragment);
	}
	
	public void joinData(TemplateResult otherTemplateResult) {
		if (result == null) {
			result = new CharacterBuffer();
		}
		result.with(otherTemplateResult.getResult());
		for (TemplateFragment templateFragment : otherTemplateResult.getExtras()) {
			extras.add(templateFragment);
		}
	}
	
	public void joinExtras(TemplateResult otherTemplateResult) {
		boolean found = false;
		for (TemplateFragment templateFragment : otherTemplateResult.getExtras()) {
			found = false;
			for (TemplateFragment ownTemplateFragment : extras) {
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
