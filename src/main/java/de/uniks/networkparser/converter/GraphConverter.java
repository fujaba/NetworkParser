package de.uniks.networkparser.converter;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.ArrayList;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphEntity;
import de.uniks.networkparser.graph.GraphImage;
import de.uniks.networkparser.graph.GraphLabel;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphNode;
import de.uniks.networkparser.graph.GraphOptions;
import de.uniks.networkparser.graph.GraphPattern;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.ModifierSet;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.TemplateResultFragment;

public class GraphConverter implements Converter{
	public static final String TYPE = "type";
	public static final String ID = "id";

	public static final String NODE = "node";
	public static final String CLAZZ = "clazz";
	public static final String PATTERN = "pattern";
	public static final String SUBGRAPH = "subgraph";

	public static final String ATTRIBUTES = "attributes";
	public static final String METHODS = "methods";
	public static final String NODES = "nodes";
	public static final String LABEL = "label";
	public static final String EDGES = "edges";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String CARDINALITY = "cardinality";
	public static final String PROPERTY = "property";
	public static final String HEAD = "head";
	public static final String SRC = "src";
	public static final String OPTIONS = "options";
	private static final String STYLE = "style";
	private static final String INFO = "info";
	private static final String COUNTER = "counter";
	private static final String MODIFIERS = "modifiers";
	private static final String BODY = "body";
	private static final String PARAMETER = "parameter";

	private Entity factory = new JsonObject();
	private boolean full = false;


	public GraphList convertGraphList(String type, EntityList list) {
		GraphList root = new GraphList().withType(type);

		// Parse all Object to Object-Diagram
		for(int i=0;i<list.size();i++) {
			Object item = list.getChild(i);
//		for (Object item : list) {
			if (item instanceof Entity) {
				parseJsonObject(root, (Entity) item);
			}
		}
		return root;
	}

	public Entity convertToJson(String type, EntityList list,
			boolean removePackage) {
		GraphList root = convertGraphList(type, list);
		return convertToJson(root, removePackage, false);
	}

	public Clazz parseJsonObject(GraphList root, Entity node) {
		String id = node.getString("id");
		String typeId = id;
		boolean isClassDiagram = GraphTokener.CLASSDIAGRAM.equalsIgnoreCase(root.getType());

		if(isClassDiagram) {
			typeId = node.getString(CLAZZ);
			id = null;
		}
		Clazz graphNode = GraphUtil.getByObject(root, typeId, true);
		if (graphNode == null) {
			graphNode = new Clazz(node.getString(CLAZZ));
			if(id != null) {
				GraphUtil.setId(graphNode, id);
			}
			root.with(graphNode);
		}

		if (node.has(HEAD)) {
			GraphUtil.setGraphImage(graphNode, new GraphImage().with(node.getString(HEAD)));
		}

		if (node.has(JsonTokener.PROPS)) {
			Entity props = (Entity) node.getValue(JsonTokener.PROPS);
			Association assoc, assocOther;
			for (int i = 0; i < props.size(); i++) {
				Object value = props.getValueByIndex(i);
				if (value instanceof Entity) {
					assocOther = new Association(graphNode).with(Association.ONE).with(AssociationTypes.EDGE);
					// Must be a Link to 1
					Clazz newNode = parseJsonObject(root, (Entity) value);

					assoc = new Association(newNode).with(Association.ONE).with(props.getKeyByIndex(i)).with(AssociationTypes.ASSOCIATION);
					assoc.with(assocOther);

					GraphUtil.setAssociation(newNode, assoc);
					GraphUtil.setAssociation(graphNode, assocOther);
				} else if (value instanceof EntityList) {
					// Must be a Link to n
					EntityList array = (EntityList) value;
					Attribute attribute = null;

					for(int e=0; e < array.size();e++) {
						Object entity = array.getChild(e);
						if(entity == null) {
							continue;
						}
						if (entity instanceof Entity) {
							assocOther = new Association(graphNode).with(Association.ONE).with(AssociationTypes.EDGE);
							Clazz newNode = parseJsonObject(root, (Entity) entity);
							assoc = new Association(newNode).with(Association.MANY).with(props.getKeyByIndex(i)).with(AssociationTypes.ASSOCIATION);
							assoc.with(assocOther);

							GraphUtil.setAssociation(newNode, assoc);
							GraphUtil.setAssociation(graphNode, assocOther);

							if(isClassDiagram) {
								break;
							}
						} else {
							if(attribute == null) {
								//FIXME FOR ASSOC -- ATTRIBUTE
								String name = props.getKeyByIndex(i);
								DataType type = DataType.create(value.getClass().getName());
								attribute = new Attribute(name, type);
								attribute.withValue(entity.toString());
							} else {
								attribute.withValue(attribute.getValue(GraphTokener.OBJECTDIAGRAM, false) + "," + entity.toString());
							}
						}
					}
				}else {
					String name = props.getKeyByIndex(i);
					Attribute attribute;
					AssociationSet associations = graphNode.getAssociations();
					for(Association childAssoc : associations) {
						 if(name.equals(childAssoc.getName()) || name.equals(childAssoc.getOther().getName())) {
							 name = null;
							 break;
						 }
					}
					if(name == null) {
						continue;
					}
					if (value != null) {
						attribute = graphNode.createAttribute(name, DataType.create(value.getClass()));
						if(isClassDiagram == false) {
							attribute.withValue(value.toString());
						}
					} else {
						attribute = graphNode.createAttribute(name, null);
					}
				}
			}
		}
		return graphNode;
	}

