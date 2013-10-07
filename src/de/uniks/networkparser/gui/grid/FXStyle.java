package de.uniks.networkparser.gui.grid;

public class FXStyle extends GridStyle{

	@Override
	public String toString() {
		String style="";
		if(getBackground()!=null){
			style="-fx-background-color: "+getBackground()+";";
		}
		if(getForground()!=null){
			style+="-fx-background-color: "+getForground()+";";
		}
		if(getBorders().size()>0){
			
//		"-fx-border-color: black; -fx-border-width: 1 0 0 1;"
		}
		
//		return super.toString();
		return style;
	}
}
