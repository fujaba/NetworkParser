package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.json.YAMLTokener;
import de.uniks.networkparser.json.YamlEntity;
import de.uniks.networkparser.test.model.util.UniversityCreator;

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
		tokener.parseToEntity(yamlEntity, new CharacterBuffer().with(text));
		return yamlEntity;
	}

	@Test
	public void YamlDecoder() {
		String yaml = ""
		         + "- studyRight: University \n"
		         + "  name:       \"Study Right\"\n"
		         + "  students:   karli\n"
		         + "  rooms:      mathRoom artsRoom sportsRoom examRoom softwareEngineering \n"
		         + "\n"
		         + "- karli: Student\n"
		         + "  id:    4242\n"
		         + "  name:  karli\n"
		         + "\n"
		         + "- albert: Prof\n"
		         + "  topic:  SE\n"
		         + "\n"
		         + "- Assignment   content:                     points: \n"
		         + "  matrixMult:  \"Matrix Multiplication\"      5\n"
		         + "  series:      Series                      6\n"
		         + "  a3:          Integrals                    8\n"
		         + "\n"
		         + "- Room                  topic:  credits: doors:                 students: assignments: \n"
		         + "  mathRoom:             math    17       null                   karli     [matrixMult series a3]\n"
		         + "  artsRoom:             arts    16       mathRoom               null      null\n"
		         + "  sportsRoom:           sports  25       [mathRoom artsRoom]\n"
		         + "  examRoom:             exam     0       [sportsRoom artsRoom]\n"
		         + "  softwareEngineering:  \"Software Engineering\" 42 [artsRoom examRoom]\n"
		         + "";
		// OLD
//		YamlIdMap yamlIdMap = new YamlIdMap("de.uniks.networkparser.test.model");
//	    University studyRight = (University) yamlIdMap.decode(yaml);
//	    Assert.assertNotNull(studyRight);
	
		// NEW
		YAMLTokener tokener = new YAMLTokener();
		IdMap map = UniversityCreator.createIdMap("");
		tokener.withMap(map);
		Object model = tokener.decode(yaml);
		Assert.assertNotNull(model);
	}
}
