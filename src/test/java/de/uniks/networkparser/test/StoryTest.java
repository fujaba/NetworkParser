package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryUtil;

public class StoryTest {
	@Test
	public void testStory() {
		Story story = new Story();
		StoryUtil.withBreakOnAssert(story, false);
		story.assertEquals("23 not 42", 42, 23, 0.01);


		story.withFileName("test.html");
		story.dumpHTML();
	}
}
