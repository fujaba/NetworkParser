package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.Iterator;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.NoIndexCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class Grammar {
	/**
	 * @param jsonObject
	 * @return the props of theJsonObject
	 */
	public JsonObject getJsonObjectProperties(JsonObject jsonObject, IdMap map) {
		if (jsonObject.has(JsonIdMap.JSON_PROPS)) {
			return jsonObject.getJsonObject(JsonIdMap.JSON_PROPS);
		}
		return null;
	}

	/**
	 * @param jsonObject
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getJsonObjectCreator(JsonObject jsonObject,
			IdMap map) {
		Object className = jsonObject.get(JsonIdMap.CLASS);
		return map.getCreatorClasses((String) className);
	}

	/**
	 * @param jsonObject
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getObjectCreator(Object modelItem,
			String className, IdMap map) {
		return map.getCreatorClasses(className);
	}

	public JsonObject getJsonObject(IdMap map, SendableEntityCreator prototyp,
			String className, String id, JsonObject jsonProp, Filter filter) {
		JsonObject json = new JsonObject();
		if (prototyp instanceof NoIndexCreator) {
			Iterator<String> keys = jsonProp.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				json.put(key, jsonProp.get(key));
			}
			json.put(JsonIdMap.CLASS, className);
			return json;
		}
		if (filter.isId(map, jsonProp, className)) {
			json.put(IdMap.ID, id);
		}
		json.put(JsonIdMap.CLASS, className);

		if (jsonProp.size() > 0) {
			json.put(JsonIdMap.JSON_PROPS, jsonProp);
		}
		return json;
	}
	
	public boolean hasValue(JsonObject json, String property){
		return json.has(property);
	}
	public String getValue(JsonObject json, String property){
		return json.getString(property);
	}
}
