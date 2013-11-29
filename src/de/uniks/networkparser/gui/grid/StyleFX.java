package de.uniks.networkparser.gui.grid;

import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.gui.GUILine;
import de.uniks.networkparser.gui.grid.GridStyle;
import de.uniks.networkparser.interfaces.GUIPosition;

public class StyleFX extends GridStyle{

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
			String color="";
			for(Iterator<Entry<GUIPosition, GUILine>> iterator = getBorders().entrySet().iterator();iterator.hasNext();){
				Entry<GUIPosition, GUILine> item = iterator.next();
				switch(item.getKey()){
					case NORTH:
						isBorders[0]=item.getValue().getWidth();
						color = item.getValue().getColor();
						break;
					case EAST:
						isBorders[1]=item.getValue().getWidth();
						color = item.getValue().getColor();
						break;
					case SOUTH:
						isBorders[2]=item.getValue().getWidth();
						color = item.getValue().getColor();
						break;
					case WEST:
						isBorders[3]=item.getValue().getWidth();
						color = item.getValue().getColor();
						break;
					default:
						break;
				}
			}
			style+="-fx-border-color: "+color+";";

			style+="-fx-border-width:";
			for(int i=0;i<4;i++){
				style+=" "+isBorders[i];
			}
			style+=";";
		}
		return style;
	}
}
