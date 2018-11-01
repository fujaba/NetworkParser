package de.uniks.networkparser.ext.javafx;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.util.List;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.gui.Dice;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class PointPaneController extends SendableItem implements PropertyChangeListener, SendableEntityCreator, ObjectCondition { 
//extends Pane 
	public static final String PROPERTY_GUI="gui";
	public static final String PROPERTY_CLICK="click";
	public static final String PROPERTY_VALUE="value";
	public static final String STOPPED="STOPPED";
	private Object pane;
	private SimpleList<Object> children = new SimpleList<Object>();
	private String color="BLACK";
	private int number;
	private Object timeline;
	private int max=6;
	private String style;
	private GUIEvent eventListener;
	private Object eventProxy;
	private SendableEntity model = new Dice();
	private double millis = 2000;
	private Class<?> circleClass=ReflectionLoader.getClass("javafx.scene.shape.Circle");
	private Class<?> mouseEventClass =ReflectionLoader.getClass("javafx.scene.input.MouseEvent");
	private Class<?> actionEventClass =ReflectionLoader.getClass("javafx.event.ActionEvent");

	public Object getTimeLine() {
		if(this.timeline != null) {
			return this.timeline; 
		}
		if(Os.isReflectionTest()) {
			return null;
		}
		this.timeline =ReflectionLoader.newInstance("javafx.animation.Timeline");
		return this.timeline;
	}
	
	public PointPaneController() {
		this.eventListener = new GUIEvent().withListener(this);
		this.eventProxy = ReflectionLoader.createProxy(eventListener, ReflectionLoader.EVENTHANDLER);

		model.addPropertyChangeListener(Dice.PROPERTY_VALUE, this);
	}

	public void setStyle(String value) {
		this.style = value;
	}
	
	public String getStyle() {
		return style;
	}

	public boolean init(Object value) {
		if(value == null) {
			return false;
		}
		if (ReflectionLoader.NODE.isAssignableFrom(value.getClass())) {
			this.pane = value;
			ReflectionLoader.call(this.pane, "setOnMouseClicked", ReflectionLoader.EVENTHANDLER, this.eventProxy);
		}
		ReflectionLoader.call(getTimeLine(), "setOnFinished", ReflectionLoader.EVENTHANDLER, this.eventProxy);
		return true;
	}

	public void throwDice() {
		showAnimation(EntityUtil.randInt(1, this.max));
	}

	public PointPaneController withValue(int number) {
		String value = ""+ReflectionLoader.call(getTimeLine(), "getStatus");
		if(STOPPED.equals(value)) {
			showNumber(number);
			fireEvent(number);
		}
		return this;
	}
	@SuppressWarnings("unchecked")
	public PointPaneController showAnimation(int number) {
		Double tX = (Double) ReflectionLoader.call(pane, "getTranslateX");
		Double tY = (Double) ReflectionLoader.call(pane, "getTranslateY");
		Double height = (Double) ReflectionLoader.call(pane, "getHeight");
		Double width = (Double) ReflectionLoader.call(pane, "getWidth");
		Object rotate = ReflectionLoader.newInstance("javafx.scene.transform.Rotate", double.class, 0, double.class, tX+width/2, double.class, tY+height/2);
//		Rotate(double angle, double pivotX, double pivotY) {
		List<Object> transforms = (List<Object>) ReflectionLoader.call(pane, "getTransforms");
		transforms.clear();
		transforms.add(rotate);

		Class<?> className = ReflectionLoader.getClass("javafx.beans.value.WritableValue");
		Class<?> keyFrameClass =ReflectionLoader.getClass("javafx.animation.KeyFrame");
		Class<?> keyValueClass =ReflectionLoader.getClass("javafx.animation.KeyValue");
		Class<?> keyValueClassArray = Array.newInstance(keyValueClass, 0).getClass();
		Class<?> durationClass =ReflectionLoader.getClass("javafx.util.Duration");

		Object maxMillis = ReflectionLoader.call(durationClass, "millis", double.class, millis);
		

		Object keyValue = ReflectionLoader.newInstance(keyValueClass, className, ReflectionLoader.call(rotate, "angleProperty"), Object.class, 360);
		Object animation = ReflectionLoader.newInstance(true, keyFrameClass, durationClass, maxMillis, keyValueClassArray, ReflectionLoader.newArray(keyValueClass, keyValue));

		SimpleList<Object> animations = new SimpleList<Object>();

		animations.clear();
		animations.add(animation);

		Object proxy = ReflectionLoader.createProxy(model, className);
		
		double count=100;
		int i=1;
		while(count<millis) {
			Object countMilli = ReflectionLoader.call(durationClass, "millis", double.class, count);
			keyValue = ReflectionLoader.newInstance(keyValueClass, className, proxy, Object.class, i);
			animation = ReflectionLoader.newInstance(keyFrameClass, durationClass, countMilli, keyValueClassArray, ReflectionLoader.newArray(keyValueClass, keyValue));
			animations.add(animation);
			if(i == this.max) {
				i=0;
			}
			i++;
			count+=100;
		}
		keyValue = ReflectionLoader.newInstance(keyValueClass, className, proxy, Object.class, number);
		animation = ReflectionLoader.newInstance(keyFrameClass, durationClass, maxMillis, 
				ReflectionLoader.EVENTHANDLER,
				this.eventProxy,
				keyValueClassArray, ReflectionLoader.newArray(keyValueClass, keyValue));

		
		animations.add(animation);

		// Run Animation
		String value = ""+ReflectionLoader.call(getTimeLine(), "getStatus");
		if(STOPPED.equals(value)) {
			List<Object> frameList = (List<Object>) ReflectionLoader.call(getTimeLine(), "getKeyFrames");
			frameList.clear();
			frameList.addAll(animations);
			ReflectionLoader.call(getTimeLine(), "playFromStart");
		}
		return this;
	}
	
	

	public void showNumber(int number) {
		this.reset();
		if (number == 1) {
			this.addCircle(2, 2);
		} else if (number == 2) {
			this.addCircle(1, 1, 3, 3);
		} else if (number == 3) {
			this.addCircle(1, 1, 2, 2, 3, 3);
		} else if (number == 4) {
			this.addCircle(1, 1, 1, 3, 3, 1, 3, 3);
		} else if (number == 5) {
			this.addCircle(1, 1, 1, 3, 2, 2, 3, 1, 3, 3);
		} else if (number == 6) {
			this.addCircle(1, 1, 1, 2, 1, 3, 3, 1, 3, 2, 3, 3);
		} else if (number == 7) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 2, 3, 1, 3, 2, 3, 3);
		} else if (number == 8) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 3, 3, 1, 3, 2, 3, 3);
		} else if (number == 9) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 2, 2, 3, 3, 1, 3, 2, 3, 3);
		}
	}
	
	private void fireEvent(int number) {
		int oldValue = this.number;
		this.number = number;
		firePropertyChange(PROPERTY_VALUE, oldValue, number);
	}

	public void addCircle(int... values) {
		if(values.length%2>0){
			return;
		}
		for(int i=0;i<values.length;i+=2){
			this.addCircle(getCircle(values[i], values[i+1]));
		}
	}

	@SuppressWarnings("unchecked")
	private void reset() {
		while (this.children.size() > 0) {
			Object circle = children.remove(0);
			List<Object> list = (List<Object>) ReflectionLoader.call(this.pane, "getChildren");
			list.remove(circle);
		}
	}

	@SuppressWarnings("unchecked")
	private void addCircle(Object circle) {
		if (circle != null && this.pane != null) {
			List<Object> list = (List<Object>) ReflectionLoader.call(this.pane, "getChildren");
			list.add(circle);
			this.children.add(circle);
		}
	}

	private Object getCircle(double x, double y) {
		if (this.pane == null) {
			return null;
		}
		
		double width = (Double) ReflectionLoader.call(this.pane, "getPrefWidth");
		Object circle = ReflectionLoader.newInstance(circleClass);
		
		Object paint = ReflectionLoader.call(ReflectionLoader.PAINT, "valueOf", String.class, getColor());
		ReflectionLoader.call(circle, "setFill", ReflectionLoader.PAINT, paint);

		ReflectionLoader.call(circle, "setRadius", double.class, width / 10);
		ReflectionLoader.call(circle, "setLayoutX", double.class, width / 4 * x);
		ReflectionLoader.call(circle, "setLayoutY", double.class, width / 4 * y);
		return circle;
	}

	public String getColor() {
		return color;
	}

	public PointPaneController withColor(String color) {
		this.color = color;
		return this;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null){
			int val = 0;
			if(evt.getNewValue() != null) {
				val=(Integer)evt.getNewValue();
			}
			this.withValue(val);
			showNumber( val);
		}
	}

	public void initPropertyChange(Object model, Object gui) {
	}

	public Object getPane() {
		return pane;
	}

	//AbstractModelController
	public boolean addListener(Object item, String property) {
		return addListener(item, property, this);
	}
