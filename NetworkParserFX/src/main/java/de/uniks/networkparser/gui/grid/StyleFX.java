package de.uniks.networkparser.gui.grid;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import de.uniks.networkparser.gui.GUILine;
import de.uniks.networkparser.interfaces.GUIPosition;

public class StyleFX extends GridStyle{
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
		String style="";
		if(getBackground()!=null){
			style="-fx-background-color: "+getBackground()+";";
		}
//		if(getForground()!=null){
//			style+="-fx-f-color: "+getForground()+";";
//		}
		if(getBorders().size()>0){
			
			String[] isBorders=new String[]{"0","0","0","0"};
			String[] colors=new String[]{"black","black","black","black"};
			for(Iterator<Entry<GUIPosition, GUILine>> iterator = getBorders().entrySet().iterator();iterator.hasNext();){
				Entry<GUIPosition, GUILine> item = iterator.next();
				switch(item.getKey()){
					case NORTH:
						isBorders[0] = item.getValue().getWidth();
						colors[0] = item.getValue().getColor();
						break;
					case EAST:
						isBorders[1] = item.getValue().getWidth();
						colors[1] = item.getValue().getColor();
						break;
					case SOUTH:
						isBorders[2] = item.getValue().getWidth();
						colors[2] = item.getValue().getColor();
						break;
					case WEST:
						isBorders[3] = item.getValue().getWidth();
						colors[3] = item.getValue().getColor();
						break;
					default:
						break;
				}
			}
			style+="-fx-border-color:";
			for(int i=0;i<4;i++){
				style+=" "+colors[i]+"";
			}
			style+=";";
			style+="-fx-border-width:";
			for(int i=0;i<4;i++){
				style+=" "+isBorders[i];
			}
			style+=";";
		}
		if(cursor!=null){
			style+="-fx-cursor:"+cursor+";";
		}
		return style;
	}
}
