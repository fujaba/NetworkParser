package de.uniks.networkparser.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.PatternCondition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class Pattern.
 *
 * @author Stefan
 */
public class Pattern implements Iterator<Object>, Iterable<Object>, ObjectCondition {
	
	/** The Constant MODIFIER_SEARCH. */
	public static final String MODIFIER_SEARCH = "search";
	
	/** The Constant MODIFIER_CHANGE. */
	public static final String MODIFIER_CHANGE = "change";
	
	/** The Constant MODIFIER_ADD. */
	public static final String MODIFIER_ADD = "add";
	
	/** The Constant MODIFIER_REMOVE. */
	public static final String MODIFIER_REMOVE = "remove";
	
	/** The Constant MODIFIER_ROLE. */
	public static final String MODIFIER_ROLE = "role";
	private Object match;
	private String modifier = MODIFIER_SEARCH;

	private SimpleSet<Pattern> children;
	private Pattern parent;
	private SimpleSet<Object> candidates = null;
	private ObjectCondition condition;
	private IdMap map;
	private SimpleIterator<Object> iterator;
	private SimpleSet<Pattern> chain;

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public Pattern getRoot() {
		if (this.getParent() != null) {
			return this.getParent().getRoot();
		}
		return this;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public IdMap getMap() {
		return getRoot().map;
	}

	/**
	 * Instantiates a new pattern.
	 *
	 * @param map the map
	 * @param match the match
	 */
	public Pattern(IdMap map, Object match) {
		this();
		this.match = match;
		this.map = map;
		if (candidates == null) {
			candidates = new SimpleSet<Object>();
		}
		this.candidates.add(match);
	}
	
	/**
	 * With condition.
	 *
	 * @param condition the condition
	 * @return the pattern
	 */
	public Pattern withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}

	/**
	 * Instantiates a new pattern.
	 */
	public Pattern() {
		this.chain = new SimpleSet<Pattern>();
		this.chain.add(this);
	}

	/**
	 * Instantiates a new pattern.
	 *
	 * @param parent the parent
	 * @param condition the condition
	 */
	public Pattern(Pattern parent, ObjectCondition condition) {
		if (parent != null) {
			this.parent = parent;
			parent.addToChain(this);
		}
		this.condition = condition;
	}

	/**
	 * Checks for.
	 *
	 * @param property the property
	 * @return the pattern
	 */
	public Pattern has(String property) {
		return has(PatternCondition.create(property));
	}

	/**
	 * Checks for.
	 *
	 * @param condition the condition
	 * @return the pattern
	 */
	public Pattern has(ObjectCondition condition) {
		Pattern root = getRoot();
		if (root != this && this.condition == null) {
			this.condition = condition;
			return this;
		}
		Pattern subPattern = new Pattern(this, condition);
		if (children == null) {
			children = new SimpleSet<Pattern>();
		}
		this.children.add(subPattern);
		if (MODIFIER_SEARCH.equals(this.modifier)) {
			subPattern.find();
		}
		return subPattern;
	}

	private boolean addToChain(Pattern value) {
		SimpleSet<Pattern> myChain = getChain();
		if (myChain != null) {
			return myChain.add(value);
		}
		return false;
	}

	/**
	 * Creates the list of type.
	 *
	 * @param <T> the generic type
	 * @param type the type
	 * @return the simple list
	 */
	public static <T> SimpleList<T> createListOfType(Class<T> type) {
		return new SimpleList<T>();
	}

	/**
	 * All matches.
	 *
	 * @param <ST> the generic type
	 * @return the st
	 */
	@SuppressWarnings("unchecked")
	public <ST extends List<Object>> ST allMatches() {
		if (this.match == null) {
			find();
		}
		if (match == null) {
			return (ST) new SimpleList<Object>();
		}
		SimpleList<? extends Object> result = createListOfType(match.getClass());
		while (find()) {
			result.add(this.match);
		}
		return (ST) result;
	}

	/**
	 * Gets the match.
	 *
	 * @return the match
	 */
	public Object getMatch() {
		return match;
	}

	/**
	 * Gets the match.
	 *
	 * @param <ST> the generic type
	 * @param clazz the clazz
	 * @return the match
	 */
	@SuppressWarnings("unchecked")
	public <ST extends Object> ST getMatch(Class<ST> clazz) {
		return (ST) match;
	}
	
	/**
	 * Find.
	 *
	 * @param condition the condition
	 * @return true, if successful
	 */
	public boolean find(Object condition) {
		this.withCandidates(condition);
		return find();
	}

	/**
	 * Find.
	 *
	 * @return true, if successful
	 */
	public boolean find() {
		SimpleSet<Pattern> chain = getChain();
		if (chain == null) {
			return false;
		}
		Pattern last = chain.last();
		if (last == this) {
			return finding(true);
		}
		return last.finding(true);
	}

	/**
	 * Gets the chain.
	 *
	 * @return the chain
	 */
	public SimpleSet<Pattern> getChain() {
		return getRoot().chain;
	}

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasNext() {
		SimpleSet<Pattern> chain = getChain();
		if (chain == null) {
			return false;
		}
		Pattern last = chain.last();
		if (last == this) {
			return finding(false);
		}
		return last.finding(false);
	}

