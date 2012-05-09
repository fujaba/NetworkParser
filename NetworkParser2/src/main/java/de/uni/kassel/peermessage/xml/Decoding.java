package de.uni.kassel.peermessage.xml;

import java.util.ArrayList;
import java.util.HashSet;

import de.uni.kassel.peermessage.ReferenceObject;
import de.uni.kassel.peermessage.Tokener;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;
import de.uni.kassel.peermessage.interfaces.XMLEntityCreator;

/**
 * The Class Decoding for Decoding XML Entities.
 */
public class Decoding {
	public static final char ENDTAG='/';
	public static final char ITEMEND='>';
	public static final char ITEMSTART='<';
	public static final char SPACE=' ';
	private ArrayList<ReferenceObject> stack = new ArrayList<ReferenceObject>();
	private HashSet<String> stopwords=new HashSet<String>();
	private XMLIdMap parent;
	private Tokener value;

	public Decoding(XMLIdMap parent){
		this.parent=parent;
		stopwords.add("?xml");
		stopwords.add("!--");
		stopwords.add("!DOCTYPE");
	}

	public Object decode(String value) {
		Object result = null;
		this.value=new Tokener(value);
		this.stack.clear();
		while (!this.value.end()) {
			result = findTag("");
			if (result != null && !(result instanceof String)) {
				break;
			}
		}
		return result;
	}

	
	public boolean stepEmptyPos(String newPrefix, Object entity, String tag) {
		boolean exit = false;
		boolean empty=true;
		
		if(!newPrefix.equals("&")){
			return value.stepPos(ITEMSTART);
		}
		if (value.getCurrentChar() != ITEMSTART) {
			value.next();
		}
		int start=value.getIndex();
		while (!value.end() && !exit) {
			if(value.checkValues('\t', '\r', '\n', ' ', ITEMSTART)){
				empty=false;
			}
			if (value.getCurrentChar() == ITEMSTART) {
				if(empty||value.charAt(value.getIndex()+1)==ENDTAG){
					exit = true;
					break;
				}
			}
			if (!exit) {
				value.next();
			}
		}
		if(!empty&&exit){
			String value=this.value.previous(start);
			ReferenceObject refObject=null;
			if("&".equals(newPrefix)){
				refObject = stack.get(stack.size() - 1);
			}
			if(refObject!=null){
				SendableEntityCreator parentCreator=refObject.getCreater();
				parentCreator.setValue(refObject.getEntity(), newPrefix, value);
			}
		}
		return exit;
	}
	
	private Object findTag(String prefix) {
		if (value.stepPos(ITEMSTART)) {
			int start = value.nextPos();

			if (value.stepPos(SPACE, ITEMEND, ENDTAG)) {
				String tag = getEntity(start);
				return findTag(prefix, tag);
			}
		}
		return null;
	}
	private Object findTag(String prefix, String tag){
		if (tag.length() > 0) {
			XMLEntityCreator entityCreater = parent.getCreatorDecodeClass(tag);
			Object entity = null;
			boolean plainvalue = false;
			String newPrefix = "";
			if (entityCreater == null) {
				if(stack.size()==0){
					return null;
				}
				// Not found child creater
				ReferenceObject referenceObject = stack.get(stack.size() - 1);
				entityCreater = (XMLEntityCreator) referenceObject.getCreater();
				String[] properties = entityCreater.getProperties();
				prefix += tag;

				for (String prop : properties) {
					if (prop.equalsIgnoreCase(prefix)) {
						entity = referenceObject.getEntity();
						plainvalue = true;
						break;
					} else if (prop.startsWith(prefix)) {
						entity = referenceObject.getEntity();
						break;
					}
				}

				if (entity != null) {
					if (!plainvalue) {
						newPrefix = prefix + XMLIdMap.ENTITYSPLITTER;
						prefix += XMLIdMap.ATTRIBUTEVALUE;
					}
				}
			} else {
				entity = entityCreater.getSendableInstance(false);
				stack.add(new ReferenceObject(entityCreater, tag, this.parent, entity));
				newPrefix = XMLIdMap.ENTITYSPLITTER;
				prefix="";
			}
			if(entity==null){
				//Children
				parseChildren(prefix + XMLIdMap.ENTITYSPLITTER, entity, tag);
			}else{
				if (!plainvalue) {
					// Parse Attributes
					while (!value.end() && value.getCurrentChar() != ITEMEND) {
						if (value.getCurrentChar() == ENDTAG) {
							break;
						}
						int start = value.nextPos();
						if (value.getCurrentChar() != ENDTAG) {
							if (value.stepPos('=')) {
								String key = value.previous(start);
								value.skip(2);
								start = value.getIndex();
								if (value.stepPosButNot('\\', '"')) {
									String value = this.value.previous(start);
									this.value.next();
									entityCreater.setValue(entity, prefix + key, value);
								}
							}
						}
					}
					
					if(value.getCurrentChar()!=ENDTAG){
						//Children
						parseChildren(newPrefix, entity, tag);
					}else{
						value.next();
					}
					return entity;
				}
				if(value.getCurrentChar()==ENDTAG){
					value.next();
				}else{
					int start = this.value.nextPos();
					this.value.stepPosButNot('\\', ITEMSTART);
					String value= this.value.previous(start);
					entityCreater.setValue(entity, prefix, value);
					this.value.stepPos(ITEMSTART);
					this.value.stepPos(ITEMEND);
				}
				return null;
			}
			return entity;
		}
		return null;
	}
	
	private void parseChildren(String newPrefix, Object entity, String tag){
		while (!value.end()) {
			if (stepEmptyPos(newPrefix, entity, tag)) {
				int start = value.nextPos();

				if (value.stepPos(SPACE, ITEMEND, ENDTAG)) {
					String nextTag = getEntity(start);
			
					if(nextTag.length()>0){
						Object result = findTag(newPrefix, nextTag);
			
						if(result!=null){
							ReferenceObject refObject=null;
							if(result!=entity){
								if("&".equals(newPrefix)){
									refObject = stack.get(stack.size() - 2);
								}else{
									refObject = stack.get(stack.size() - 1);
								}
								if(refObject!=null){
									SendableEntityCreator parentCreator=refObject.getCreater();
									parentCreator.setValue(refObject.getEntity(), nextTag, result);
									if(entity!=null&&stack.size()>0){
										stack.remove(stack.size() - 1);
									}
								}
							}
						}
					}
					if(value.end()){
						if(entity!=null&&stack.size()>0){
							stack.remove(stack.size() - 1);
						}
					}else if(value.getCurrentChar()==ENDTAG){
						value.stepPos(ITEMEND);
						break;
					}
					value.next();
				}
			}
		}
	}
	
	private String getEntity(int start) {
		String tag = value.substring(start, value.getIndex());
		for(String stopword : stopwords){
			if(tag.startsWith(stopword)){
				return "";
			}
		}
		return tag;
	}
	public void addStopWords(String... stopwords){
		for(String stopword : stopwords){
			this.stopwords.add(stopword);
		}
	}
}