//FIXME R
	public boolean addListener(Object item, String property, PropertyChangeListener listener) {
		if (item == null) {
			return false;
		}
		GenericCreator creator = new GenericCreator(item);
		Object result;
		if (property != null) {
			if (item instanceof SendableEntity) {
				((SendableEntity) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
			if (item instanceof PropertyChangeSupport) {
				((PropertyChangeSupport) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
			result =ReflectionLoader.calling(item, "addPropertyChangeListener", false, Boolean.TRUE, 
					String.class, property, java.beans.PropertyChangeListener.class, listener);
			if(result != null) {
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
		}
		result = ReflectionLoader.calling(item, "getPropertyChangeSupport", false, Boolean.TRUE);
		if(result instanceof PropertyChangeSupport) {
			PropertyChangeSupport pcs = (PropertyChangeSupport) result;
			if (property == null) {
				pcs.addPropertyChangeListener(listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, null));
			} else {
				pcs.addPropertyChangeListener(property, listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			}
			return true;
		}
		result =ReflectionLoader.calling(item, "addPropertyChangeListener", false, Boolean.TRUE, 
				java.beans.PropertyChangeListener.class, listener);
		if(result != null) {
			listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return true;
		}
		return false;
	}

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof PointPaneController == false) {
			return false;
		}
		PointPaneController controller = (PointPaneController) entity;
		if(PROPERTY_GUI.equalsIgnoreCase(attribute)) {
			controller.init(value);
			return true;
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PointPaneController();
	}

	@Override
	public boolean update(Object value) {
		if(mouseEventClass.isAssignableFrom(value.getClass())) {
			String status = ""+ReflectionLoader.call(this.getTimeLine(), "getStatus");
			if(STOPPED.equals(status)) {
				int point = EntityUtil.randInt(1, 6);
				showAnimation(point);
//				that.withValue(point);
			}
			firePropertyChange(PROPERTY_CLICK, null, number);
		}
		if(actionEventClass.isAssignableFrom(value.getClass())) {
			fireEvent(number);
		}
		return false;
	}
}