	private boolean findCondition(String modifier) {
		if(condition == null) {
			return false;
		}
		if(condition instanceof Pattern) {
			return condition.update(this);
		}
		if(MODIFIER_ROLE.equalsIgnoreCase(modifier)) {
			SimpleIterator<Object> iterator = getIterator();
			if(iterator != null) {
				PatternEvent event = new PatternEvent(this, MODIFIER_SEARCH);
				while(iterator.hasNext()) {
					Object candidate = iterator.next();
					event.setCandidate(candidate);
					if(condition.update(event)) {
						return true;
					}
				}
			}
		}
		return condition.update(this);
	}
	
	private boolean finding(boolean save) {
		/* Backwards */
		
		if (!findCondition(null)) {
			/* Not found */
			if (parent == null) {
				return false; 
			}
			boolean finding = parent.finding(save);
			if (save) {
				this.candidates = null;
				this.match = null;
				this.iterator = null;
				if (condition != null) {
					findCondition(MODIFIER_ROLE);
				}
			}

			if (!finding) {
				return this.match != null;
			}
			if (children != null) {
				for (Pattern child : children) {
					finding = child.find();
					if (!finding) {
						break;
					}
				}
				if (!finding) {
					return false;
				}
			}
		}
		if (save) {
			this.match = iterator.current();
			applyPattern();
		}
		return this.match != null;
	}

	/**
	 * Next.
	 *
	 * @return the object
	 */
	@Override
	public Object next() {
		if (find()) {
			return getMatch();
		}
		return null;
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<Object> iterator() {
		return this;
	}

	/**
	 * Gets the candidates.
	 *
	 * @return the candidates
	 */
	public SimpleSet<Object> getCandidates() {
		return candidates;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public Pattern getParent() {
		return parent;
	}

	/**
	 * With candidates.
	 *
	 * @param newValue the new value
	 * @return the pattern
	 */
	public Pattern withCandidates(Object newValue) {
		if (this.candidates == null) {
			this.candidates = new SimpleSet<Object>();
			this.iterator = (SimpleIterator<Object>) this.candidates.iterator();
			this.iterator.withCheckPointer(false);
		}
		if (newValue instanceof Collection<?>) {
			this.candidates.withList((Collection<?>) newValue);
		} else {
			this.candidates.with(newValue);
		}
		return this;
	}

	/**
	 * Gets the iterator.
	 *
	 * @return the iterator
	 */
	public SimpleIterator<Object> getIterator() {
		return iterator;
	}

	/**
	 * With match.
	 *
	 * @param candidate the candidate
	 * @return the pattern
	 */
	public Pattern withMatch(Object candidate) {
		this.match = candidate;
		return this;
	}

	/**
	 * Apply pattern.
	 *
	 * @return true, if successful
	 */
	public boolean applyPattern() {
		if (MODIFIER_SEARCH.equals(this.modifier)) {
			return true;
		}
		/* Go throw all Matches */
		SimpleSet<Pattern> chain = getChain();

		/* FROM LAST TO FIRST */
		for (int i = 0; i < chain.size(); i++) {
			Pattern pattern = chain.get(i);
			pattern.appling();
		}
		return true;
	}

	/**
	 * Appling.
	 *
	 * @return true, if successful
	 */
	public boolean appling() {
		if (!(condition instanceof PatternCondition)) {
			return false;
		}
		PatternCondition patternCondition = (PatternCondition) condition;
		if (MODIFIER_ADD.equals(this.modifier)) {
			if (this.match == null) {
				return false;
			}
			Object value = patternCondition.getValue();
			String clazzName = "" + value;
			SendableEntityCreator creatorClass = getMap().getCreator(clazzName, true);
			if (creatorClass != null) {
				this.match = creatorClass.getSendableInstance(false);
				if (this.parent != null) {
					this.parent.setValue(patternCondition.getLinkName(), this.match);
				}
			}
			return true;
		}
		if (MODIFIER_CHANGE.equals(this.modifier)) {
			if (this.parent != null) {
				this.parent.setValue(patternCondition.getLinkName(), patternCondition.getValue());
			}
			return true;
		}
		if (MODIFIER_REMOVE.equals(this.modifier)) {
			SendableEntityCreator creatorClass = getMap().getCreatorClass(this.match);
			if (creatorClass instanceof SendableEntityCreator) {
				((SendableEntityCreator) creatorClass).setValue(this.match, null, null,
						SendableEntityCreator.REMOVE_YOU);
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the value.
	 *
	 * @param property the property
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String property, Object value) {
		if (this.match != null) {
			SendableEntityCreator creatorClass = getMap().getCreatorClass(this.match);
			creatorClass.setValue(this.match, property, value, SendableEntityCreator.NEW);
		}
		return false;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if(value == null) {
			return false;
		}
		 if(value instanceof Clazz) {
			return false;
		} 
		Object item = null;
		if(value instanceof SimpleEvent) {
			String linkName;
			SimpleEvent event = (SimpleEvent) value;
			item = event.getNewValue();
			PatternEvent pe = (PatternEvent) event;
			Object id = event.getPropagationId();
			if(id instanceof String) {
				linkName = (String) id;
			}else {
				linkName = event.getPropertyName();
				}
			IdMap map = this.getMap();
			SendableEntityCreator creator = map.getCreatorClass(item);
			if (creator != null) {
				Object newValue = creator.getValue(item, linkName);
				pe.addCandidate(newValue);
				return true;
			}
		}else {
			item = value;
		}
		return item != null;
	}

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the pattern
	 */
	public Pattern withMap(IdMap map) {
		this.map = map;
		return this;
	}
}
