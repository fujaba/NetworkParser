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
import java.util.Iterator;
import java.util.Map.Entry;
import de.uniks.networkparser.GUILine;
import de.uniks.networkparser.Style;
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