	public Entity convertToJson(EntityList list, boolean removePackage) {
		return convertToJson(GraphTokener.OBJECTDIAGRAM, list, removePackage);
	}
	
	public Entity convertToJson(GraphModel root, boolean removePackage, boolean removeParameterNames) {
		String type = GraphTokener.CLASSDIAGRAM;
		String style = null;
		GraphOptions options = null;
		
		if(root instanceof GraphList) {
			GraphList graphList = (GraphList) root;
			type = graphList.getType();
			style = graphList.getStyle();
			options = graphList.getOptions();
		}
		Entity jsonRoot = (Entity) factory.getNewList(true);
		jsonRoot.put(TYPE, type);
		String id = root.getName();
		if(id != null && id.length()>0) {
			jsonRoot.put(ID, root.getName());
		}

		if(options != null) {
			jsonRoot.put(OPTIONS, options.getJson());
		}
		if(style!=null) {
			jsonRoot.put(STYLE, style);
		}
		jsonRoot.put(NODES, parseEntities(type, root, removePackage, removeParameterNames));
		EntityList parseEdges = parseEdges(type, root.getAssociations(), removePackage);
		if(parseEdges != null && parseEdges.sizeChildren()>0) {
			jsonRoot.put(EDGES, parseEdges);
		}
		return jsonRoot;
	}

