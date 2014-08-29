package de.uniks.networkparser.test.build.sources;

import java.util.ArrayList;

public class Body implements FilePart{
	public static final String CRLF="\r\n";
	private ArrayList<MethodItem> items= new ArrayList<MethodItem>();
	private MethodItem currentItem=null;
	private int counter=0;

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (MethodItem item : items){
			sb.append(item.toString());
		}
		return sb.toString();
	}

	@Override
	public void append(String value) {
		
		if(counter<1){
			currentItem = new MethodItem();
			items.add(currentItem);
		}
		
		currentItem.append(value);
		for (int z=0;z<value.length();z++){
			if(value.charAt(z)=='{'){
				counter++;
			}else if(value.charAt(z)=='}'){
				counter--;
			}
		}
	}

	@Override
	public int length() {
		return toString().length();
	}

	public Body allAll(String newBody) {
		String[] bodyLines=newBody.split(CRLF);
		for (String line : bodyLines){
			append(line+CRLF);
		}
		return this;
	}

	@Override
	public void finish() {
		for (MethodItem item : items){
			item.finish();
		}
	}

	public int getLinesOfCode() {
		int linesOfCode=0;
		for (MethodItem item : items){
			linesOfCode += item.getLinesOfCode();
		}
		return linesOfCode;
	}

	public ArrayList<MethodItem> getItems() {
		return items;
	}
}
