package de.uniks.networkparser.test.build.sources;


public class LoCMetric {
	private int linesOfCode=0;
	private int commentCount=0;
	private int methodheader=0;
	private int emptyLine=0;
	private int annotation=0;
	public void finish(MethodItem item){
		for (String line : item.getBody()){
			String simple = line.trim();
			if(simple.length()<1){
				emptyLine++;
				continue;
			}
			if(simple.indexOf("/*")>=0 || simple.indexOf("*/")>=0 || simple.indexOf("//")>=0 || simple.startsWith("*")){
				commentCount++;
				continue;
			}
			if("{}".indexOf(simple)>=0){
				methodheader++;
				continue;
			}
			if(simple.startsWith("@")){
				annotation++;
				continue;
			}
			linesOfCode++;
		}
	}
	public int getLinesOfCode() {
		return linesOfCode;
	}
	public int getCommentCount() {
		return commentCount;
	}

	public int getMethodheader() {
		return methodheader;
	}
	public int getEmptyLine() {
		return emptyLine;
	}
	public int getAnnotation() {
		return annotation;
	}
	
	@Override
	public String toString(){
		return "Line of File:" + getFullLines() + " - Lines of Code:" +linesOfCode;
	}
	
	public int getFullLines(){
		return (linesOfCode+commentCount+methodheader+emptyLine+annotation);
	}
}
