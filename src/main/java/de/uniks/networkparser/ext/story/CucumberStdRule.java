package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class CucumberStdRule implements ObjectCondition {
	private Cucumber cucumber;
	private SimpleKeyValueList<String, SimpleList<Clazz>> assocs=new SimpleKeyValueList<String, SimpleList<Clazz>>();
	private GraphList model = new GraphList().withType(GraphTokener.OBJECTDIAGRAM);

	@Override
	public boolean update(Object value) {
		if(value instanceof Cucumber) {
			return analyseCucumber((Cucumber) value);
		}
		return false;
	}
	
	public boolean analyseCucumber(Cucumber cucumber) {
		this.cucumber = cucumber;
		
		analyseDefinition(cucumber.getDefinition());
		
		if(analyseTokens(cucumber.getGiven()) == false) {
			return false;
		}
		this.cucumber.withModel(model);
		return true;
	}
	
	private boolean analyseDefinition(SimpleKeyValueList<String, Boolean > values) {
		for(int i=0;i<values.size();i++) {
			String string = values.getKeyByIndex(i);
			String[] token = string.split(" ");
			char[] types= new char[token.length];
			int z;
			int isPos = 0;
			int andPos = 0;
			for(z=0;z<token.length;z++) {
				if(types[z]==0) {
					types[z]=Cucumber.UNKNOWN;
				}
				Character type = cucumber.getTokenType(token[z]);
				if(type != null) {
					types[z] = type;
					if(type==Cucumber.E) {
						isPos = z;
					}
					continue;
				}
				if(token[z].equalsIgnoreCase("and")) {
					andPos = z;
					types[z]=Cucumber.U;
					types[z-1]=Cucumber.N;
					types[z+1]=Cucumber.N;
					continue;
				}
				types[z]=Cucumber.N;
			}
			if(isPos>0) {
				int target = -1;
				if(andPos<1) {
					// A Single Definition
					int source = 0;
					for(z=isPos;z<token.length;z++) {
						if(types[z] == Cucumber.N) {
							target=z;
							break;
						}
					}
					for(z=0;z<isPos;z++) {
						if(types[z] == Cucumber.N) {
							source=z;
							break;
						}
					}
					if(source >= 0&& target > 0) {
						cucumber.addTypeDicitonary(token[source], token[target]);
					}
				} else {
					if(andPos>isPos) {
						// Target is Source and Source
						for(z=0;z<isPos;z++) {
							if(types[z] == Cucumber.N) {
								target=z;
								break;
							}
						}
						if(target>=0) {
							for(z=isPos;z<token.length;z++) {
								if(types[z] == Cucumber.N) {
									cucumber.addTypeDicitonary(token[z], token[target]);
									break;
								}
							}
						}
					} else {
						for(z=isPos;z<token.length;z++) {
							if(types[z] == Cucumber.N) {
								target=z;
								break;
							}
						}
						if(target>=0) {
							for(z=0;z<isPos;z++) {
								if(types[z] == Cucumber.N) {
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

	private boolean analyseTokens(SimpleKeyValueList<String, Boolean > values) {
		int z;
		for(int i=0;i<values.size();i++) {
			String string = values.getKeyByIndex(i);
			String[] token = string.split(" ");
			// Test for replace Link Names
			if(assocs.size()>0) {
				for(z=0;z<token.length;z++) {
					int found =assocs.indexOf(token[z]);
					int n;
					if(found < 0 && token[z].endsWith("s")) {
						for(n=0;n<assocs.size();n++) {
							String key = assocs.getKeyByIndex(n);
							if(token[z].startsWith(key)) {
								found=n;
								break;
							}
						}
					}
					if(found >= 0) {
						SimpleList<Clazz> elements = assocs.getValueByIndex(found);
						String[] newToken=new String[token.length+(elements.size() + elements.size()-1) - 1];
						for(n=0;n<z;n++) {
							newToken[n]=token[n];
						}
						int temp=z;
						for(n=0;n<elements.size();n++) {
							newToken[n+temp]=elements.get(n).getId();
							if(n<elements.size()-1) {
								temp++;
								newToken[n+temp]="and";
							}
						}
						n=n+temp;
						temp = z+1;
						while(temp<token.length) {
							newToken[n++] = token[temp++];
						}
						token = newToken;
					}
				}
			}
			char[] types= new char[token.length];
			for(z=0;z<token.length;z++) {
				if(types[z]==0) {
					types[z]=Cucumber.UNKNOWN;
				}
				Character type = cucumber.getTokenType(token[z]);
				if(type != null) {
					types[z]=type;
					continue;
				}
				if(token[z].equalsIgnoreCase("and")) {
					types[z]=Cucumber.U;
					types[z-1]=Cucumber.N;
					types[z+1]=Cucumber.N;
				}
			}
			for(z=0;z<token.length;z++) {
				if(types[z] != Cucumber.UNKNOWN) {
					continue;
				}
				if(z<1) {
					types[z]=Cucumber.V;
					continue;
				}
				if(types[z-1] == Cucumber.A) {
					types[z] = Cucumber.T;
					continue;
				}

				if(types[z-1] == Cucumber.V) {
					types[z] = Cucumber.N;
				} else {
					types[z] = Cucumber.V;
				}
			}
			for(z=0;z<token.length;z++) {
				if(types[z]==Cucumber.N) {
					getClazz(token[z]);
				} else if(types[z]==Cucumber.T && types[z-2] ==Cucumber.N) {
					Clazz clazz = getClazz(token[z-2]);
					try {
						Double.parseDouble(token[z]);
						clazz.withAttribute(token[z], DataType.DOUBLE);
						try {
							Integer.parseInt(token[z]);
						}catch (Exception e) {
							clazz.withAttribute(token[z], DataType.INT);
						}
					} catch (NumberFormatException e) {
						clazz.withAttribute(token[z], DataType.STRING);
					}
				} else if(types[z]==Cucumber.V) {
					int source = Association.ONE;
					int target = Association.ONE;
					if(z>1 && types[z-2]==Cucumber.U) {
						if(token[z].endsWith("s") && token[z].equals("has") == false) {
							token[z] = token[z].substring(0, token[z].length() - 1);
						}
						source = Association.MANY;
					}
					if(z+2<types.length && types[z+2]==Cucumber.U) {
						target = Association.MANY;
					}
					int s=z-1;
					int t=z+1;
					if(s>=0) {
						do {
							Clazz sourceClazz = getClazz(token[s]);
							do {
								Clazz targetClazz = getClazz(token[t]);
								sourceClazz.withUniDirectional(targetClazz, getLinkName(token[z]), target);
								addToAssoc(source, sourceClazz, token[z]);
								if(t+1<types.length && types[t+1]==Cucumber.U) {
									t+=2;
								}else {
									break;
								}	
							}while(t<=types.length);
							if(s > 0 && types[s-1]==Cucumber.U) {
								s-=2;
							}else {
								break;
							}
							
						} while(s>=0);
					}
				}
			}
			values.setValue(i, Boolean.TRUE);
		}
		return true;
	}
	
	private boolean addToAssoc(int source, Clazz sourceClazz, String assocName) {
		assocName = assocName.toLowerCase();
		if(source==Association.MANY && assocName.endsWith("s")) {
			assocName = assocName.substring(0, assocName.length()-1);
		}
		SimpleList<Clazz> sub = assocs.get(assocName);
		if(sub != null) {
			return sub.add(sourceClazz);
		}
		sub = new SimpleList<Clazz>().with(sourceClazz);
		return assocs.add(assocName, sub);
	}
	
	private String getLinkName(String id) {
		return cucumber.getDictionary(id);
	}

	private Clazz getClazz(String id) {
		id = cucumber.getDictionary(id);
		Clazz clazz = GraphUtil.createClazzById(model, id);
		String type= cucumber.getTypeDictionary(id);
		if(type != null) {
			clazz.with(type);
		}
		return clazz;
	}
}
