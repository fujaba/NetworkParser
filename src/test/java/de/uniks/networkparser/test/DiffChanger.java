package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.TextDiff;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class DiffChanger {
	
	private JsonObject createHelpClass(String name, String color) {
		JsonObject master=new JsonObject();
		JsonArray assoc=new JsonArray();
		master.add("trainers", assoc);
		assoc.add(createTrainerClass(name, color));
		return master;
	}
	private JsonObject createTrainerClass(String name, String color) {
		JsonObject trainer=new JsonObject();
		JsonObject props = new JsonObject();
		trainer.add("props", props);
		props.add("name", name);
		props.add("color", color);
		return trainer;
	}
	@Test
	public void compareJsonWithDifference() {
		JsonObject master = this.createHelpClass("Alice", "green");
		JsonObject slave = this.createHelpClass("Bob", "blue");
		slave.getJsonArray("trainers").add(createTrainerClass("Stefan", "black"));
		TextDiff diffs=new TextDiff();
		Assert.assertFalse(EntityUtil.compareEntity(master, slave, diffs, null));
		System.out.println(diffs);
		
	}
}
