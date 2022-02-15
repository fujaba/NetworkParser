package de.uniks.networkparser.graph;

/**
 * The Class GraphMetric.
 *
 * @author Stefan
 */
public class GraphMetric extends GraphMember {
	private int mccabe = 0;
	private int linesOfCode = 0;
	private int commentCount = 0;
	private int methodheader = 0;
	private int emptyLine = 0;
	private int annotation = 0;
	private String crc;

	/**
	 * With mc cabe.
	 *
	 * @param value the value
	 * @return the graph metric
	 */
	public GraphMetric withMcCabe(int value) {
		this.mccabe = value;
		return this;
	}

	/**
	 * Gets the mc cabe.
	 *
	 * @return the mc cabe
	 */
	public int getMcCabe() {
		return mccabe;
	}

	/**
	 * Creates the.
	 *
	 * @param owner the owner
	 * @return the graph metric
	 */
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

	/**
	 * Gets the lines of code.
	 *
	 * @return the lines of code
	 */
	public int getLinesOfCode() {
		return linesOfCode;
	}

	/**
	 * Gets the comment count.
	 *
	 * @return the comment count
	 */
	public int getCommentCount() {
		return commentCount;
	}

	/**
	 * Gets the methodheader.
	 *
	 * @return the methodheader
	 */
	public int getMethodheader() {
		return methodheader;
	}

	/**
	 * Gets the empty line.
	 *
	 * @return the empty line
	 */
	public int getEmptyLine() {
		return emptyLine;
	}

	/**
	 * Gets the annotation lines.
	 *
	 * @return the annotation lines
	 */
	public int getAnnotationLines() {
		return annotation;
	}

	/**
	 * With loc.
	 *
	 * @param emptyLines the empty lines
	 * @param commentLine the comment line
	 * @param methodHeader the method header
	 * @param annotation the annotation
	 * @param loc the loc
	 * @return the graph metric
	 */
	public GraphMetric withLoc(int emptyLines, int commentLine, int methodHeader, int annotation, int loc) {
		this.emptyLine = emptyLines;
		this.commentCount = commentLine;
		this.methodheader = methodHeader;
		this.annotation = annotation;
		this.linesOfCode = loc;
		return this;
	}

	/**
	 * Gets the full lines.
	 *
	 * @return the full lines
	 */
	public int getFullLines() {
		return (linesOfCode + commentCount + methodheader + emptyLine + annotation);
	}

	/**
	 * To lo C string.
	 *
	 * @return the string
	 */
	public String toLoCString() {
		return "Line of File:" + getFullLines() + " - Lines of Code:" + linesOfCode;
	}

	/**
	 * Merge.
	 *
	 * @param otherMetric the other metric
	 * @return the graph metric
	 */
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

	/**
	 * With CRC.
	 *
	 * @param value the value
	 * @return the graph metric
	 */
	public GraphMetric withCRC(String value) {
		this.crc = value;
		return this;
	}
	
	/**
	 * Gets the crc.
	 *
	 * @return the crc
	 */
	public String getCRC() {
		return crc;
	}
}
