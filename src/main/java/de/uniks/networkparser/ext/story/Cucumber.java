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

/**
 * The Class Cucumber.
 *
 * @author Stefan
 */
public class Cucumber implements ObjectCondition {
	
	/** The Constant TYPE_SCENARIO. */
	public static final String TYPE_SCENARIO = "scenario";
	
	/** The Constant TYPE_TITLE. */
	public static final String TYPE_TITLE = "title";
	
	/** The Constant TYPE_GIVEN. */
	public static final String TYPE_GIVEN = "given";
	
	/** The Constant TYPE_WHEN. */
	public static final String TYPE_WHEN = "when";
	
	/** The Constant TYPE_THEN. */
	public static final String TYPE_THEN = "then";
	
	/** The Constant TYPE_STARTSITUATION. */
	public static final String TYPE_STARTSITUATION = "start situation";
	
	/** The Constant TYPE_ACTION. */
	public static final String TYPE_ACTION = "action";
	
	/** The Constant TYPE_RESULTSITUATION. */
	public static final String TYPE_RESULTSITUATION = "result situation"; 
	
	/** The Constant TYPE_DEFINITION. */
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

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public GraphList getModel() {
		return model;
	}
	
	/**
	 * Gets the class model.
	 *
	 * @param packageName the package name
	 * @return the class model
	 */
	public ClassModel getClassModel(String packageName) {
		ClassModel classModel = new ClassModel(packageName);
		if(this.dirty ) {
			this.analyse();
		}
		classModel.add(model);
		return classModel;
	}

	/**
	 * Adds the type dicitonary.
	 *
	 * @param values the values
	 * @return the cucumber
	 */
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
	
	/**
	 * Adds the dictionary.
	 *
	 * @param name the name
	 * @param type the type
	 * @param token the token
	 */
	public void addDictionary(String name, String type, char token) {
		typeDictionary.add(name, type);
		tokens.add(name, token);
	}

	/**
	 * Adds the token rule.
	 *
	 * @param token the token
	 * @param type the type
	 * @return the cucumber
	 */
	public Cucumber addTokenRule(String token, char type) {
		this.tokens.add(token.toLowerCase(), type);
		return this;
	}

	/**
	 * Adds the dicitonary.
	 *
	 * @param values the values
	 * @return the cucumber
	 */
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

	/**
	 * Adds the rule.
	 *
	 * @param rule the rule
	 * @return true, if successful
	 */
	public boolean addRule(ObjectCondition rule) {
		return this.rules.add(rule);
	}

	private Cucumber withTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the rules.
	 *
	 * @return the rules
	 */
	public SimpleList<ObjectCondition> getRules() {
		return rules;
	}

	/**
	 * Gets the given.
	 *
	 * @return the given
	 */
	public SimpleKeyValueList<String, Boolean> getGiven() {
		return given;
	}

	/**
	 * Gets the when.
	 *
	 * @return the when
	 */
	public SimpleKeyValueList<String, Boolean> getWhen() {
		return when;
	}

	/**
	 * Gets the then.
	 *
	 * @return the then
	 */
	public SimpleKeyValueList<String, Boolean> getThen() {
		return then;
	}

	/**
	 * Given.
	 *
	 * @param value the value
	 * @return the cucumber
	 */
	public Cucumber Given(String value) {
		given.add(value, false);
		this.dirty = true;
		return this;
	}

	/**
	 * When.
	 *
	 * @param value the value
	 * @return the cucumber
	 */
	public Cucumber When(String value) {
		when.add(value, false);
		this.dirty = true;
		return this;
	}

	/**
	 * Then.
	 *
	 * @param value the value
	 * @return the cucumber
	 */
	public Cucumber Then(String value) {
		then.add(value, false);
		this.dirty = true;
		return this;
	}

	/**
	 * Definition.
	 *
	 * @param value the value
	 * @return the cucumber
	 */
	public Cucumber Definition(String value) {
		definition.add(value, false);
		this.dirty = true;
		return this;
	}

	/**
	 * Creates the scenario.
	 *
	 * @param title the title
	 * @return the cucumber
	 */
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

	/**
	 * Creates the next scenario.
	 *
	 * @param title the title
	 * @return the cucumber
	 */
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

	/**
	 * Creates the from file.
	 *
	 * @param file the file
	 * @return the cucumber
	 */
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

	/**
	 * Creates the.
	 *
	 * @param type the type
	 * @return the cucumber
	 */
	public static final Cucumber create(String type) {
		if (type == null || type.toLowerCase() == TYPE_SCENARIO) {
			return createScenario(null);
		}
		return new Cucumber();
	}

	/**
	 * Clear type directory.
	 *
	 * @return true, if successful
	 */
	public boolean clearTypeDirectory() {
		typeDictionary.clear();
		return true;
	}

	/**
	 * Clear directory.
	 *
	 * @return true, if successful
	 */
	public boolean clearDirectory() {
		dictionary.clear();
		return true;
	}

	/**
	 * Analyse.
	 *
	 * @return true, if successful
	 */
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

	/**
	 * Gets the type dictionary.
	 *
	 * @param key the key
	 * @return the type dictionary
	 */
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

	/**
	 * With model.
	 *
	 * @param model the model
	 * @return the cucumber
	 */
	public Cucumber withModel(GraphList model) {
		this.model = model;
		return this;
	}

	/**
	 * Gets the dictionary.
	 *
	 * @param id the id
	 * @return the dictionary
	 */
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

	/**
	 * Gets the definition.
	 *
	 * @return the definition
	 */
	public SimpleKeyValueList<String, Boolean> getDefinition() {
		return definition;
	}

	/**
	 * Gets the token type.
	 *
	 * @param value the value
	 * @return the token type
	 */
	public Character getTokenType(String value) {
		Character test = tokens.get(value);
		if (test == null && parent != null) {
			return parent.getTokenType(value);
		}
		return test;
	}
	
	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
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
				output.createChild("h1", this.getTitle());
				output.createChild("p", TYPE_DEFINITION+":");
				for(int i=0;i<this.definition.size();i++) {
					output.createChild("div", this.definition.getKeyByIndex(i));
				}
				
				CharacterBuffer buffer=new CharacterBuffer();
				output.createChild("p", TYPE_STARTSITUATION+":");
				for(int i=0;i<this.given.size();i++) {
					buffer.append(this.given.getKeyByIndex(i));
				}
				output.createChild("div", buffer.toString());
				
				buffer.clear();
				output.createChild("p", TYPE_ACTION+":");
				for(int i=0;i<this.when.size();i++) {
					buffer.append(this.when.getKeyByIndex(i));
				}
				output.createChild("div", buffer.toString());
				
				buffer.clear();
				output.createChild("p", TYPE_RESULTSITUATION+":");
				for(int i=0;i<this.then.size();i++) {
					buffer.append(this.then.getKeyByIndex(i));
				}
				output.createChild("div", buffer.toString());
				ClassModel model=new ClassModel();
				model.add(this.model);
				output.withGraph(model);
			}
		}
		return true;
	}
	
	/**
	 * With text.
	 *
	 * @param type the type
	 * @param text the text
	 * @return the cucumber
	 */
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

	/**
	 * With pattern.
	 *
	 * @param pattern the pattern
	 * @return the cucumber
	 */
	public Cucumber withPattern(Pattern pattern) {
		this.pattern = pattern;
		return this;
	}
	
	/**
	 * Gets the pattern.
	 *
	 * @return the pattern
	 */
	public Pattern getPattern() {
		if(this.dirty) {
			this.analyse();
		}
		return pattern;
	}
}
