package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryBook;
import de.uniks.networkparser.ext.story.StoryStepJUnit;
import de.uniks.networkparser.ext.story.StoryUtil;

public class StoryTest {
	@Test
	public void testStory() {
		Story story = new Story();
		StoryUtil.withBreakOnAssert(story, false);
//		story.assertEquals("23 not 42", 42, 23, 0.01);
		story.assertEquals("42 == 42", 42, 42);

		story.addSourceCode(StoryTest.class);
		story.addText("<div style=\"border:1px solid red\">Hallo</div>");
		story.addImage("https://seblog.cs.uni-kassel.de/wp-content/themes/segroup/plugins/uni-logo-widget/img/uniks_logo.png");
//		story.add(new StoryStepSourceCode().withCode(StoryTest.class).withCode("src/test/java"));
		story.withName("test.html");

		story.addSourceCode(StoryTest.class, 0, 0);

		story.addSourceCode("src/main/java", IdMap.class, "cloneObject(Object reference, Object filter)");



		ClassModel model = new ClassModel();
		model.createClazz("Person");

		story.addDiagram(model);


		story.dumpHTML();
	}
	
	@Test
	public void testStoryJUnit() {
		Story story = new Story().withName("StoryJUnit");
		story.withPath("build/story");
		StoryStepJUnit storyTest = new StoryStepJUnit().withPackageName("de.uniks.networkparser.test.model");
		// MUST BE JACOCO TO BUILD-Path MAVEN EXAMPLE: dependencies {compile "org.jacoco:jacoco:0.8.1"}
		
		// MSUT BE A LINK TO JACOCO AGENT
		storyTest.withAgentPath("lib/jacocoagent.jar");
		story.add(storyTest);
		story.dumpHTML();
	}


	@Test
	public void testStoryBook() {
		StoryBook book = new StoryBook();
		
		
		Story story = book.createStory("Main");
		story.withName("story/kk.html");
		story.addText("MainText");

		Story halloWelt = book.createStory("HalloWelt");
		Assert.assertNotNull(halloWelt);

		Story ludo = book.createStory("Ludo");
		Assert.assertNotNull(ludo);

		Story startGame = book.createStory("StartGame");
		Assert.assertNotNull(startGame);

		Story winGame  = book.createStory("WinGame");
		Assert.assertNotNull(winGame);

		book.dumpIndexHTML();
	}

}
