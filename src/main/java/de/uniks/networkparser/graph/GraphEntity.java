package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.Condition;

public abstract class GraphEntity extends GraphMember {
	public static final String PROPERTY_PACKAGENAME = "packageName";
	public static final String PROPERTY_EXTERNAL = "external";
	private boolean external;
	protected String id;

	public String getName(boolean shortName) {
		if (this.name == null) {
			return null;
		}
		if (shortName == false) {
			if (name.indexOf('.') < 0 && this.parentNode != null) {
				String parentName = ((GraphMember) this.parentNode).getName();
				if (parentName != null && parentName.isEmpty() == false) {
					return parentName + "." + name.replace("$", ".");
				}
			}
			return name.replace("$", ".");
		}
		if (name.endsWith("...")) {
			String realName = name.substring(0, name.length() - 3);
			int pos = realName.lastIndexOf(".");
			return name.substring(pos + 1);
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	protected boolean setId(String id) {
		if (id != this.id) {
			this.id = id;
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}

	protected String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphTokener.OBJECTDIAGRAM)) {
			return getId();
		} else if (typ.equals(GraphTokener.CLASSDIAGRAM)) {
			return getName(shortName);
		}
		return "";
	}

	/**
	 * get All Edges
	 * 
	 * @param type      Association types Edge for all Association for only Assocs
	 * @param otherType Other Association type
	 * @param filters   Can Filter the List of Associations
	 * @return all Associations of a Clazz
	 *
	 *         <pre>
	 * Clazz  --------------------- Associations
	 * one                          many
	 *         </pre>
	 */
	AssociationSet getEdges(AssociationTypes type, Condition<?>... filters) {
		if (this.children == null || type == null) {
			return AssociationSet.EMPTY_SET;
		}
		AssociationSet collection = new AssociationSet();
		if (this.children instanceof Association) {
			if (check((Association) this.children, filters)) {
				collection.add((Association) this.children);
			}
		} else if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Association) {
					Association assoc = (Association) item;
					if (check(assoc, filters)) {
						if (type == AssociationTypes.EDGE) {
							collection.add(assoc);
						} else if (type == AssociationTypes.ASSOCIATION) {
							if (AssociationTypes.isEdge(assoc.getType())) {
								collection.add(assoc);
							}
						} else if (type.equals(assoc.getType())) {
							collection.add(assoc);
						}
					}
				}
			}
		}
		return collection;
	}

	protected GraphMember getByObject(String clazz, boolean fullName) {
		if (clazz == null || children == null) {
			return null;
		}
		String sub = clazz;
		if (clazz.lastIndexOf(".") >= 0) {
			sub = clazz.substring(clazz.lastIndexOf(".") + 1);
		}
		String id;
		GraphSimpleSet collection = this.getChildren();
		for (GraphMember item : collection) {
			id = item.getFullId();
			if (clazz.equalsIgnoreCase(id) || sub.equalsIgnoreCase(id)) {
				return item;
			}
		}
		if (fullName || clazz.lastIndexOf(".") < 0) {
			return null;
		}
		for (GraphMember item : collection) {
			if (item instanceof Clazz) {
				id = ((Clazz) item).getId();
			} else {
				id = item.getName();
			}
			if (id.endsWith(clazz)) {
				return item;
			}
		}
		return null;
	}

	protected boolean isExternal() {
		return this.external;
	}

	protected GraphEntity withExternal(boolean value) {
		if (this.external != value) {
			this.external = value;
		}
		return this;
	}

	protected GraphEntity with(Association... values) {
		if (values != null) {
			boolean add;
			AssociationSet allAssoc;
			for (Association assoc : values) {
				/* Do Nothing */
				if (assoc == null || assoc.getOther() == null) {
					continue;
				}
				add = true;

				/* If Nessesarry to search Assoc_Own - Otherclazz_Property */

				Association assocOther = assoc.getOther();
				boolean mergeFlag = (assoc.getType() == AssociationTypes.ASSOCIATION
						&& assocOther.getType() == AssociationTypes.EDGE)
						|| (assoc.getType() == AssociationTypes.EDGE
								&& assocOther.getType() == AssociationTypes.ASSOCIATION);
				boolean generalizationFlag = (assoc.getType() == AssociationTypes.GENERALISATION
						&& assocOther.getType() == AssociationTypes.EDGE)
						|| (assoc.getType() == AssociationTypes.EDGE
								&& assocOther.getType() == AssociationTypes.GENERALISATION);
				if (generalizationFlag) {
					allAssoc = this.getEdges(AssociationTypes.GENERALISATION);
				} else {
					allAssoc = this.getEdges(AssociationTypes.ASSOCIATION);
				}
				for (Association item : allAssoc) {
					if (item == assoc || item.getOther() == assoc) {
						/* I Know the Assoc */
						add = false;
						break;
					}
					/* Implements new Search for Association Only Search for duplicate */
					Association itemOther = item.getOther();
					String name = itemOther.name();
					if (generalizationFlag && item.getClazz() == assoc.getClazz()) {
						break;
					}
					if (name != null && name.equals(assocOther.name())
							&& itemOther.getClazz() == assocOther.getClazz()) {
						if (item != assoc) {
							add = false;
						}
						break;
					}
					/* Check for Merge Association */
					if (mergeFlag) {
						if (itemOther.getClazz() == assocOther.getClazz() && item.getClazz() == assoc.getClazz()) {
							add = false;
							if (assocOther.name() != null && assoc.name() == null) {
								if (itemOther.getType() == AssociationTypes.EDGE
										&& item.getType() == AssociationTypes.ASSOCIATION) {
									itemOther.with(AssociationTypes.ASSOCIATION);
									itemOther.with(assocOther.getName());
									itemOther.with(assocOther.getCardinality());
									GraphMember attribute = itemOther.getClazz().getChildByName(assocOther.getName(),
											Attribute.class);
									if (attribute != null) {
										itemOther.getClazz().remove(attribute);
									}
								}

							} else if (item.getType() == AssociationTypes.EDGE
									&& itemOther.getType() == AssociationTypes.ASSOCIATION) {
								/* Cool its Bidirectional but remove Attributes */
								item.with(AssociationTypes.ASSOCIATION);
								item.with(assoc.getName());
								item.with(assoc.getCardinality());

								GraphMember attribute = item.getClazz().getChildByName(assoc.getName(),
										Attribute.class);
								if (attribute != null) {
									item.getClazz().remove(attribute);
								}
							}
							break;
						}
					}
				}
				if (add) {
					/* ADD TO PARENT MAY BE LIST */
					if (this.parentNode != null) {
						if (this.parentNode instanceof GraphModel) {
							((GraphModel) this.parentNode).with(assoc);
						}
					}
					if (this.children == null) {
						this.children = assoc;
					} else {
						GraphSimpleSet list;
						if (this.children instanceof GraphSimpleSet) {
							list = (GraphSimpleSet) this.children;
							list.add(assoc);
						} else {
							list = new GraphSimpleSet();
							list.with((GraphMember) this.children);
							this.children = list;
							list.add(assoc);
						}
					}
				}
			}
		}
		return this;
	}

	public GraphMember getChildByName(String name, Class<?> subClass) {
		if (this.children == null) {
			return null;
		}
		GraphSimpleSet children = this.getChildren();
		String itemName;
		for (GraphMember item : children) {
			if (item instanceof Association) {
				Association assoc = (Association) item;
				itemName = assoc.getOther().name();
			} else {
				itemName = item.getName();
			}
			if (itemName != null && itemName.equals(name)) {
				if (subClass != null && subClass == item.getClass()) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * get all Associations
	 * 
	 * @param filters Can Filter the List of Attributes
	 * @return all Attributes of a Clazz
	 *
	 *         <pre>
	 * Clazz  --------------------- Association
	 * one                          many
	 *         </pre>
	 */
	public AssociationSet getAssociations(Condition<?>... filters) {
		return getEdges(AssociationTypes.EDGE, filters);
	}

	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public GraphEntity with(Annotation value) {
		withAnnotation(value);
		return this;
	}

	protected GraphEntity with(ModifyEntry modifier) {
		super.withChildren(modifier);
		return this;
	}

	public Object getValue(String attribute) {
		if (attribute == null) {
			return null;
		}
		if (PROPERTY_EXTERNAL.equalsIgnoreCase(attribute)) {
			return isExternal();
		}
		return super.getValue(attribute);
	}

	@Override
	protected String getFullId() {
		if (this.id != null) {
			return this.id + " : " + super.getFullId();
		}
		return super.getFullId();
	}
}
