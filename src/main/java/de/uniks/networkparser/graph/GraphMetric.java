package de.uniks.networkparser.graph;

public class GraphMetric extends GraphMember {
	private int mccabe = 0;
	private int linesOfCode = 0;
	private int commentCount = 0;
	private int methodheader = 0;
	private int emptyLine = 0;
	private int annotation = 0;
	private String crc;

	public GraphMetric withMcCabe(int value) {
		this.mccabe = value;
		return this;
	}

	public int getMcCabe() {
		return mccabe;
	}

	public static GraphMetric create(GraphMember owner) {
		if (owner == null) {
			return new GraphMetric();
		}
		GraphSimpleSet children = owner.getChildren();
		GraphMetric metric = null;
		for (int i = 0; i < children.size(); i++) {
			GraphMember item = children.get(i);
			if (item instanceof GraphMetric) {
				metric = (GraphMetric) item;
				break;
			}
		}
		if (metric == null) {
			metric = new GraphMetric();
			owner.withChildren(metric);
		}
		return metric;
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

	public int getAnnotationLines() {
		return annotation;
	}

	public GraphMetric withLoc(int emptyLines, int commentLine, int methodHeader, int annotation, int loc) {
		this.emptyLine = emptyLines;
		this.commentCount = commentLine;
		this.methodheader = methodHeader;
		this.annotation = annotation;
		this.linesOfCode = loc;
		return this;
	}

	public int getFullLines() {
		return (linesOfCode + commentCount + methodheader + emptyLine + annotation);
	}

	public String toLoCString() {
		return "Line of File:" + getFullLines() + " - Lines of Code:" + linesOfCode;
	}

	public GraphMetric merge(GraphMetric otherMetric) {
		if (otherMetric == null) {
			return this;
		}
		this.emptyLine += otherMetric.getEmptyLine();
		this.commentCount += otherMetric.getCommentCount();
		this.methodheader += otherMetric.getMethodheader();
		this.annotation += otherMetric.getAnnotationLines();
		this.linesOfCode += otherMetric.getLinesOfCode();
		return this;
	}

	public GraphMetric withCRC(String value) {
		this.crc = value;
		return this;
	}
	
	public String getCRC() {
		return crc;
	}
}
