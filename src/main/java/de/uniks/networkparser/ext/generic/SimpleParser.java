package de.uniks.networkparser.ext.generic;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

/**
 * The Class SimpleParser.
 *
 * @author Stefan
 */
public class SimpleParser {
	private IdMap map;
	private Tokener tokener;
	private char endTag;

	/**
	 * Gets the tokener.
	 *
	 * @return the tokener
	 */
	public Tokener getTokener() {
		if (this.tokener == null) {
			this.tokener = new JsonTokener();
		}
		return tokener;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public IdMap getMap() {
		if (this.map == null) {
			this.map = new IdMap();
		}
		return map;
	}

	/**
	 * Decode.
	 *
	 * @param <T> the generic type
	 * @param json the json
	 * @return the t
	 */
	public <T> T decode(Object json) {
		return decode(json, null);
	}

	/**
	 * Decode.
	 *
	 * @param <T> the generic type
	 * @param json the json
	 * @param classOfT the class of T
	 * @return the t
	 */
	public <T> T decode(Object json, Class<T> classOfT) {
		JsonObject jsonObject = null;
		if (json instanceof String) {
			jsonObject = new JsonObject().withValue((String) json);
		} else if (json instanceof JsonObject) {
			jsonObject = (JsonObject) json;
		}
		if (jsonObject == null) {
			return null;
		}
		String className = jsonObject.getString(IdMap.CLASS);
		if (className == null || className.length() < 1) {
			if (classOfT == null) {
				return null;
			}
			className = classOfT.getName();
			if (!jsonObject.has(JsonTokener.PROPS)) {
				JsonObject obj = new JsonObject();
				obj.put(JsonTokener.PROPS, jsonObject);
				jsonObject = obj;
			}
			jsonObject.put(IdMap.CLASS, className);
		}
		IdMap map = getMap();
		GenericCreator.create(map, className);
		Object result = map.decode(jsonObject);
		if (classOfT == null) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends T> targetClass = (Class<? extends T>) Class.forName(className);
				if (targetClass == null) {
					return null;
				}
				return targetClass.cast(result);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return classOfT.cast(result);

	}

	/**
	 * Encode.
	 *
	 * @param src the src
	 * @return the json object
	 */
	public JsonObject encode(Object src) {
		if (src == null) {
			return new JsonObject();
		}
		IdMap map = getMap();
		GenericCreator.create(map, src.getClass());
		return map.toJsonObject(src);
	}

	/**
	 * From json.
	 *
	 * @param <T> the generic type
	 * @param json the json
	 * @return the t
	 */
	public static <T> T fromJson(Object json) {
		return new SimpleParser().decode(json);
	}

	/**
	 * From file.
	 *
	 * @param <T> the generic type
	 * @param file the file
	 * @return the t
	 */
	public static <T> T fromFile(String file) {
		BaseItem modelJson = FileBuffer.readBaseFile(file);
		return new SimpleParser().decode(modelJson);
	}

	/**
	 * From json.
	 *
	 * @param <T> the generic type
	 * @param json the json
	 * @param classOfT the class of T
	 * @return the t
	 */
	public static <T> T fromJson(Object json, Class<T> classOfT) {
		return new SimpleParser().decode(json, classOfT);
	}

	/**
	 * To json.
	 *
	 * @param src the src
	 * @return the json object
	 */
	public static JsonObject toJson(Object src) {
		return new SimpleParser().encode(src);
	}

	/**
	 * Decode model.
	 *
	 * @param <T> the generic type
	 * @param buffer the buffer
	 * @return the t
	 */
	public <T> T decodeModel(Buffer buffer) {
		return decodeModel(buffer, map, getTokener(), endTag);
	}

	/**
	 * Decode model.
	 *
	 * @param <T> the generic type
	 * @param buffer the buffer
	 * @param map the map
	 * @return the t
	 */
	public static <T> T decodeModel(Buffer buffer, IdMap map) {
		if (buffer == null || map == null) {
			return null;
		}
		char firstChar = buffer.nextClean(true);
		Tokener tokener = null;
		char endTag = 0;
		if (firstChar == JsonArray.START) {
			tokener = new JsonTokener();
			endTag = JsonArray.END;
		} else if (firstChar == JsonObject.START) {
			tokener = new JsonTokener();
			endTag = JsonObject.END;
		} else if (firstChar == XMLEntity.START) {
			tokener = new XMLTokener();
			endTag = XMLEntity.END;
		}
		if (tokener == null) {
			return null;
		}
		buffer.nextClean(false);
		return decodeModel(buffer, map, tokener, endTag);
	}

	private static char getEndTag(char startTag) {
		if (startTag == JsonArray.START) {
			return JsonArray.END;
		}
		if (startTag == JsonObject.START) {
			return JsonObject.END;
		}
		if (startTag == XMLEntity.START) {
			return XMLEntity.END;
		}
		return 0;
	}

	/**
	 * Decode model.
	 *
	 * @param <T> the generic type
	 * @param buffer the buffer
	 * @param map the map
	 * @param tokener the tokener
	 * @param endTag the end tag
	 * @return the t
	 */
	public static <T> T decodeModel(Buffer buffer, IdMap map, Tokener tokener, char endTag) {
		Object result = decodingModel(buffer, map, tokener, endTag);
		if (result == null) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends T> targetClass = (Class<? extends T>) result.getClass();
			return targetClass.cast(result);
		} catch (Exception e) {
		}
		return null;
	}

