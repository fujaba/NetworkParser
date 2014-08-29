package de.uniks.networkparser.test.build.sources;

import java.util.ArrayList;

public class MethodItem {
	private boolean isMethod;
	private String name="";
//	private StringBuilder comment= new StringBuilder();
	private ArrayList<String> body= new ArrayList<String>();
	private LoCMetric locmetric= new LoCMetric();
	private McCabe mccabemetric= new McCabe();
	
	public void append(String value) {
		if(body.size()<1){
			if(value.indexOf("{")>0 && value.indexOf("(")>0){
				int pos = value.indexOf("(");
				name = value.substring(0, pos);
				if(name.lastIndexOf(" ")>0){
					name = name.substring(name.lastIndexOf(" ")+1);
				}
				isMethod = true;
			}
		}
		
		body.add(value);
	}


	public boolean isMethod() {
		return isMethod;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (String line : body){
			sb.append(line);
		}
		return sb.toString();
	}


	public void finish() {
		locmetric.finish(this);
		mccabemetric.finish(this);
	}


	public int getLinesOfCode() {
		return locmetric.getLinesOfCode();
	}
	
	public LoCMetric getLinesOfCodeMetric() {
		return locmetric;
	}

	public ArrayList<String> getBody() {
		return body;
	}


	public String getName() {
		return name;
	}


	public int getMcCabe() {
		return mccabemetric.getMcCabe();
	}
}
