package de.uniks.networkparser.parser.excel;
import java.nio.charset.Charset;

/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in writing, software distributed under the Licence is
distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations under the Licence.
*/
import de.uniks.networkparser.gui.Pos;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class ExcelCell implements SendableEntityCreatorTag, EntityList{
	public static final String TAG="c";
	public static final String PROPERTY_STYLE="s";
	public static final String PROPERTY_TYPE="t";
	public static final String PROPERTY_REFERENZ="r";
	public static final String[] PROPERTIES={PROPERTY_STYLE, PROPERTY_TYPE, PROPERTY_REFERENZ};
	private Pos pos;
	private Object content;
	private ExcelCell referenceCell;
	private String style;
	private String type;
	private SimpleList<EntityList> children;

	@Override
	public String[] getProperties() {
		return PROPERTIES;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof ExcelCell == false) {
			return null;
		}
		if(PROPERTY_STYLE.equals(attribute)) {
			((ExcelCell)entity).getStyle();
		}
		if(PROPERTY_TYPE.equals(attribute)) {
			((ExcelCell)entity).getType();
		}
		if(PROPERTY_REFERENZ.equals(attribute)) {
			((ExcelCell)entity).getReferenz();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(XMLEntityCreator.ALL.equals(type)) {
			if(value instanceof XMLEntity == false) {
				return false;
			}
			XMLEntity item = (XMLEntity) value;
			ExcelCell parent = (ExcelCell) entity;
			if(item.getValue(PROPERTY_STYLE) != null) {
				parent.setStyle(item.getString(PROPERTY_STYLE));
			}
			if(item.getValue(PROPERTY_TYPE) != null) {
				parent.setType(item.getString(PROPERTY_TYPE));
			}
			if(item.getValue(PROPERTY_REFERENZ) != null) {
				parent.withReferenz(Pos.valueOf(""+item.getValue(PROPERTY_REFERENZ)));
			}
			for(EntityList child : item.getChildren()) {
				parent.with(child);
			}
			return true;
		}
		if(entity instanceof ExcelCell == false) {
			return false;
		}
		if(PROPERTY_STYLE.equals(attribute)) {
			((ExcelCell)entity).setStyle(""+value);
			return true;
		}
		if(PROPERTY_TYPE.equals(attribute)) {
			((ExcelCell)entity).setType(""+value);
			return true;
		}
		if(PROPERTY_REFERENZ.equals(attribute)) {
			((ExcelCell)entity).withReferenz(Pos.valueOf(""+value));
			return true;
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ExcelCell();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	public Pos getReferenz() {
		return pos;
	}

	public ExcelCell withReferenz(Pos referenz) {
		this.pos = referenz;
		return this;
	}

	public String getType() {
		return type;
	}

	public String getStyle() {
		return style;
	}
	public Object getContent() {
		if(referenceCell != null) {
			return referenceCell.getContent();
		}
		return content;
	}
	public String getContentAsString() {
		if(referenceCell != null) {
			return referenceCell.getContentAsString();
		}
		if(content == null) {
			return "";
		}
		return content.toString();
	}
	public boolean setContent(Object value) {
		if(referenceCell != null) {
			return referenceCell.setContent(value);
		}
		if((this.content == null && value != null) ||
		   (this.content != null && this.content.equals(value) == false)) {
		   this.content = value;
		   return true;
	   }
	   return false;
	}
	
	public ExcelCell withContent(Object value) {
		setContent(value);
		return this;
	}
	
	public String getDataLine(){
		String ref = "";
		if(this.pos != null) {
			ref = this.pos.toString();
		}
		if(content == null){
			return null;
		}
		if(content instanceof Number) {
			return "<c r=\""+ref+"\"><v>"+content+"</v></c>";
		}
		if(content instanceof Boolean) {
			if((Boolean)content) {
				return "<c r=\""+ref+"\" t=\"b\"><v>1</v></c>";
			}
			return "<c r=\""+ref+"\" t=\"b\"><v>0</v></c>";
		}
		return "<c r=\""+ref+"\" t=\"inlineStr\"><is><t>"+new String(content.toString().getBytes(Charset.forName("UTF-8")))+"</t></is></c>";
	}
	
	public ExcelCell getReferenceCell() {
		return referenceCell;
	}

	public boolean setStyle(String value) {
		if((this.style == null && value != null) ||
				(this.style != null && this.style.equals(value) == false)) {
			this.style = value;
			return true;
		}
		return false;
	}

	public boolean setType(String value) {
		if((this.type == null && value != null) ||
				(this.style != null && this.type.equals(value) == false)) {
			this.type = value;
			return true;
		}
		return false;
	}

	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	@Override
	public String toString(Converter converter) {
		return toString();
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}

	@Override
	public ExcelCell with(Object... values) {
		if(values == null) {
			return null;
		}
		if(children ==null) {
			this.children = new SimpleList<EntityList>();
		}
		for(Object item : values) {
			if(item instanceof EntityList) {
				this.children.add((EntityList) item);
			}
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ExcelCell();
	}

	@Override
	public SimpleList<EntityList> getChildren() {
		return this.children;
	}

	@Override
	public int size() {
		if(this.children == null) {
			return 0;
		}
		return this.children.size();
	}

	public boolean setReferenceCell(ExcelCell value) {
		if((this.referenceCell == null && value != null) ||
				(this.referenceCell != null && this.referenceCell.equals(value) == false)) {
			this.referenceCell = value;
			return true;
		}
		return false;
	}
}