	private static Object decodingModel(Buffer buffer, IdMap map, Tokener tokener, char endTag) {
		if (buffer == null) {
			return null;
		}
		String className = null;

		String key = buffer.nextString().toString();
		Object result = null;
		if (key != null && IdMap.CLASS.equals(key)) {
			/* CLASSNAME : */
			buffer.getChar();
			className = tokener.nextString(buffer).toString();

			SendableEntityCreator creator = map.getCreator(className, true);
			if (creator == null) {
				return null;
			}

			/* MAYBE ID */
			key = tokener.nextString(buffer).toString();
			String id = null;
			if (IdMap.ID.equals(key)) {
				/* : */
				buffer.getChar();
				id = tokener.nextString(buffer).toString();
				result = map.getObject(id);
				if (result != null) {
					return result;
				}
			}
			result = creator.getSendableInstance(false);
			if (id != null) {
				map.put(id, result, false);
			}

			/* So now decoding Attributes */
			char currentChar = buffer.getCurrentChar();
			while (currentChar != endTag && !buffer.isEnd()) {
				key = tokener.nextString(buffer).toString();
				if (key.length() < 1) {
					break;
				}
				if (Tokener.PROPS.equals(key)) {
					/* Now Skip */

					/* Start Tag */
					char propStartTag = currentChar = buffer.getChar();
					char propEndTag = getEndTag(currentChar);
					while (currentChar != propEndTag && !buffer.isEnd()) {
						key = tokener.nextString(buffer).toString();
						if (key.length() < 1) {
							break;
						}
						/* SKIP : */
						buffer.getChar();

						if (buffer.getCurrentChar() == propStartTag) {
							/* new Subtype */
							Object subElement = decodingModel(buffer, map, tokener, propEndTag);
							creator.setValue(result, key, subElement, SendableEntityCreator.NEW);
						} else if (buffer.getCurrentChar() == JsonArray.START) {
							/* LIST of elements */
							do {
								Object subElement = decodingModel(buffer, map, tokener, propEndTag);
								creator.setValue(result, key, subElement, SendableEntityCreator.NEW);
								currentChar = buffer.getCurrentChar();
							} while (currentChar != JsonArray.END && !buffer.isEnd());
						} else {
							String value = tokener.nextString(buffer).toString();

							creator.setValue(result, key, value, SendableEntityCreator.NEW);
							currentChar = buffer.getCurrentChar();
						}
					}
				} else {
					/* Skip */
					tokener.nextString(buffer).toString();
				}
			}
		} else {
			return key;
		}
		return result;
	}
}
