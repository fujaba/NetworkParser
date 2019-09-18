package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.Token;
import de.uniks.networkparser.xml.HTMLEntity;

public class Cucumber implements ObjectCondition {
	public static final String TYPE_SCENARIO = "scenario";
	public static final String TYPE_TITLE = "title";
	public static final String TYPE_GIVEN = "given";
	public static final String TYPE_WHEN = "when";
	public static final String TYPE_THEN = "then";
	public static final String TYPE_STARTSITUATION = "start situation";
	public static final String TYPE_ACTION = "action";
	public static final String TYPE_RESULTSITUATION = "result situation"; 
	
	public static final String TYPE_DEFINITION = "definition";

	private Cucumber parent;

	private SimpleKeyValueList<String, Boolean> given = new SimpleKeyValueList<String, Boolean>();
	private SimpleKeyValueList<String, Boolean> when = new SimpleKeyValueList<String, Boolean>();
	private SimpleKeyValueList<String, Boolean> then = new SimpleKeyValueList<String, Boolean>();
	private SimpleKeyValueList<String, Boolean> definition = new SimpleKeyValueList<String, Boolean>();
	private SimpleList<ObjectCondition> rules = new SimpleList<ObjectCondition>();
	private SimpleKeyValueList<String, String> typeDictionary = new SimpleKeyValueList<String, String>();
	private SimpleKeyValueList<String, String> dictionary = new SimpleKeyValueList<String, String>();

	private SimpleKeyValueList<String, Character> tokens = new SimpleKeyValueList<String, Character>();
	private String title;
	private GraphList model;
	private Pattern pattern;
	private boolean dirty;

	public GraphList getModel() {
		return model;
	}
	
	public ClassModel getClassModel(String packageName) {
		ClassModel classModel = new ClassModel(packageName);
		if(this.dirty ) {
			this.analyse();
		}
		classModel.add(model);
		return classModel;
	}

	public Cucumber addTypeDicitonary(String... values) {
		if (values == null || values.length < 1) {
			typeDictionary.add("alice", "Player");
			typeDictionary.add("bob", "Player");
			typeDictionary.add("albert", "Player");
			typeDictionary.add("stefan", "Player");
			typeDictionary.add("ludo", "Game");
			typeDictionary.add("startingArea", "Place");

			typeDictionary.add("token", "Stone");
			typeDictionary.add("startarea", "Place");
			
			addDictionary("name", "String", Token.ATTRTYPE);
			return this;
		}
		for (int i = 0; i < values.length - 1; i += 2) {
			if (values[i] != null) {
				typeDictionary.add(values[i].toLowerCase().trim(), values[i + 1].trim());
			}
		}
		return this;
	}
	
	public void addDictionary(String name, String type, char token) {
		typeDictionary.add(name, type);
		tokens.add(name, token);
	}

	public Cucumber addTokenRule(String token, char type) {
		this.tokens.add(token.toLowerCase(), type);
		return this;
	}

	public Cucumber addDicitonary(String... values) {
		if (values == null || values.length < 1) {
			dictionary.add("has", "has");
			dictionary.add("startingArea", "startArea");
			return this;
		}
		for (int i = 0; i < values.length - 1; i += 2) {
			if (values[i] != null) {
				dictionary.add(values[i].toLowerCase().trim(), values[i + 1].trim());
			}
		}
		return this;
	}

	public boolean addRule(ObjectCondition rule) {
		return this.rules.add(rule);
	}

