package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.Token;

public class CucumberStdRule implements ObjectCondition {
	private Cucumber cucumber;
	private SimpleKeyValueList<String, ClazzSet> assocs = new SimpleKeyValueList<String, ClazzSet>();
	private GraphList model = new GraphList().withType(GraphTokener.OBJECTDIAGRAM);

	@Override
	public boolean update(Object value) {
		if (value instanceof Cucumber) {
			return analyseCucumber((Cucumber) value);
		}
		return false;
	}

	public boolean analyseCucumber(Cucumber cucumber) {
		this.cucumber = cucumber;

		analyseDefinition(cucumber.getDefinition());

		if (analyseTokens(cucumber.getGiven()) == false) {
			return false;
		}
		this.cucumber.withModel(model);
		return true;
	}

	protected SimpleSet<Token> splitText(String values) {
		Token token = new Token().addKind(Token.UNKNOWN);
		SimpleSet<Token> result = new SimpleSet<Token>();
		for (int i = 0; i < values.length(); i++) {
			char c = values.charAt(i);
			if (c == ' ') {
				result.add(token);
				token = new Token().addKind(Token.UNKNOWN);
				continue;
			}
			if (c == '.') {
				if (token.length() > 0) {
					result.add(token);
				}
				result.add(new Token().addText('.').addKind(Token.POINT));
				token = new Token().addKind(Token.UNKNOWN);
				continue;
			}
			token.addText(c);
		}
		result.add(token);
		return result;
	}

	private boolean analyseDefinition(SimpleKeyValueList<String, Boolean> values) {
		for (int i = 0; i < values.size(); i++) {
			String string = values.getKeyByIndex(i);
			// FIXME
			String[] token = string.split(" ");
			char[] types = new char[token.length];
			int z;
			int isPos = 0;
			int andPos = 0;
			for (z = 0; z < token.length; z++) {
				if (types[z] == 0) {
					types[z] = Token.UNKNOWN;
				}
				Character type = cucumber.getTokenType(token[z]);
				if (type != null) {
					types[z] = type;
					if (type == Token.DEFINITION) {
						isPos = z;
					}
					continue;
				}
				if (token[z].equalsIgnoreCase("and")) {
					andPos = z;
					types[z] = Token.AND;
					types[z - 1] = Token.NOMEN;
					types[z + 1] = Token.NOMEN;
					continue;
				}
				types[z] = Token.NOMEN;
			}
			if (isPos > 0) {
				int target = -1;
				if (andPos < 1) {
					// A Single Definition
					int source = 0;
					for (z = isPos; z < token.length; z++) {
						if (types[z] == Token.NOMEN) {
							target = z;
							break;
						}
					}
					for (z = 0; z < isPos; z++) {
						if (types[z] == Token.NOMEN) {
							source = z;
							break;
						}
					}
					if (source >= 0 && target > 0) {
						cucumber.addTypeDicitonary(token[source], token[target]);
					}
				} else {
					if (andPos > isPos) {
						// Target is Source and Source
						for (z = 0; z < isPos; z++) {
							if (types[z] == Token.NOMEN) {
								target = z;
								break;
							}
						}
						if (target >= 0) {
							for (z = isPos; z < token.length; z++) {
								if (types[z] == Token.NOMEN) {
									cucumber.addTypeDicitonary(token[z], token[target]);
									break;
								}
							}
						}
					} else {
						for (z = isPos; z < token.length; z++) {
							if (types[z] == Token.NOMEN) {
								target = z;
								break;
							}
						}
						if (target >= 0) {
							for (z = 0; z < isPos; z++) {
								if (types[z] == Token.NOMEN) {
									cucumber.addTypeDicitonary(token[z], token[target]);
									break;
								}
							}
						}
					}
				}
			}
			values.setValue(i, true);
		}
		return true;
	}

