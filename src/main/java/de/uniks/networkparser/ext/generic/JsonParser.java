package de.uniks.networkparser.ext.generic;
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;

public class JsonParser {
	private IdMap map;
	
	public IdMap getMap() {
		if(this.map == null) {
			this.map = new IdMap();
		}
		return map;
	}
	public <T> T decode(Object json) {
		return decode(json, null);
	}
	public <T> T decode(Object json, Class<T> classOfT) {
		JsonObject jsonObject = null;
		if(json instanceof String) {
			jsonObject = new JsonObject().withValue((String)json);
		} else if(json instanceof JsonObject) {
			jsonObject = (JsonObject) json;
		}
		if(jsonObject == null) {
			return null;
		}
		String className = jsonObject.getString(IdMap.CLASS);
		if(className == null || className.length()<1) {
			if(classOfT == null) {
				return null;
			}
			className = classOfT.getName(); 
			if(jsonObject.has(JsonTokener.PROPS) == false) {
				JsonObject obj = new JsonObject();
				obj.put(JsonTokener.PROPS, jsonObject);
				jsonObject = obj;
			}
			jsonObject.put(IdMap.CLASS, className);
		}
		IdMap map = getMap();
		GenericCreator.create(map, className);
		Object result = map.decode(jsonObject);
		if(classOfT == null) {
			try {
				@SuppressWarnings("unchecked")
				Class<?extends T> targetClass = (Class<? extends T>) Class.forName(className);
				if(targetClass == null) {
					return null;
				}
				return targetClass.cast(result);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return classOfT.cast(result);
		
	}
	public JsonObject encode(Object src) {
		if(src == null) {
			return new JsonObject();
		}
		IdMap map = getMap();
		GenericCreator.create(map, src.getClass());
		return map.toJsonObject(src);
	}
	public static <T> T fromJson(Object json) {
		return new JsonParser().decode(json);
	}
	public static <T> T fromJson(Object json, Class<T> classOfT) {
		return new JsonParser().decode(json, classOfT);
	}
	public static JsonObject toJson(Object src) {
		return new JsonParser().encode(src);
	}
}
