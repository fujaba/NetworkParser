package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteConverter64;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * The Class I18NUtil.
 *
 * @author Stefan
 */
public class I18NUtil {
	
	/**
	 * Export.
	 *
	 * @param fileName the file name
	 * @param exportFile the export file
	 * @return true, if successful
	 */
	public boolean export(String fileName, String exportFile) {
		BaseItem buffer = FileBuffer.readBaseFile(fileName);
		return export(buffer, exportFile);

	}

	/**
	 * Export.
	 *
	 * @param buffer the buffer
	 * @param exportFile the export file
	 * @return true, if successful
	 */
	public boolean export(BaseItem buffer, String exportFile) {
		if (buffer == null) {
			return false;
		}
		CharacterBuffer data = transform(new CharacterBuffer(), buffer, "");
		if (exportFile == null) {
			exportFile = "texte.csv";
		}
		if (data != null && data.length() > 0) {
			return FileBuffer.writeFile(exportFile, data) > 0;
		}
		return false;
	}

	private CharacterBuffer transform(CharacterBuffer builder, BaseItem node, String prefix) {
		if (builder == null || !(node instanceof Entity)) {
			return builder;
		}
		Entity entity = (Entity) node;
		for (int i = 0; i < entity.size(); i++) {
			String fieldName = entity.getKeyByIndex(i);
			String tag;
			if (prefix.length() > 0) {
				tag = prefix + ":" + fieldName;
			} else {
				tag = fieldName;
			}
			Object value = entity.getValueByIndex(i);
			if (value instanceof BaseItem) {
				transform(builder, (BaseItem) value, tag);
			} else {
				builder.append(tag + "=" + value + BaseItem.CRLF);
			}
		}
		return builder;
	}

	/**
	 * Validate.
	 *
	 * @param sourceLanguage the source language
	 * @param targetLanguage the target language
	 * @param getMissedText the get missed text
	 * @return true, if successful
	 */
	public boolean validate(String sourceLanguage, String targetLanguage, boolean getMissedText) {
		if (sourceLanguage == null || targetLanguage == null) {
			return false;
		}
		BaseItem source = FileBuffer.readBaseFile(sourceLanguage + ".json");
		BaseItem target = FileBuffer.readBaseFile(targetLanguage + ".json");
		return validate(source, target, targetLanguage + "_neu.json", getMissedText);
	}

	/**
	 * Validate.
	 *
	 * @param sourceLanguage the source language
	 * @param targetLanguage the target language
	 * @param targetFileName the target file name
	 * @param getMissedText the get missed text
	 * @return true, if successful
	 */
	public boolean validate(BaseItem sourceLanguage, BaseItem targetLanguage, String targetFileName,
			boolean getMissedText) {
		if (!(sourceLanguage instanceof Entity) || !(targetLanguage instanceof Entity)) {
			return false;
		}
		Entity sourceEntity = (Entity) sourceLanguage;
		Entity targetEntity = (Entity) targetLanguage;

		if (validator(sourceEntity, targetEntity, "", getMissedText) && targetFileName != null) {
			Entity entity = (Entity) targetLanguage;
			FileBuffer.writeFile(targetFileName, entity.toString(2));
		}
		return true;
	}

	/**
	 * Validator.
	 *
	 * @param original the original
	 * @param newLanguage the new language
	 * @param prefix the prefix
	 * @param addNewLanguage the add new language
	 * @return true, if successful
	 */
	public boolean validator(Entity original, Entity newLanguage, String prefix, boolean addNewLanguage) {
		if (original == null || newLanguage == null) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < original.size(); i++) {
			String fieldName = original.getKeyByIndex(i);
			Object newValue = newLanguage.getValue(fieldName);
			Object value = original.getValue(fieldName);
			if (newValue == null) {
				if (addNewLanguage && !(value instanceof Entity)) {
					String oldText = ByteConverter64.toBase64String("" + value).toString();
					String browser = "Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0";
					HTMLEntity response = NodeProxyTCP.getSimpleHTTP(
							"https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=de&dt=t&q="
									+ oldText,
							NodeProxyTCP.USERAGENT, browser);
					JsonArray answer = new JsonArray().withValue(response.getBody().getValue());
					JsonArray first = answer.getJSONArray(0).getJSONArray(0);
					newLanguage.put(fieldName, first.getString(0));
				}
			}
			if (newValue != null) {
				if (value instanceof Entity && newValue instanceof Entity) {
					String tag;
					if (prefix.length() > 0) {
						tag = prefix + "." + fieldName;
					} else {
						tag = fieldName;
					}
					result = result & validator((Entity) value, (Entity) newValue, tag, addNewLanguage);
				}
			}
		}
		return result;
	}
}
