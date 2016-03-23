package de.uniks.networkparser.test.ant.sources;

import java.util.ArrayList;
import java.util.HashSet;

public class Imports implements FilePart{
	private ArrayList<String> items= new ArrayList<String>();
	private HashSet<String> packages= new HashSet<String>();

	@Override
	public void append(String value) {
		items.add(value);
		String simple = value.trim();

//		if(simple.endsWith(";")){
////			simple =simple.substring(0, simple.length()-1);
//		}
		simple = simple.substring(simple.indexOf(" ")+1);
		packages.add(simple.substring(0, simple.lastIndexOf(".")));
	}

	@Override
	public int length() {
		return toString().length();
	}

	@Override
	public void finish() {

	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (String item : items){
			sb.append(item);
		}
		return sb.toString();
	}

	public HashSet<String> getPackages() {
		return packages;
	}
}