	public GraphModel convertFromJson(Entity model, GraphModel reference) {
		if (model.has(NODES) == false) {
			return null;
		}
		EntityList nodes = (EntityList) model.getValue(NODES);
		if(reference == null) {
			reference = new GraphList().with(model.getString("package"));
		}
		reference.with(model.getString("package"));
		for (int i = 0; i < nodes.size(); i++) {
			Object item = nodes.getChild(i);
			if (item instanceof Entity) {
				Entity node = (Entity) item;
				Clazz clazz;
				if(node.has(LABEL)) {
					clazz = reference.createClazz(node.getString(LABEL));
				} else {
					clazz = reference.createClazz(node.getString(ID));
				}
				String type = node.getString(TYPE);
				if(type != null && type.length() > 0) {
					GraphUtil.setClazzType(clazz, type);
				}
				
				EntityList list = (EntityList) node.getValue(ATTRIBUTES);
				if(list != null) {
					for(int a=0;a<list.size();a++) {
						Object entity = list.getChild(a);
						if (entity instanceof String) {
							String attribute = (String) entity;
							int pos = attribute.indexOf(":");
							if (pos > 0) {
								clazz.createAttribute(attribute.substring(0, pos),
										DataType.create(attribute.substring(pos + 1)));
							}
						} else if(entity instanceof Entity) {
							Entity json = (Entity) entity;
							if(json.has(ID)) {
								Attribute attribute = clazz.createAttribute(json.getString(ID), DataType.create(json.getString(TYPE)));
								String string = json.getString(MODIFIERS);
								if(string != null && string.length()>0) {
									for(String modifier : string.split(" ")) {
										if(modifier.length()>0) {
											attribute.with(Modifier.create(modifier));
										}
									}
								}
							}
						}
					}
				}
				// All Methods
				list = (EntityList) node.getValue(METHODS);
				if(list != null) {
					for(int a=0;a<list.size();a++) {
						Object entity = list.getChild(a);
						if (entity instanceof String) {
						} else if(entity instanceof Entity) {
							Entity json = (Entity) entity;
							if(json.has(ID)) {
								Method method = clazz.createMethod(json.getString(ID));

								Object value = json.getValue(PARAMETER);
								if(value != null && value instanceof EntityList) {
									EntityList params = (EntityList) value;
									for(int p=0; p< params.sizeChildren();p++) {
										Object paramJson = params.getChild(p);
										if(paramJson != null && paramJson instanceof Entity) {
											Parameter param = new Parameter(DataType.create(((Entity) paramJson).getString(TYPE)));
											param.with(((Entity) paramJson).getString(ID));
											method.with(param);
										}
									}
								}
								method.with(DataType.create(json.getString(TYPE)));
								String string = json.getString(MODIFIERS);
								if(string != null && string.length()>0) {
									for(String modifier : string.split(" ")) {
										if(modifier.length()>0) {
											method.with(Modifier.create(modifier));
										}
									}
								}
								string = json.getString(BODY);
								if(string != null && string.length()>0) {
									method.withBody(string);
								}
							}
						}
					}
				}
			}
		}
		EntityList edges = (EntityList) model.getValue(EDGES);
		if(edges != null) {
			for(int e = 0;e<edges.size();e++) {
				Object entity = edges.getChild(e);
				if(entity instanceof Entity) {
					Entity edge = (Entity) entity;
					Entity source = (Entity) edge.getValue(SOURCE);
					Entity target = (Entity) edge.getValue(TARGET);
					if(source.has(CLAZZ) && target.has(CLAZZ)) {
						Association from = new Association(GraphUtil.getByObject(reference, source.getString(CLAZZ), true));
						Association to = new Association(GraphUtil.getByObject(reference, target.getString(CLAZZ), true));
						from.with(to);
						from.with(GraphUtil.createCardinality(source.getString(CARDINALITY)));
						to.with(GraphUtil.createCardinality(target.getString(CARDINALITY)));
						from.with(source.getString(PROPERTY));
						to.with(target.getString(PROPERTY));
						from.with(AssociationTypes.valueOf(source.getString(TYPE)));
						to.with(AssociationTypes.valueOf(target.getString(TYPE)));
					} else if(edge.getString(TYPE).equalsIgnoreCase("edge")) {
						Clazz fromClazz = GraphUtil.getByObject(reference, source.getString(ID), true);
						Clazz toClazz = GraphUtil.getByObject(reference, target.getString(ID), true);
						fromClazz.withBidirectional(toClazz, target.getString("property"), Association.ONE, source.getString("property"), Association.ONE);
					}
				}
			}
		}
		reference.fixClassModel();
		return reference;
	}

	private EntityList parseEdges(String type, SimpleSet<Association> edges,
			boolean shortName) {
		EntityList result = (EntityList) factory.getNewList(false);
		ArrayList<String> ids = new ArrayList<String>();

		for (Association edge : edges) {
			SimpleSet<GraphEntity> edgeNodes = GraphUtil.getNodes(edge);
			for (GraphEntity source : edgeNodes) {
				SimpleSet<GraphEntity> edgeOtherNodes = GraphUtil.getNodes(edge.getOther());
				for (GraphEntity target : edgeOtherNodes) {
					Entity child = parseEdge(type, source, target, edge, shortName, ids);
					if(child != null) {
						result.add(child);
					}
				}
			}
		}
		if(result.size()<1){
			return null;
		}
		return result;
	}

	private Entity parseEdge(String type, GraphEntity source, GraphEntity target, Association edge, boolean shortName,
			ArrayList<String> ids) {
		if (source instanceof Clazz && target instanceof Clazz) {
			return parseEdge(type, (Clazz) source, (Clazz) target, edge, shortName, ids);
		}
		if (source instanceof GraphPattern && target instanceof GraphPattern) {
			return parseEdge(type, (GraphPattern) source, (GraphPattern) target, edge, shortName, ids);
		}
		return null;
	}

	private Entity parseEdge(String type, Clazz source, Clazz target, Association edge, boolean shortName, ArrayList<String> ids) {
		Entity child = (Entity) factory.getNewList(true);
		child.put(TYPE, edge.getOther().getType());
		Entity sourceInfo = addInfo(edge, true);
		Entity targetInfo = addInfo(edge.getOther(), true);
		if (type.equals(GraphTokener.OBJECTDIAGRAM)) {
			sourceInfo.put(ID, source.getId() + " : " + source.getName(shortName));
			targetInfo.put(ID, target.getId() + " : " + target.getName(shortName));
			child.put(SOURCE, sourceInfo);
			child.put(TARGET, targetInfo);
			return child;
		}else{
			String id = new CharacterBuffer()
					.with(source.getName(false), ":", edge.getName(), 	"-", target.getName(false), ":",edge.getOther().getName()).toString();
			if (!ids.contains(id)) {
				Match diff = GraphUtil.getDifference(edge);
				if(diff != null && diff.getCount()>0) {
					child.put(COUNTER, diff.getCount());
				}
				sourceInfo.put(ID, source.getName(shortName));
				targetInfo.put(ID, target.getName(shortName));
				child.put(SOURCE, sourceInfo);
				child.put(TARGET, targetInfo);
				ids.add(id);
				return child;
			}
		}
		return null;
	}

