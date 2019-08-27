package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.PatternCondition;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.networkparser.parser.Token;

public class CucumberStdRule implements ObjectCondition {
	private Cucumber cucumber;
	private SimpleKeyValueList<String, ClazzSet> assocs = new SimpleKeyValueList<String, ClazzSet>();
	private ClazzSet clazzSet = new ClazzSet();
	private GraphList model;
	private ClazzSet attributeSet;

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

		GraphList model = new GraphList().withType(GraphTokener.OBJECTDIAGRAM);
		if (analyseTokens(cucumber.getGiven(), model) == false) {
			return false;
		}
		this.model = model;
		this.cucumber.withModel(model);
		
		
		// Now Analyse WHEN BLOCK
		model = new GraphList().withType(GraphTokener.OBJECTDIAGRAM);
		if (analyseTokens(cucumber.getWhen(), model) == false) {
			return false;
		}
		Pattern pattern = new Pattern();
		SimpleSet<GraphMember> visited=new SimpleSet<GraphMember>(); 
		for(Clazz clazz : model.getClazzes()) {
			pattern.withCondition(this.analysePattern(new And(), clazz, visited));
			//FIXME AND INCLUDE VISITED
			break;
		}
		this.cucumber.withPattern(pattern);
		return true;
	}

	private ObjectCondition analysePattern(And parent, Clazz clazz, SimpleSet<GraphMember> visited) {
		if(clazz == null) {
			return null;
		}
		if(visited.add(clazz) == false) {
			return null;
		}
		if(parent == null) {
			parent = new And();
		}
		
		// Copy to New One
		InstanceOf condition = InstanceOf.create(clazz);
		parent.add(condition);
		for(Association assoc : clazz.getAssociations()) {
			parent.add(analysePattern(assoc, visited));
		}
		for(Attribute attr : clazz.getAttributes()) {
			parent.add(analysePattern(attr, visited));
		}
		if(parent.size()==1) {
			return parent.first(); 
		}
		return parent;
	}

	private ObjectCondition analysePattern(Association assoc, SimpleSet<GraphMember> visited) {
		if(assoc == null) {
			return null;
		}
		Association other = assoc.getOther();
		if(visited.add(assoc) == false || visited.add(other) == false) {
			return null;
		}
		And parent = new And();
		parent.add(PatternCondition.create(other.getName()));
		Clazz clazz = other.getClazz();
		
		analysePattern(parent, clazz, visited);
		
		return parent;
	}

	private ObjectCondition analysePattern(Attribute attribute, SimpleSet<GraphMember> visited) {
		if(attribute == null) {
			return null;
		}
		String value = attribute.getValue();
		
		Object newValue = GraphUtil.getValue(attribute.getType(), value);
		if(newValue == null) {
			return null;
		}
		return Equals.create(attribute.getName(), newValue);
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
			String[] token = string.split(" ");
			char[] types = new char[token.length];
			int z;
			int isPos = 0;
			int andPos = 0;
			for (z = 0; z < token.length; z++) {
				if (types[z] == 0) {
					types[z] = Token.UNKNOWN;
					if(cucumber.getTypeDictionary(token[z])!= null) {
						// May be Attribute
						if(this.analyseTokenForAttribute(token)) {
							break;
						}
					}
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
					/* A Single Definition */
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
						/* Target is Source and Source */
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

	private boolean analyseTokenForAttribute(String[] token) {
		if(token == null || token.length<1) {
			return false;
		}
		int andPos=0, isPos = 0;
		char[] types=  new char[token.length];
		types[0] = Token.NOMEN;
		boolean isAttribute = false;
		int z;
		for (z = 1; z < token.length; z++) {
			Character type = cucumber.getTokenType(token[z]);
			if (type != null) {
				types[z] = type;
				if (type == Token.DEFINITION) {
					isPos = z;
				}
				if(type == Token.ATTRTYPE ) {
					isAttribute = true;
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
		if(isAttribute && isPos<=0) {
			Attribute attr=null;
			String className=null;
			for(int i = token.length-1;i>=0;i--) {
				if(types[i]==Token.ATTRTYPE) {
					String type = cucumber.getTypeDictionary(token[i]);
					attr = new Attribute(token[i], DataType.create(type));
				}else if(types[i]==Token.NOMEN && attr != null && "has".equalsIgnoreCase(token[i])==false) {
					className = token[i];
					break;
				}
				if(andPos == i-1) {
					//FIXME add AND POS
				}
			}
			if(attr != null && className != null) {
				// Save Attributes
				if(attributeSet == null) {
					this.attributeSet = new ClazzSet();
				}
				Clazz clazz = attributeSet.getClazz(className);
				if(clazz == null) {
					clazz = new Clazz(className);
					attributeSet.add(clazz);
				}
				GraphUtil.withChildren(clazz, attr);
				return true;
			}
			
		}
		return false;
	}

	private boolean analyseSentense(String sentence, GraphModel model) {
		String[] token = sentence.split(" ");
		int z;
		/* Test for replace Link Names */
		if (assocs.size() > 0) {
			StringCondition condition = StringCondition.createEquals(Clazz.PROPERTY_ID, null);
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
					boolean transform = false;
					condition.withValue(token[z - 1]);
					if(model == null || clazzSet.filter(condition).size()<1) {
						transform = true;
					}
					ClazzSet elements = assocs.getValueByIndex(found);
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
				clazzSet.add(getClazz(token[z], model));
			} else if (types[z] == Token.ATTRNAME && (types[z - 2] == Token.NOMEN
					|| types[z - 1] == Token.ATTRNAME && types[z - 3] == Token.NOMEN)) {
				Clazz clazz;
				if (types[z - 1] == Token.ATTRNAME) {
					clazz = getClazz(token[z - 3], model);
				} else {
					clazz = getClazz(token[z - 2], model);
				}
				clazzSet.add(clazz);
				DataType type = DataType.STRING;
				String name = "value";
				try {
					Double.parseDouble(token[z+1]);
					type = DataType.DOUBLE;
					try {
						Integer.parseInt(token[z+1]);
						type = DataType.INT;
					} catch (Exception e) {
					}
				} catch (NumberFormatException e) {
				}
				if (types[z - 1] == Token.ATTRNAME) {
					name = token[z - 1];
				}
				clazz.createAttribute(name, type).withValue(token[z+1]);
			} else if (types[z] == Token.ATTR) {
				Clazz clazz = getClazz(token[z - 1], model);
				DataType type = DataType.STRING;
				if(z<token.length ) {
					if(types[z + 1] == Token.ATTRNAME) {
						continue;
					}
					if(types[z+1]==Token.ATTRVALUE) {
						// Check DataType
						try {
							Double.parseDouble(token[z+1]);
							type = DataType.DOUBLE;
							try {
								Integer.parseInt(token[z+1]);
								type = DataType.INT;
							} catch (Exception e) {
							}
						} catch (NumberFormatException e) {
						}
					}
				}
				clazz.createAttribute(token[z], type);
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
						Clazz sourceClazz = getClazz(token[s], model);
						clazzSet.add(sourceClazz);
						do {
							Clazz targetClazz = getClazz(token[t], model);
							clazzSet.add(targetClazz);
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
		return true;
	}
	
	private boolean analyseTokens(SimpleKeyValueList<String, Boolean> values, GraphModel model) {
		for (int i = 0; i < values.size(); i++) {
			String string = values.getKeyByIndex(i);
			String[] sentences = string.split("\\.");
			for(int s=0;s<sentences.length;s++) {
				analyseSentense(sentences[s].trim(), model);
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
		if(cucumber == null) {
			return null;
		}
		return cucumber.getDictionary(id);
	}

	private Clazz getClazz(String id, GraphModel model) {
		if(cucumber == null || model == null) {
			return null;
		}
		id = cucumber.getDictionary(id);
		Clazz clazz = GraphUtil.createClazzById(model, id);
		
		String type = cucumber.getTypeDictionary(id);
		if (type != null) {
			clazz.with(type);
			// Search for Attribute in Definition
			cucumber.getTypeDictionary(id);
		}
		// Add Attributes from id
		if(attributeSet != null) {
			Clazz prototype = attributeSet.getClazz(clazz.getName());
			if(prototype  != null) {
				AttributeSet attributes = prototype.getAttributes();
				for(Attribute attr : attributes) {
					GraphUtil.withChildren(clazz, new Attribute(attr.getName(), attr.getType()));
				}
			}
		}
		return clazz;
	}
	
	public GraphModel getModel() {
		return model;
	}
}
