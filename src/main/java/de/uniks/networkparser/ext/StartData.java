package de.uniks.networkparser.ext;

import java.util.Set;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;

public class StartData implements SendableEntityCreatorNoIndex
{
	public static final String PROPERTY_EDITABLE="editable";
	private final static StartData  instance = new StartData();
	private static String[] attribute = null;
	private static SimpleList<StartElement> properties=new SimpleList<StartElement>();
	private static boolean editable=true;
	private static String fileName = "config.json";
	private static String NULLVALUE = "null";
	
	public StartData() {
		
	}
	
	
	public static boolean setFileName(String value) {
		if(value != null && value != fileName) {
			StartData.fileName = value;
			return true;
		}
		return false;
	}
	
	public static StartData instance() {
		return instance;
	}
	
	public static boolean addParameter(String key, String label, String description, Object values) {
		if(PROPERTY_EDITABLE.equals(key)) {
			return false;
		}
		StartElement startElement = new StartElement();
		startElement.withDescription(description);
		startElement.withLabel(label);
		startElement.withKey(key);
		startElement.withDefaultValues(values);
		boolean success =  StartData.properties.add(startElement);
		if(success) {
			StartData.attribute=null;
		}
		return success;
	}
	
	public static boolean addParameter(String key, Object value) {
		if(PROPERTY_EDITABLE.equals(key)) {
			if(value instanceof Boolean) {
				StartData.editable = (boolean) value;
				return true; 
			}
			return false;
		}
		StartElement startElement = new StartElement();
		startElement.withKey(key);
		startElement.withValue(value);
		boolean success =  StartData.properties.add(startElement);
		if(success) {
			StartData.attribute=null;
		}
		return success;
	}
	
	public static boolean has(String key) {
		if(key == null) {
			return false;
		}
		for(StartElement item : properties) {
			if(key.equalsIgnoreCase(item.getKey())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isEditable() {
		return editable;
	}
	
	public static String getString(String key) {
		if(key == null) {
			return null;
		}
		for(StartElement item : properties) {
			if(key.equalsIgnoreCase(item.getKey())) {
				Object result = item.getValue();
				if(result  instanceof String) {
					return (String) result;
				}
				return ""+result;
			}
		}
		return null;
	}

	public static Integer getInteger(String key) {
		if(key == null) {
			return null;
		}
		for(StartElement item : properties) {
			if(key.equalsIgnoreCase(item.getKey())) {
				Object result = item.getValue();
				if(result  instanceof Integer) {
					return (Integer) result;
				}
				return Integer.valueOf(""+result);
			}
		}
		return null;
	}

	public static boolean setValue(String key, Object value) {
		if(PROPERTY_EDITABLE.equals(key)) {
			if(value instanceof Boolean) {
				StartData.editable = (boolean) value;
				return true; 
			}
			return false;
		}
		for(StartElement item : properties) {
			if(key.equalsIgnoreCase(item.getKey())) {
				if(NULLVALUE.equals(value)) {
					item.withValue(null);
				}else {
					item.withValue(value);
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String[] getProperties() {
		if(StartData.attribute == null) {
			StartData.attribute = new String[StartData.properties.size()+1];
			attribute[0] = PROPERTY_EDITABLE;
			int i=1;
			for(StartElement item : properties) {
				StartData.attribute[i++] = item.getKey();
			}
		}
		return attribute;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new StartData();
	}
	
	public static SimpleList<StartElement> getElements() {
		return properties;
	}

	@Override
	public Object getValue(Object entity, String attrName) {
		if(attrName == null || entity instanceof StartData == false) {
			return null;
		}
		if(PROPERTY_EDITABLE.equalsIgnoreCase(attrName)) {
			return isEditable();
		}
		SimpleList<StartElement> properties = StartData.getElements();
		for(StartElement item : properties) {
			if(attrName.equalsIgnoreCase(item.getKey())) {
				Object value = item.getValue();
				if(NULLVALUE.equals(value)) {
					return "";
				}
				return value;
			}
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attrName, Object value, String type) {
		if(attrName == null || entity instanceof StartData == false) {
			return false;
		}
		if(PROPERTY_EDITABLE.equalsIgnoreCase(attrName)) {
			StartData.editable = (Boolean)value;
			return true;
		}
		for(StartElement item : properties) {
			if(attrName.equalsIgnoreCase(item.getKey())) {
				if(NULLVALUE.equals(value)) {
					item.withValue(null);
				}else {
					item.withValue(value);
				}
				return true;
			}
		}
		// Not Found Create it
		System.out.println(attrName+" not in Config-File");
		return StartData.addParameter(attrName, value);
	}
	
	public static boolean save() {
		StartData startData = StartData.instance();
		if(startData.size() <1) {
			// its only the editableFlag not a value
			return false;
		}
		IdMap map = new IdMap();
		map.with(startData);
		JsonObject config = map.toJsonObject(startData, Filter.createFull());
		return FileBuffer.writeFile(StartData.fileName, config.toString(2));
	}
	
	public int size() {
		return StartData.properties.size();
	}
	
	public static boolean load() {
		IdMap map = new IdMap();
		map.with(StartData.instance());
		CharacterBuffer readFile = FileBuffer.readFile(StartData.fileName);
		JsonObject json = new JsonObject().withValue(readFile);
		// Merge Properties from File and Properties from StartData
		Set<String> keySet = json.keySet();
		for(String key : keySet) {
			if(IdMap.CLASS.equals(key)) {
				continue;
			}
			if(has(key) == false) {
				addParameter(key, null);
			}
		}
		return map.decode(json, StartData.instance(), null) != null;
	}


	public static boolean isAutoStart() {
		return StartData.isEditable() == false;
	}
}