	private Entity parseEdge(String type, GraphPattern source, GraphPattern target, Association edge, boolean shortName, ArrayList<String> ids) {
		Entity child = (Entity) factory.getNewList(true);
		child.put(TYPE, edge.getType());
		Entity sourceInfo = addInfo(edge, false);
		Entity targetInfo = addInfo(edge.getOther(), false);
		sourceInfo.put(ID, source.getId());
		targetInfo.put(ID, target.getId());
		
		child.put(SOURCE, sourceInfo);
		child.put(TARGET, targetInfo);

		GraphLabel info = edge.getInfo();
		if(info != null) {
			child.put(INFO, info.getName());
			child.put(STYLE, info.getStyle());
		}
		return child;
	}

	private Entity addInfo(Association edge, boolean cardinality) {
		Entity result = (Entity) factory.getNewList(true);
		result.put(PROPERTY, edge.getName());
		if(cardinality) {
			result.put(CARDINALITY, edge.getCardinality());
		}
		if(full) {
			result.put(TYPE, edge.getType().getValue());
			result.put(CLAZZ, edge.getClazz().getName());
		}
		return result;
	}

	public EntityList parseEntities(String type, GraphEntity nodes,
			boolean shortName, boolean removeParameterNames) {
		EntityList result = (EntityList) factory.getNewList(false);
		ArrayList<String> ids = new ArrayList<String>();
		GraphSimpleSet children = GraphUtil.getChildren(nodes);
		for (GraphMember entity : children) {
			Entity item = parseEntity(type, entity, shortName, removeParameterNames);
			if (item != null) {
				if (GraphTokener.CLASSDIAGRAM.equals(type) && item.has(ID)) {
					String key = item.getString(ID);
					if (ids.contains(key)) {
						continue;
					}
					ids.add(key);
				}
				result.add(item);
			}
		}
		if(result.size()<1){
			return null;
		}
		return result;
	}

	public Entity parseEntity(String type, GraphMember entity, boolean shortName, boolean removeParameterNames) {
		if (type == null) {
			type = GraphTokener.OBJECTDIAGRAM;
			if (entity.getName() == null) {
				type = GraphTokener.CLASSDIAGRAM;
			}
		}
		Entity item = (Entity) factory.getNewList(true);

		if(entity instanceof Clazz) {
//			item.put(TYPE, CLAZZ);
			Clazz clazz = (Clazz) entity;
			if(full) {
				item.put(MODIFIERS, clazz.getModifier());
			}
			item.put(TYPE, clazz.getType());
			if (type == GraphTokener.OBJECTDIAGRAM) {
				item.put(ID,
						clazz.getId() + " : " + clazz.getName(shortName));
			} else {
				item.put(ID, clazz.getName(shortName));
			}
		}else if(entity instanceof GraphPattern) {
			item.put(TYPE, PATTERN);
			String bounds = ((GraphPattern) entity).getBounds();
			if(bounds != null) {
				item.put(STYLE, bounds);
			}
			item.put(ID, entity.getName());
		}else if(entity instanceof GraphList) {
			return convertToJson((GraphList) entity, shortName, false);
		} else {
			item.put(TYPE, NODE);
		}
		

		if(entity instanceof GraphEntity) {
			parseGraphEntity((GraphEntity)entity, item, type, shortName, removeParameterNames);
			return item;
		}
		if(entity instanceof GraphNode) {
			item.put(ID, entity.getName());
			return item;
		}
		return null;
	}
	private void parseGraphEntity(GraphEntity entity, Entity item, String type, boolean shortName, boolean removeParameterNames) {
		GraphImage nodeHeader = getNodeHeader(entity);
		if (nodeHeader != null) {
			Entity header = (Entity) factory.getNewList(true);
			header.put(SRC, nodeHeader);
			item.put(HEAD, header);
		}
		EntityList items = parseAttributes(type, entity, shortName);
		if(items.size()>0){
			item.put(ATTRIBUTES, items);
		}
		items = parseMethods(entity, shortName, removeParameterNames);
		if(items.size()>0){
			item.put(METHODS, items);
		}
		Match diff = GraphUtil.getDifference(entity);
		if(diff != null && diff.getCount()>0) {
			item.put(COUNTER, diff.getCount());
		}
	}