	private boolean analyseTokens(SimpleKeyValueList<String, Boolean> values) {
		int z;
		// FIXME
		for (int i = 0; i < values.size(); i++) {
//			splitText();
			String string = values.getKeyByIndex(i);
			String[] token = string.split(" ");
			// Test for replace Link Names
			if (assocs.size() > 0) {
				for (z = 0; z < token.length; z++) {
					int found = assocs.indexOf(token[z]);
					int n;
					if (found < 0 && token[z].endsWith("s")) {
						for (n = 0; n < assocs.size(); n++) {
							String key = assocs.getKeyByIndex(n);
							if (token[z].startsWith(key)) {
								found = n;
								break;
							}
						}
					}
					if (found >= 0) {
						ClazzSet elements = assocs.getValueByIndex(found);
						boolean transform = true;
						if (z > 0) {
							if (elements.contains((Object) token[z - 1])) {
								transform = false;
							}
						}
						if (transform) {
							String[] newToken = new String[token.length + (elements.size() + elements.size() - 1) - 1];
							for (n = 0; n < z; n++) {
								newToken[n] = token[n];
							}
							int temp = z;
							for (n = 0; n < elements.size(); n++) {
								newToken[n + temp] = elements.get(n).getId();
								if (n < elements.size() - 1) {
									temp++;
									newToken[n + temp] = "and";
								}
							}
							n = n + temp;
							temp = z + 1;
							while (temp < token.length) {
								newToken[n++] = token[temp++];
							}
							token = newToken;
						}
					}
				}
			}
			char[] types = new char[token.length];
			for (z = 0; z < token.length; z++) {
				if (types[z] == 0) {
					types[z] = Token.UNKNOWN;
				}
				Character type = cucumber.getTokenType(token[z]);
				if (type != null) {
					types[z] = type;
					continue;
				}
				if (token[z].equalsIgnoreCase("and")) {
					types[z] = Token.AND;
					types[z - 1] = Token.NOMEN;
					types[z + 1] = Token.NOMEN;
				}
			}
			for (z = 0; z < token.length; z++) {
				if (types[z] != Token.UNKNOWN) {
					continue;
				}
				if (z < 1) {
					types[z] = Token.NOMEN;
					continue;
				}
				if (types[z - 1] == Token.ATTRNAME) {
					types[z] = Token.ATTRVALUE;
					continue;
				}
				if (types[z - 1] == Token.ATTR) {
					try {
						Double.parseDouble(token[z]);
						types[z] = Token.ATTRVALUE;
					} catch (NumberFormatException e) {
						types[z] = Token.ATTRNAME;
					}
					continue;
				}

				if (types[z - 1] == Token.NOMEN) {
					types[z] = Token.VERB;
				} else {
					types[z] = Token.NOMEN;
				}
			}
			for (z = 0; z < token.length; z++) {
				if (types[z] == Token.NOMEN) {
					getClazz(token[z]);
				} else if (types[z] == Token.ATTRNAME && (types[z - 2] == Token.NOMEN
						|| types[z - 1] == Token.ATTRNAME && types[z - 3] == Token.NOMEN)) {
					Clazz clazz;
					if (types[z - 1] == Token.ATTRNAME) {
						clazz = getClazz(token[z - 3]);
					} else {
						clazz = getClazz(token[z - 2]);
					}
					DataType type = DataType.STRING;
					String name = "value";
					try {
						Double.parseDouble(token[z]);
						type = DataType.DOUBLE;
						try {
							Integer.parseInt(token[z]);
							type = DataType.INT;
						} catch (Exception e) {
						}
					} catch (NumberFormatException e) {
					}
					if (types[z - 1] == Token.ATTRNAME) {
						name = token[z - 1];
					}
					clazz.createAttribute(name, type).withValue(token[z]);
				} else if (types[z] == Token.VERB) {
					int source = Association.ONE;
					int target = Association.ONE;
					if (z > 1 && types[z - 2] == Token.AND) {
						if (token[z].endsWith("s") && token[z].equals("has") == false) {
							token[z] = token[z].substring(0, token[z].length() - 1);
						}
						source = Association.MANY;
					}
					if (z + 2 < types.length && types[z + 2] == Token.AND) {
						target = Association.MANY;
					}
					int s = z - 1;
					int t = z + 1;
					if (s >= 0) {
						do {
							Clazz sourceClazz = getClazz(token[s]);
							do {
								Clazz targetClazz = getClazz(token[t]);
								sourceClazz.withUniDirectional(targetClazz, getLinkName(token[z]), target);
								addToAssoc(source, sourceClazz, token[z]);
								if (t + 1 < types.length && types[t + 1] == Token.AND) {
									t += 2;
								} else {
									break;
								}
							} while (t <= types.length);
							if (s > 0 && types[s - 1] == Token.AND) {
								s -= 2;
							} else {
								break;
							}

						} while (s >= 0);
					}
				}
			}
			values.setValue(i, Boolean.TRUE);
		}
		return true;
	}

	private boolean addToAssoc(int source, Clazz sourceClazz, String assocName) {
		assocName = assocName.toLowerCase();
		if (source == Association.MANY && assocName.endsWith("s")) {
			assocName = assocName.substring(0, assocName.length() - 1);
		}
		ClazzSet sub = assocs.get(assocName);
		if (sub != null) {
			return sub.add(sourceClazz);
		}
		sub = new ClazzSet().with(sourceClazz);
		return assocs.add(assocName, sub);
	}

	private String getLinkName(String id) {
		return cucumber.getDictionary(id);
	}

	private Clazz getClazz(String id) {
		id = cucumber.getDictionary(id);
		Clazz clazz = GraphUtil.createClazzById(model, id);
		String type = cucumber.getTypeDictionary(id);
		if (type != null) {
			clazz.with(type);
		}
		return clazz;
	}
}
