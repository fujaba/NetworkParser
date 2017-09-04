package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.yaml.YAMLTokener;
import de.uniks.networkparser.yaml.YamlEntity;

public class YAMLTest {

	@Test
	public void testSimpleYAML() {
//		String text = "Hallo Welt";
//		YamlEntity yamlEntity = new YamlEntity();
//		yamlEntity.withValue(new CharacterBuffer().with(text));
		YamlEntity yamlEntity = parseEntity( "Hallo Welt");
		Assert.assertNotNull(yamlEntity);
		
		yamlEntity = parseEntity("-Test");
		Assert.assertNotNull(yamlEntity);

		yamlEntity = parseEntity("- Test");
		Assert.assertNotNull(yamlEntity);

		yamlEntity = parseEntity("- Test:Value");
		Assert.assertNotNull(yamlEntity);
	}
	
	private YamlEntity parseEntity(String text) {
		YamlEntity yamlEntity = new YamlEntity();
		YAMLTokener tokener = new YAMLTokener();
		tokener.withBuffer(text);
		tokener.parseToEntity(yamlEntity);
		return yamlEntity;
	}
}