	private Cucumber withTitle(String title) {
		this.title = title;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public SimpleList<ObjectCondition> getRules() {
		return rules;
	}

	public SimpleKeyValueList<String, Boolean> getGiven() {
		return given;
	}

	public SimpleKeyValueList<String, Boolean> getWhen() {
		return when;
	}

	public SimpleKeyValueList<String, Boolean> getThen() {
		return then;
	}

	public Cucumber Given(String value) {
		given.add(value, false);
		this.dirty = true;
		return this;
	}

	public Cucumber When(String value) {
		when.add(value, false);
		this.dirty = true;
		return this;
	}

	public Cucumber Then(String value) {
		then.add(value, false);
		this.dirty = true;
		return this;
	}

	public Cucumber Definition(String value) {
		definition.add(value, false);
		this.dirty = true;
		return this;
	}

	public static Cucumber createScenario(String title) {
		Cucumber cucumber = new Cucumber().withTitle(title);
		cucumber.addRule(new CucumberStdRule());
		cucumber.addTokenRule("with", Token.ATTR);
		cucumber.addTokenRule("the", Token.IGNORE);
		cucumber.addTokenRule("a", Token.IGNORE);
		cucumber.addTokenRule("is", Token.DEFINITION);

		cucumber.addDicitonary();
		cucumber.addTypeDicitonary();
		return cucumber;
	}

	public Cucumber createNextScenario(String title) {
		Cucumber cucumber = new Cucumber().withTitle(title);
		cucumber.withParent(this);
		cucumber.addRule(new CucumberStdRule());
		return cucumber;
	}

	private Cucumber withParent(Cucumber cucumber) {
		this.parent = cucumber;
		return this;
	}

	public static Cucumber createFromFile(String file) {
		CharacterBuffer readFile = FileBuffer.readFile(file);
		SimpleList<String> lines = readFile.splitStrings('\n');
		if (lines.size() < 1) {
			return null;
		}
		String line = lines.get(0);
		int pos = line.indexOf(":");
		Cucumber cucumber;
		if (pos > 0) {
			String temp = line.substring(0, pos);
			cucumber = create(temp);
			line = line.substring(pos + 1); /* REMOVE TYPE */
		} else {
			cucumber = create(TYPE_SCENARIO);
		}
		int i = 1;
		CharacterBuffer subText = new CharacterBuffer();
		subText.add(line.trim());
		String type = null;
		for (; i < lines.size(); i++) {
			type = getType(line);
			if (type == null) {
				subText.add(BaseItem.CRLF);
				subText.add(line.trim());
			} else {
				break;
			}
		}
		cucumber.withTitle(subText.toString());
		subText.clear();
		for (; i < lines.size(); i++) {
			String newType = getType(line);
			if (newType == null) {
				if (subText.length() > 0) {
					subText.add(" ");
				}
				subText.add(line);
			} else {
				if (subText.length() < 1) {
					/* FIRSTLINE */
					type = newType;
					subText.add(line.substring(newType.length() + 2));
				} else {
					if (type.equals(TYPE_GIVEN)) {
						cucumber.Given(subText.toString());
					}
					if (type.equals(TYPE_WHEN)) {
						cucumber.When(subText.toString());
					}
					if (type.equals(TYPE_THEN)) {
						cucumber.Then(subText.toString());
					}
					if (type.equals(TYPE_DEFINITION)) {
						cucumber.Definition(subText.toString());
					}
					subText.clear();
					subText.add(line.substring(newType.length() + 2));
					type = newType;
				}
			}
			if (subText.length() > 0) {
				if (type.equals(TYPE_GIVEN)) {
					cucumber.Given(subText.toString());
				}
				if (type.equals(TYPE_WHEN)) {
					cucumber.When(subText.toString());
				}
				if (type.equals(TYPE_THEN)) {
					cucumber.Then(subText.toString());
				}
				if (type.equals(TYPE_DEFINITION)) {
					cucumber.Definition(subText.toString());
				}
			}
		}
		return cucumber;
	}

	private static String getType(String line) {
		int pos = line.indexOf(":");
		if (pos < 0) {
			return null;
		}
		String temp = line.substring(0, pos);
		if (temp.equalsIgnoreCase(TYPE_GIVEN)) {
			return TYPE_GIVEN;
		}
		if (temp.equalsIgnoreCase(TYPE_WHEN)) {
			return TYPE_WHEN;
		}
		if (temp.equalsIgnoreCase(TYPE_THEN)) {
			return TYPE_THEN;
		}
		if (temp.equalsIgnoreCase(TYPE_DEFINITION)) {
			return TYPE_DEFINITION;
		}
		return null;
	}

	public static final Cucumber create(String type) {
		if (type == null || type.toLowerCase() == TYPE_SCENARIO) {
			return createScenario(null);
		}
		return new Cucumber();
	}

	public boolean clearTypeDirectory() {
		typeDictionary.clear();
		return true;
	}

	public boolean clearDirectory() {
		dictionary.clear();
		return true;
	}

	public boolean analyse() {
		for (ObjectCondition rule : rules) {
			if (rule.update(this) == false) {
				return false;
			}
		}
		int i;
		for (i = 0; i < given.size(); i++) {
			Boolean key = given.getValueByIndex(i);
			if (key.booleanValue() == false) {
				return false;
			}
		}
		for (i = 0; i < when.size(); i++) {
			Boolean key = when.getValueByIndex(i);
			if (key.booleanValue() == false) {
				return false;
			}
		}
		for (i = 0; i < then.size(); i++) {
			Boolean key = then.getValueByIndex(i);
			if (key.booleanValue() == false) {
				return false;
			}
		}
		this.dirty = false;
		return true;
	}

	public String getTypeDictionary(String key) {
		if (key == null) {
			return null;
		}
		String result = typeDictionary.get(key.toLowerCase().trim());
		if (result == null && parent != null) {
			return parent.getTypeDictionary(key);
		}
		return result;
	}

	public Cucumber withModel(GraphList model) {
		this.model = model;
		return this;
	}

	public String getDictionary(String id) {
		if (id == null) {
			return id;
		}
		if (this.parent != null) {
			id = parent.getDictionary(id);
		}
		boolean removeS = true;
		id = id.trim();
		for (int i = 0; i < dictionary.size(); i++) {
			if (id.equalsIgnoreCase(dictionary.getKeyByIndex(i))) {
				String temp = dictionary.getValueByIndex(i);
				if (id.equalsIgnoreCase(temp)) {
					removeS = false;
					id = temp;
					break;
				}
				id = temp;
				i = 0;
			}
		}
		if (removeS) {
			if (id.endsWith("s")) {
				id = id.substring(0, id.length() - 1);
			}
		}
		return id;
	}

	public SimpleKeyValueList<String, Boolean> getDefinition() {
		return definition;
	}

	public Character getTokenType(String value) {
		Character test = tokens.get(value);
		if (test == null && parent != null) {
			return parent.getTokenType(value);
		}
		return test;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		this.analyse();
		if(this.model != null) {
			SimpleEvent event = (SimpleEvent) value;
			Object newValue = event.getNewValue();
			if(newValue instanceof HTMLEntity) {
				HTMLEntity output = (HTMLEntity) newValue;
				// Write File
				output.createBodyTag("h1", this.getTitle());
				output.createBodyTag("p", TYPE_DEFINITION+":");
				for(int i=0;i<this.definition.size();i++) {
					output.createBodyTag("div", this.definition.getKeyByIndex(i));
				}
				
				CharacterBuffer buffer=new CharacterBuffer();
				output.createBodyTag("p", TYPE_STARTSITUATION+":");
				for(int i=0;i<this.given.size();i++) {
					buffer.append(this.given.getKeyByIndex(i));
				}
				output.createBodyTag("div", buffer.toString());
				
				buffer.clear();
				output.createBodyTag("p", TYPE_ACTION+":");
				for(int i=0;i<this.when.size();i++) {
					buffer.append(this.when.getKeyByIndex(i));
				}
				output.createBodyTag("div", buffer.toString());
				
				buffer.clear();
				output.createBodyTag("p", TYPE_RESULTSITUATION+":");
				for(int i=0;i<this.then.size();i++) {
					buffer.append(this.then.getKeyByIndex(i));
				}
				output.createBodyTag("div", buffer.toString());
				ClassModel model=new ClassModel();
				model.add(this.model);
				output.withGraph(model);
			}
		}
		return true;
	}
	
	public Cucumber withText(String type, String text) {
		if(TYPE_TITLE.equalsIgnoreCase(type)) {
			this.withTitle(text);
			return this;
		}
		if(TYPE_DEFINITION.equalsIgnoreCase(type) ) {
			this.Definition(text);
			return this;
		}
		if(TYPE_GIVEN.equalsIgnoreCase(type) || TYPE_STARTSITUATION.equalsIgnoreCase(type)) {
			this.Given(text);
			return this;
		}
		if(TYPE_WHEN.equalsIgnoreCase(type) || TYPE_ACTION.equalsIgnoreCase(type)) {
			this.When(text);
			return this;
		}
		if(TYPE_THEN.equalsIgnoreCase(type) || TYPE_RESULTSITUATION.equalsIgnoreCase(type)) {
			this.Then(text);
		}
		return this;
	}

	public Cucumber withPattern(Pattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
}