	public GraphImage getNodeHeader(GraphEntity entity) {
		GraphSimpleSet children = GraphUtil.getChildren(entity);
		for (GraphMember member : children) {
			if (member instanceof GraphImage) {
				return (GraphImage) member;
			}
		}
		return null;
	}

	private EntityList parseAttributes(String type, GraphEntity list,
			boolean shortName) {
		EntityList result = (EntityList) factory.getNewList(false);
		String splitter = "";
		if (type.equals(GraphTokener.OBJECTDIAGRAM)) {
			splitter = "=";
		} else if (type.equals(GraphTokener.CLASSDIAGRAM)) {
			splitter = ":";
		}
		GraphSimpleSet children = GraphUtil.getChildren(list);
		for (GraphMember item : children) {
			if (!(item instanceof Attribute)) {
				continue;
			}
			Attribute attribute = (Attribute) item;
			String name = attribute.getName();
			if(name == null || name.length()<1) {
				continue;
			}
			if(full) {
				Entity json = (Entity) factory.getNewList(true);
				json.put(ID, name);
				json.put(MODIFIERS, attribute.getModifier());
				json.put(TYPE, attribute.getType().getName(true));
				result.add(json);
			}else {
				result.add(attribute.getName() + splitter + attribute.getValue(type, shortName));
			}
		}
		return result;
	}

