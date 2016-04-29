package de.uniks.networkparser.ext.javafx;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.Iterator;
import java.util.Map.Entry;
import de.uniks.networkparser.event.GUILine;
import de.uniks.networkparser.event.Style;
import de.uniks.networkparser.interfaces.GUIPosition;

public class StyleFX extends Style{
	public static final String PROPERTY_CURSOR="cursor";
	private String cursor;

	public String getCursor() {
		return cursor;
	}
	public StyleFX withCursor(String cursor) {
		this.cursor = cursor;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder style=new StringBuilder();
		String item;
		item =getBackground();
		if(item != null){
			style.append("-fx-background-color: ");
			if(item.startsWith("#") == false){
				style.append("#");
			}
			style.append(item);
			style.append(";");
		}
		item =getForground();
		if(item != null){
			style.append("-fx-text-fill: ");
			if(item.startsWith("#") == false){
				style.append("#");
			}
			style.append(item);
			style.append(";");
		}
		if(getBorders().size()>0){
			String[] isBorders=new String[]{"0","0","0","0"};
			String[] colors=new String[]{"black","black","black","black"};
			for(Iterator<Entry<GUIPosition, GUILine>> iterator = getBorders().entrySet().iterator();iterator.hasNext();){
				Entry<GUIPosition, GUILine> border = iterator.next();
				switch(border.getKey()){
					case NORTH:
						isBorders[0] = border.getValue().getWidth();
						colors[0] = border.getValue().getColor();
						break;
					case EAST:
						isBorders[1] = border.getValue().getWidth();
						colors[1] = border.getValue().getColor();
						break;
					case SOUTH:
						isBorders[2] = border.getValue().getWidth();
						colors[2] = border.getValue().getColor();
						break;
					case WEST:
						isBorders[3] = border.getValue().getWidth();
						colors[3] = border.getValue().getColor();
						break;
					default:
						break;
				}
			}
			style.append("-fx-border-color:");
			
			for(int i=0;i<4;i++){
				style.append(" "+colors[i]);
			}
			style.append(";");
			style.append("-fx-border-width:");
			for(int i=0;i<4;i++){
				style.append(" "+isBorders[i]);
			}
			style.append(";");
		}
		if(cursor!=null){
			style.append("-fx-cursor:"+cursor+";");
		}
		if(this.getAlignment()!= null) {
			style.append("-fx-text-alignment:"+this.getAlignment()+";");
		}
		return style.toString();
	}
	
	public static String getPath(){
		return StyleFX.class.getResource("dialog/styles.css").toExternalForm();
	}
}
