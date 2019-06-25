package de.uniks.networkparser.logic;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class OCLParser implements ObjectCondition {
	/* context Meeting inv: self.end > self.start */
	public static final String INV = "inv:";
	private IdMap map;
	private SendableEntityCreator creator;
	private ObjectCondition inv;

	public static OCLParser create(CharSequence sequence, IdMap map) {
		OCLParser parser = new OCLParser();
		parser.withMap(map);

		if (sequence == null) {
			return parser;
		}
		CharacterBuffer buffer = new CharacterBuffer().with(sequence);
		if (buffer.startsWith("context", 0, true) == false) {
			return parser;
		}
		buffer.withPosition(7);
		buffer.trim();
		String className = buffer.nextString().toString();

		parser.withCreator(className);

		CharacterBuffer item = buffer.nextString();
		if (INV.equalsIgnoreCase(item.toString()) == false) {
			return parser;
		}
		parser.withInv(parser.parsingElement(buffer));
		return parser;
	}

	public OCLParser withInv(ObjectCondition parsingElement) {
		this.inv = parsingElement;
		return this;
	}

	private ObjectCondition parsingElement(CharacterBuffer buffer) {
		if (buffer == null) {
			return null;
		}
		CharacterBuffer item = buffer.nextString();
		ObjectCondition con = null;
		if (item.startsWith("self.")) {
			MapCondition condition = new MapCondition().withMap(map).withCreator(getCreator());
			String property = item.substring(5);
			condition.withProperty(property);

			item = buffer.nextString();

			Equals sub = new Equals();
			sub.withLeft(condition);
			if (item.equals("==")) {
				sub.withPosition(Equals.POS_EQUALS);
				item = buffer.nextString();
				sub.withRight(StringCondition.create(item));
				con = sub;
			} else if (item.equals(">")) {
				sub.withPosition(Equals.POS_LOWER);
				item = buffer.nextString();
				sub.withRight(StringCondition.create(item));
				con = sub;
			} else if (item.equals("<")) {
				sub.withPosition(Equals.POS_GREATER);
				item = buffer.nextString();
				sub.withRight(StringCondition.create(item));
				con = sub;
			} else {
				con = condition;
			}
		}
		return con;
	}

	private SendableEntityCreator getCreator() {
		return creator;
	}

	public OCLParser withMap(IdMap map) {
		this.map = map;
		return this;
	}

	public OCLParser withCreator(String className) {
		if (this.map != null && className != null) {
			className = EntityUtil.upFirstChar(className);
			this.creator = this.map.getCreator(className, false);
		}
		return this;
	}

	public OCLParser withCreator(SendableEntityCreator creator) {
		this.creator = creator;
		return this;
	}

	@Override
	public boolean update(Object value) {
		Object item;
		SimpleEvent event = null;
		if (value instanceof SimpleEvent) {
			event = (SimpleEvent) value;
			item = event.getSource();
		} else {
			item = value;
		}
		if (item == null) {
			return false;
		}
		if (creator == null) {
			return false;
		}
		if (inv == null) {
			return false;
		}
		if (event == null) {
			event = new SimpleEvent(item, "", map, creator);
		}
		return inv.update(event);
	}
}