	private EntityList parseMethods(GraphEntity list, boolean shortName, boolean removeParameterNames) {
		EntityList result = (EntityList) factory.getNewList(false);
		GraphSimpleSet children = GraphUtil.getChildren(list);
		for (GraphMember item : children) {
			if (item instanceof Method == false) {
				continue;
			}
			Method method = (Method) item;
			if (full) {
				Entity json = (Entity) factory.getNewList(true);
				json.put(ID, method.getName());
				json.put(TYPE, method.getReturnType().getName(true));	// RETURNTYPE
				json.put(MODIFIERS, method.getModifier());
				if(method.getBody() != null && method.getBody().length()>0) {
					json.put(BODY, method.getBody());
				}
				ParameterSet parameters = method.getParameters();
				if (parameters.size()>0) {
					EntityList paramList = (EntityList) factory.getNewList(false);
					for(Parameter parameter : parameters) {
						Entity param = (Entity) factory.getNewList(true);
						param.put(ID, parameter.getName());
						param.put(TYPE, parameter.getType().getName(true));
						paramList.add(param);
					}
					json.put(PARAMETER, paramList);
				}
				result.add(json);
			}else {
				result.add( method.getName(false, removeParameterNames));
			}
		}
		return result;
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof GraphModel) {
			return this.convertToJson((GraphModel)entity, false, false).toString();
		}
		return null;
	}
	public static Entity convertModel(GraphModel model) {
		GraphConverter converter = new GraphConverter();
		return converter.convertToJson(model, false, true);
	}

	public GraphConverter withFull(boolean value) {
		this.full = value;
		return this;
	}
	public TemplateResultFragment convertToAdvanced(TemplateResultFragment fragment ) {
		if(fragment == null) {
			return null;
		}
		GraphModel model = (GraphModel) fragment.getMember();
		GraphSimpleSet diffs = GraphUtil.getGraphDiff(null,  model);
		if(diffs.size() < 0) {
			return fragment;
		}
		SimpleKeyValueList<GraphMember, String> names = new SimpleKeyValueList<GraphMember, String>();
		if(fragment.isExpression()) {
			// Add Model
			names.add(model, "model");
		}

		for(int i=0;i<diffs.size();i++) {
			Match diff = (Match) diffs.get(i);
			GraphMember match = diff.getMatch();
			if(match == null) {
				continue;
			}
			String matchNameMatch = (String) names.getValue(match);
			String matchNameClazz = null;
			String type = null;
//			String type = diff.getType();
			if(diff.getNewValue() == null) {
				if(diff.getOldValue() != null) {
					type = SendableEntityCreator.REMOVE;
				}
			} else {
				if(diff.getOldValue() != null) {
					type = SendableEntityCreator.UPDATE;
				} else {
					type = SendableEntityCreator.NEW;
				}
			}
			
			Clazz clazz = match.getClazz();
			if(matchNameMatch == null) {
				// MUST BE CREATE
				matchNameClazz = (String) names.getValue(match);
				if(matchNameClazz == null) {
					matchNameClazz = getFreeName(names, clazz);
					fragment.withLine("#IMPORT "+matchNameClazz + " = model.createClazz(\""+clazz.getName()+"\");", Clazz.class);
//					if(match instanceof Clazz) {
//						continue;
//					}
					//TODO MISSING ATTRIBUTES OF CLAZZ
				}
				// SO DO NEW, UPDATE, REMOVE
				if(SendableEntityCreator.NEW.equalsIgnoreCase(type)) {
					// IST NEW ONE
					Object newValue = diff.getNewValue();
					if (newValue instanceof Attribute) {
						createMember(fragment, (Attribute)newValue, names, null);
						continue;
					}
					if (newValue instanceof Association) {
						createMember(fragment, (Association) newValue, names, null);
						continue;
					}
					if (newValue instanceof Method) {
						ModifierSet refModifier = GraphUtil.getModifier(new Method());
						createMember(fragment, (Method)newValue, names, refModifier);
					}
					continue;
				}
			}
			// SO CHECK
			Object newValue = diff.getNewValue();
			Object oldValue = diff.getOldValue();
			if(SendableEntityCreator.UPDATE.equalsIgnoreCase(type)) {
				// RENAME
				if(Clazz.PROPERTY_MODIFIERS.equalsIgnoreCase(diff.getType()) && newValue instanceof String) {
					newValue = fragment.replacing("#IMPORT.create(\""+ (String) newValue+"\")", Modifier.class.getName());
				}
				if (match instanceof Clazz) {
					if(Clazz.PROPERTY_TYPE.equalsIgnoreCase(diff.getType())) {
						fragment.withLine("#IMPORTA.setClazzType("+matchNameClazz+", #IMPORTB.TYPE_" + ((String) newValue).toUpperCase()+")", GraphUtil.class, Clazz.class);
					}
					if(Clazz.PROPERTY_SUPERCLAZZ.equalsIgnoreCase(diff.getType())) {
						if (oldValue == null) {
							String key = (String) names.getValue(newValue);
							if(key != null) {
								fragment.withLineString(matchNameMatch + ".withSuperClazz(\"" + key + "\");");
							} else {
								fragment.withLineString(matchNameMatch + ".withSuperClazz(\"model.getGenerator().findClazz(\"" + ((Clazz)newValue).getName(false) + "\"));");
							}
						}else {
							String key = (String) names.getValue(oldValue);
							if(key != null) {
								fragment.withLineString(matchNameMatch + ".withoutSuperClazz(\"" + key + "\");");
							} else {
								fragment.withLineString(matchNameMatch + ".withSuperClazz(\"model.getGenerator().findClazz(\"" + ((Clazz)oldValue).getName(false) + "\"));");
							}
						}
						continue;
					}
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + newValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findClazz(\""+clazz.getName()+"\", true).with(\"" + newValue + "\");");
					}
				} else if (match instanceof Attribute) {
					if(Clazz.PROPERTY_TYPE.equalsIgnoreCase(diff.getType())) {
						if(newValue instanceof DataType == false) {
							newValue = fragment.replacing("#IMPORT.create(\""+ newValue+"\")", DataType.class.getName());
						}
					}
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + newValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findAttribute("+matchNameClazz+", \""+match.getName()+"\", true).with(" + newValue + ");");
					}
				} else if (match instanceof Association) {
					if(Association.PROPERTY_CARDINALITY.equalsIgnoreCase(diff.getType()) && newValue instanceof String) {
						newValue = fragment.replacing(""+ newValue);
					}
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + newValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findAssociation(\""+matchNameClazz, clazz.getName()+"\", true).with(\"" + newValue + "\");");
					}
				} else if (match instanceof Method) {
					if(Clazz.PROPERTY_TYPE.equalsIgnoreCase(diff.getType())) {
						newValue = fragment.replacing("#IMPORT.create(\""+ newValue+"\")", DataType.class.getName());
					}
					if(Method.PROPERTY_PARAMETER.equalsIgnoreCase(diff.getType())) {
						// SO  Method
						if(matchNameMatch == null) {
							matchNameMatch = getFreeName(names, match);
							fragment.withLine("#IMPORT "+matchNameMatch + " = model.findMethod(" + matchNameClazz+", "+match.getName()+"\");", Method.class);
						}
						if(oldValue == null) {
							Parameter parameter = (Parameter) newValue;
							fragment.withLine(matchNameMatch+".createParameter(\"" + parameter.getName() + "\", " + parameter.getType() + ");", DataType.class);
						} else {
							Parameter parameter = (Parameter) oldValue;
							fragment.withLine(matchNameMatch+".remove(model.getGenerator().findParameter("+matchNameMatch+", \"" + parameter.getName() + "\"));");
						}
						continue;
					}
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + newValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findMethod (\""+matchNameClazz, clazz.getName()+"\", true).with(\"" + newValue + "\");");
					}
				}
			}
			if(SendableEntityCreator.REMOVE.equalsIgnoreCase(type)) {
				if(Clazz.PROPERTY_MODIFIERS.equalsIgnoreCase(diff.getType()) && oldValue instanceof String) {
					oldValue = fragment.replacing("#IMPORT.create(\""+ (String) oldValue +"\")", Modifier.class.getName());
				}
				if (match instanceof GraphModel) {
					fragment.withLineString("model.getGenerator().removeClazz(\"" + matchNameClazz + "\");");
				}
				if (match instanceof Clazz) {
					if(Clazz.PROPERTY_ATTRIBUTE.equalsIgnoreCase(diff.getType())) {
						fragment.withLineString(matchNameClazz+".without(model.getGenerator().findAttribute(" + matchNameClazz + ", \"" + ((GraphMember)oldValue).getName() + "));");
						continue;
					} else if(Clazz.PROPERTY_METHOD.equalsIgnoreCase(diff.getType())) {
						fragment.withLineString(matchNameClazz+".without(model.getGenerator().findMethod(" + matchNameClazz + ", \"" + ((GraphMember)oldValue).getName() + "));");
						continue;
					} else if(Clazz.PROPERTY_ASSOCIATION.equalsIgnoreCase(diff.getType())) {
						fragment.withLineString(matchNameClazz+".without(model.getGenerator().findAssociation(" + matchNameClazz + ", \"" + ((Association)oldValue).getOther().getName() + "));");
						continue;
					}
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".remove(\"" + oldValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findClazz(\""+clazz.getName()+"\", true).remove(\"" + newValue + "\");");
					}
				} else if (match instanceof Attribute) {
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + oldValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findAttribute(\""+matchNameClazz, clazz.getName()+"\", true).with(\"" + newValue + "\");");
					}
				} else if (match instanceof Method) {
					if(matchNameMatch != null) {
						fragment.withLineString(matchNameMatch+".with(\"" + oldValue + "\");");
					} else {
						fragment.withLineString("model.getGenerator().findMethod (\""+matchNameClazz, clazz.getName()+"\", true).with(\"" + newValue + "\");");
					}
				}
			}
		}
		return fragment;
	}

	public TemplateResultFragment convertToMetaText(GraphModel model, boolean full, boolean useImport) {
		TemplateResultFragment fragment = TemplateResultFragment.create(model, useImport, true);
		if(full == false) {
			return convertToAdvanced(fragment);
		}
		AssociationSet associations = new AssociationSet();
		AttributeSet attributes = new AttributeSet();

		ModifierSet refModifier = GraphUtil.getModifier(new Clazz(""));
		MethodSet methods = new MethodSet();
		AssociationSet superAssocs = new AssociationSet();
		SimpleKeyValueList<GraphMember, String> names = new SimpleKeyValueList<GraphMember, String>();
		// Add Model
		names.add(model, "model");
		ClazzSet clazzes = model.getClazzes();
		String name;
		String variable;

		for(int i = 0;i < clazzes.size(); i++) {
			Clazz clazz = clazzes.get(i);

			AttributeSet subAttr = clazz.getAttributes();
			MethodSet subMethod = clazz.getMethods();
			ModifierSet modifiers = this.getModifier(clazz, refModifier);
			boolean isVariable = subAttr.size()>0 || subMethod.size()>0 || modifiers.size()>0;
			attributes.addAll(subAttr);
			methods.addAll(subMethod);

			name = getFreeName(names, clazz);
			if(isVariable) {
				variable = "#IMPORT "+name+" = ";
			} else {
				variable = "";
			}
			String temp ="";
			if (clazz.getType().equals(Clazz.TYPE_INTERFACE)) {
				temp = ".enableInterface()";
			}
			fragment.withLine(variable + "model.createClazz(\""+clazz.getName()+"\")"+temp+";", Clazz.class);

			for(Modifier m : modifiers ) {
				fragment.withLine(name+".with(#IMPORT.create(\"" + m.getName() + "\"));", Modifier.class);
			}

			for (Association association : associations) {
				if(GraphUtil.isAssociation(association)) {
					associations.add(association);
				} else {
					superAssocs.add(association);
				}
			}

			clazzes.addAll(clazz.getSuperClazzes(false));
			clazzes.addAll(clazz.getInterfaces(false));
		}
		
		refModifier = GraphUtil.getModifier(new Attribute("", DataType.VOID));
		for (Attribute attribute : attributes) {
			createMember(fragment, attribute, names, refModifier);
		}

		for (Association assoc : associations) {
			createMember(fragment, assoc, names, null);
		}
		refModifier = GraphUtil.getModifier(new Method());
		for (Method method : methods) {
			createMember(fragment, method, names, refModifier);
		}
		String root = GraphUtil.getGenPath(model);
		if(root != null && root.isEmpty() == false) {
			fragment.withLine("model.generate(\""+root+"\");");
		} else {
			fragment.withLine("model.generate();");
		}
		return fragment;
	}
	
	private void createMember(TemplateResultFragment fragment, GraphMember member, SimpleKeyValueList<GraphMember, String> names, ModifierSet refModifier) {
		if(member instanceof Association) {
			Association assoc = (Association) member;
			String name = (String) names.getValue(assoc.getClazz());
			String otherName = (String)names.getValue(assoc.getOtherClazz());
			String card = ""+assoc.getOther().getCardinality();
			if(GraphUtil.isUndirectional(assoc)) {
				fragment.withLine(name+".createUniDirectional("+otherName.toLowerCase()+", \"" + otherName + "\"");
			} else {
				fragment.withLine(name+".createBidirectional(" + otherName.toLowerCase() + ", \"" + assoc.getOther().getName() + "\", #IMPORT."+card+ ", \"" + 
						assoc.getName() + "\"");
			}
		}
		if(member instanceof Method) {
			Method method = (Method) member;
			ModifierSet modifiers = this.getModifier(method, refModifier);
			String variable;

			String name = getFreeName(names, method);
			if(modifiers.size()>0) {
				variable = "#IMPORT "+name+" = ";
 			} else {
				variable = "";
			}
			String clazzName = (String) names.getValue(method.getClazz());
			CharacterBuffer paramsString = new CharacterBuffer();
			String split = ", ";
			for(Parameter param : method.getParameters()) {
				if(paramsString.isEmpty() == false) {
					paramsString.with(split);
				}
				paramsString.with("new #IMPORTB("+param.getType().toString(fragment.isUseImports())+")");
				if(param.getName() != null) {
					paramsString.with(".with(\"" + param.getName() + "\")");
				}
			}
			if(paramsString.isEmpty()) {
				split = "";
			}
			fragment.withLine(variable + clazzName + ".createMethod(\"" + method.getName() + "\", " + method.getReturnType().toString(fragment.isUseImports())+split+paramsString.toString()+");", Method.class, Parameter.class);
			for(Modifier m : modifiers) {
				fragment.withLine(name+".with(#IMPORT.create(\"" + m.getName() + "\"));", Modifier.class);
			}
		}
		if(member instanceof Attribute) {
			Attribute attribute = (Attribute) member;
			ModifierSet modifiers = this.getModifier(attribute, refModifier);
			String name = getFreeName(names, attribute);
			String variable;
			if(modifiers.size()>0) {
				variable = "#IMPORT "+name+" = ";
 			} else {
				variable = "";
			}
			String clazzName = (String) names.getValue(attribute.getClazz());
			fragment.withLine(variable + clazzName + ".createAttribute(\"" + attribute.getName() + "\", " + attribute.getType().toString(fragment.isUseImports()) + ");\n", Attribute.class);

			for(Modifier m : modifiers) {
				fragment.withLine(name+".with(#IMPORT.create(\"" + m.getName() + "\"));", Modifier.class);
			}
		}
	}

	private String getFreeName(SimpleKeyValueList<GraphMember, String> names, GraphMember member) {
		String value = member.getName().toLowerCase();
		if(names.containsValue(value) == false) {
			names.add(member, value);
			return value;
		}
		if(member instanceof Clazz == false) {
			// Search for Clazz
			String clazzName = (String) names.getValue(member.getClazz());
			value = clazzName + "_" + member.getName().toLowerCase();
			if(names.containsValue(value) == false) {
				names.add(member, value);
				return value;
			}
		}
		int i=1;
		String startValue = value;
		while(i<1000) {
			value = startValue + i;
			if(names.containsValue(value) == false) {
				names.add(member, value);
				return value;
			}
		}
		return null;
	}

	private ModifierSet getModifier(GraphMember owner, ModifierSet ref) {
		ModifierSet modifierSet = GraphUtil.getModifier(owner);
		for(int i=modifierSet.size() - 1;i>=0;i--) {
			if(ref.contains(modifierSet.get(i))) {
				modifierSet.remove(i);
			}
		}
		return modifierSet;
	}
}
