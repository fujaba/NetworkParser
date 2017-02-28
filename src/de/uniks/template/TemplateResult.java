package de.uniks.template;

import java.util.HashMap;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SortedList;

public class TemplateResult {

	private SortedList<TemplateFragment> extras = new SortedList<TemplateFragment>();
	
	private CharacterBuffer result = null;
	
	public void execute(Template rootTemplate, HashMap<String, String> parameters, boolean isStandard) {
		result = new CharacterBuffer();
		String templateResult = "";
		Template template = rootTemplate;
		TemplateFragment templateFragment = null;
		while (template != null) {
			templateResult = template.generate(parameters);
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
		for (TemplateFragment templateFragment : otherTemplateResult.getExtras()) {
			extras.add(templateFragment);
		}
	}
	
	public CharacterBuffer getResult() {
		return result;
	}
	
}
