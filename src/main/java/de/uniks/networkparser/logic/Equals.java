package de.uniks.networkparser.logic;
import java.beans.PropertyChangeEvent;
import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleKeyValueList;
/**
 * @author Stefan Lindel Clazz of EqualsCondition
 */

public class Equals implements ParserCondition, SendableEntityCreator {
	/** Constant of KEY. */
	public static final String PROPERTY_KEY = "key";

	/** Constant of StrValue. */
	public static final String PROPERTY_VALUE = "value";
	/** Constant of Position. */
	public static final String PROPERTY_POSITION = "position";

	/** Variable of StrValue. */
	private String key;

	/** Variable of leftCondition. */
	private ObjectCondition left;

	/** Variable of leftCondition. */
	private ObjectCondition right;

	/** Variable of Value. */
	private Object value;

	/** Variable of ValueStrValue. */
	private Object delta;
	/**
	 * Variable of Position. Position of the Byte or -1 for currentPosition
	 */
	private int position = -1;

	private Object getValue(ObjectCondition condition, Object evt) {
		if(evt instanceof LocalisationInterface == false) {
			return null;
		}
		LocalisationInterface li = (LocalisationInterface) evt;
		if (condition instanceof ParserCondition) {
			return ((ParserCondition)condition).getValue(li);
		} else if (condition instanceof ChainCondition) {
			ChainCondition chainCondition = (ChainCondition) condition;
			Set<ObjectCondition> templates = chainCondition.getList();
			CharacterBuffer buffer=new CharacterBuffer();
			for(ObjectCondition item : templates) {
				if(item instanceof VariableCondition) {
					VariableCondition vc = (VariableCondition) item;
					Object result = vc.getValue(li);
					if(result != null) {
						buffer.with(result.toString());
					}
				} else {
					buffer.with(item.toString());
				}
			}
			return buffer.toString();
		}
		return null;
	}

	@Override
	public boolean update(Object evt) {
		if (evt == null) {
			return value == null;
		}
		if(evt instanceof LocalisationInterface && this.left != null && this.right != null) {

			Object leftValue = getValue(this.left, evt);
			Object rightValue = getValue(this.right, evt);

			if(leftValue == null) {
				return rightValue == null;
			}


			if(this.position !=0) {
				if(leftValue instanceof Number || EntityUtil.isNumeric(""+leftValue)) {
					if(rightValue instanceof Number || EntityUtil.isNumeric(""+rightValue)) {
						Double leftNumber = Double.valueOf(""+leftValue);
						Double rightNumber = Double.valueOf(""+rightValue);
						if(position>0) {
							return rightNumber>leftNumber;
						}
						return rightNumber<leftNumber;
					}
				}
			}
			if(leftValue instanceof String && rightValue instanceof String) {
				return ((String)leftValue).equalsIgnoreCase((String)rightValue);
			}
			if(leftValue instanceof Number && rightValue instanceof String) {
				leftValue = ""+leftValue;
			}
			return leftValue.equals(rightValue);
		}
		if(value == null) {
			return evt == null;
		}
		if(evt instanceof SimpleKeyValueList<?, ?>) {
			SimpleKeyValueList<?, ?> keyValueList = (SimpleKeyValueList<?, ?>) evt;
			Object value = keyValueList.get(this.key);
			if(value != null) {
				return value.equals(this.value);
			}
		}
		if((evt instanceof PropertyChangeEvent) == false) {
			if(value instanceof Number && evt instanceof Number) {
				// Check for Number
				if(value instanceof Byte
						|| value instanceof Short
						|| value instanceof Integer
						|| value instanceof Long) {
					if(delta == null) {
						return value == evt;
					}
					Long expValue = Long.valueOf(""+value);
					Long evtValue = Long.valueOf(""+evt);
					Long deltaValue = Long.valueOf(""+delta);
					return ((expValue - deltaValue) <= evtValue && (expValue + deltaValue)>= evtValue);
				}
				// FLOAT DOUBLE AND OTHER
				Double expValue = (Double)value;
				Double evtValue = (Double) evt;
				if(delta != null) {
					Double deltaValue = (Double) delta;
					return ((expValue - deltaValue) <= evtValue && (expValue + deltaValue)>= evtValue);
				}
				return expValue.equals(evtValue);
			}
			return value.equals(evt);
		}

		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		if (event.getSource() instanceof BufferedBuffer && value instanceof Byte) {
			Byte btrValue = (Byte) value;
			BufferedBuffer buffer = (BufferedBuffer) event.getSource();
			int pos;
			if (position < 0) {
				pos = buffer.position();
			} else {
				pos = position;
			}
			return buffer.byteAt(pos) == btrValue;
		}

		if(event.getPropertyName() == null) {
			return false;
		}
		return event.getPropertyName().equals(value);
	}

	/**
	 * @param value		The new Position
	 * @return 			Equals Instance
	 */
	public Equals withPosition(int value) {
		this.position = value;
		return this;
	}

	/**
	 * @return The Position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param value		The new StringValue
	 * @return 			Equals Instance
	 */
	public Equals withValue(Object value) {
		this.value = value;
		return this;
	}

	public Equals withValue(Object value, Object delta) {
		this.withValue(value);
		this.withDelta(delta);
		return this;
	}

	/** @return The StringVlaue */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		if(left != null && right != null) {
			return ""+left.toString() +"==" + right.toString();
		}
		return "==" + value + " ";
	}

	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_KEY, PROPERTY_VALUE, PROPERTY_POSITION};
	}

	@Override
	public ParserCondition getSendableInstance(boolean prototyp) {
		return new Equals();
	}

	public String getKey() {
		return key;
	}

	public Equals withKey(String key) {
		this.key = key;
		return this;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getKey();
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getValue();
		}
		if (PROPERTY_POSITION.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getPosition();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(entity instanceof Equals == false) {
			return false;
		}
		Equals element = (Equals) entity;
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			element.withKey(String.valueOf(value));
			return true;
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			element.withValue(value);
			return true;
		}
		if (PROPERTY_POSITION.equalsIgnoreCase(attribute)) {
			element.withPosition(Integer.parseInt("" + value));
			return true;
		}
		return false;
	}

	public Object getDelta() {
		return delta;
	}

	public Equals withDelta(Object delta) {
		this.delta = delta;
		return this;
	}

	public static Equals create(String key, Object value) {
		Equals condition = new Equals();
		condition.withKey(key);
		condition.withValue(value);
		return condition;
	}

	public static Equals createNullCondition() {
		return new Equals().withValue(null);
	}

	public Equals withLeft(ObjectCondition expression) {
		this.left = expression;
		return this;
	}
	public Equals withRight(ObjectCondition expression) {
		this.right = expression;
		return this;
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	//KEY LEFTVALUE
	//VALUE RIGHTVALUE
	@Override
	public Object getValue(LocalisationInterface value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			Object object = variables.getValue(variables, this.key);
			return object;
		}
		if(value != null && this.key != null) {
			return value.getText(this.key, null, null);
		}
		if(this.key == null) {
			return null;
		}
		if(this.value.equals(value)) {
			return value;
		}
		return null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
	}
}
