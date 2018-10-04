package de.uniks.networkparser.converter;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphEntity;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Converter;

// graph : [ strict ] (graph | digraph) [ ID ] '{' stmt_list '}'
// stmt_list : [ stmt [ ';' ] [ stmt_list ] ]
// stmt : node_stmt
// | edge_stmt
// | attr_stmt
// | ID '=' ID
// | subgraph
// attr_stmt : (graph | node | edge) attr_list
// attr_list : '[' [ a_list ] ']' [ attr_list ]
// a_list : ID '=' ID [ (';' | ',') ] [ a_list ]
// edge_stmt : (node_id | subgraph) edgeRHS [ attr_list ]
// edgeRHS : edgeop (node_id | subgraph) [ edgeRHS ]
// node_stmt : node_id [ attr_list ]
// node_id : ID [ port ]
// port : ':' ID [ ':' compass_pt ]
// | ':' compass_pt
// subgraph : [ subgraph [ ID ] ] '{' stmt_list '}'
// compass_pt : (n | ne | e | se | s | sw | w | nw | c | _)
public class DotConverter implements Converter {
	private boolean removePackage;

	public DotConverter() {

	}

	public DotConverter(boolean removePackage) {
		this.removePackage = removePackage;
	}

	public GraphList decode(Object item) {
		if (item instanceof CharacterBuffer) {
			return decodeGraph((CharacterBuffer) item);
		} else if (item != null) {
			CharacterBuffer buffer = new CharacterBuffer();
			buffer.with(item.toString());
			return decodeGraph(buffer);
		}
		return null;
	}

	GraphList decodeGraph(BufferItem value) {
		char c = value.nextClean(true);
		StringBuilder sb = new StringBuilder();
//			boolean isQuote = true;
		boolean useStrict = false;
		GraphList graph = new GraphList();
		do {
			c = value.getChar();
			switch (c) {
			case 0:
				break;
			case ' ':
				if (useStrict == false && "strict".equals(sb.toString())) {
					sb = new StringBuilder();
					useStrict = true;
				}
				break;
			case '[':
			case '{':
			case '\n':
				value.getChar();
				if (c == '[') {
					decodeAttributes(graph, value);
				}
				decodeEdge(graph, value);
				c = 0;
				break;
			default:
				sb.append(c);
			}
		} while (c != 0);
		return graph;
	}

	void decodeEdge(GraphList graph, BufferItem value) {
		char endChar;
		do {
			GraphEntity node = decodeNode(graph, value);
			graph.withNode(node);

			// and Second Node
			if (value.getCurrentChar() == '-') {
				// May Be Edge
				Association edge = new Association(node);
				char c = value.getChar();
				if (c == '-') {
					// Bidiassoc
				} else if (c == '>') {
					edge.with(AssociationTypes.UNDIRECTIONAL);
				}
				value.getChar();

				GraphEntity otherNode = decodeNode(graph, value);
				if (otherNode != null) {
					Association otherEdge = new Association(otherNode);
					otherEdge.with(otherNode);
					graph.withNode(otherNode);
					edge.with(otherEdge);
				}
			}
			endChar = value.getCurrentChar();
		} while (endChar != 0 && endChar != '}');
		value.skip();
	}

	GraphEntity decodeNode(GraphList graph, BufferItem value) {
		char c = value.nextClean(true);
		StringBuilder sb = new StringBuilder();
		sb.append(c);
//			boolean isQuote = true;
		GraphEntity node = null;
		do {
			c = value.getChar();
			switch (c) {
			case 0:
				break;
			case '[':
			case '-':
			case '}':
			case '\n':
				String id = sb.toString().trim();
				node = graph.getNode(id);
				if (node == null) {
					node = new Clazz(id);
				}
				if (c == '[') {
					decodeAttributes(node, value);
				}
				if (c == '\n') {
					value.skip();
				}
				value.nextClean(true);
				c = 0;
				break;
			default:
				sb.append(c);
			}
		} while (c != 0);
		return node;
	}

//		ID '=' ID [ (';' | ',') ]
	void decodeAttributes(GraphEntity node, BufferItem value) {
		value.skipTo('[', false);
		char c;
		do {
			String key = decodeValue(value);
			if (key != null && value.getCurrentChar() == '=') {
				value.skip();
				String valueStr = decodeValue(value);
				if (node instanceof Clazz) {
					Attribute attribute = ((Clazz) node).createAttribute(key, DataType.STRING);
					attribute.withValue(valueStr);
				}
			}
			c = value.getCurrentChar();
			if (c != ']') {
				c = value.getChar();
			}
		} while (c != ']');
		value.skip();
	}

	String decodeValue(BufferItem value) {
		char c = value.nextClean(true);
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		do {
			c = value.getChar();
			switch (c) {
			case 0:
				break;
			case '=':
			case ',':
			case ';':
			case '}':
			case ']':
			case '\n':
				c = 0;
				break;
			default:
				sb.append(c);
			}
		} while (c != 0);
		return sb.toString();
	}

	private String getType(GraphEntity item, String type, boolean shortName) {
		if (type.equals(GraphTokener.OBJECTDIAGRAM)) {
			return item.getId();
		} else if (type.equals(GraphTokener.CLASSDIAGRAM)) {
			return item.getName(shortName);
		}
		return "";
	}

	@Override
	public String encode(BaseItem entity) {
		return encode(entity, removePackage);
	}

	public String encode(BaseItem entity, boolean removePackage) {
		if (entity instanceof GraphList == false) {
			return "";
		}
		GraphList root = (GraphList) entity;
		StringBuilder sb = new StringBuilder();
		String graphType = "graph";
		if (GraphTokener.OBJECTDIAGRAM.equals(root.getType())) {
			sb.append(" ObjectDiagram {" + BaseItem.CRLF);
		} else {
			sb.append(" ClassDiagram {" + BaseItem.CRLF);
		}
		sb.append("   node [shape = none, fontsize = 10, fontname = \"Arial\"];" + BaseItem.CRLF);
		sb.append("   edge [fontsize = 10, fontname = \"Arial\"];" + BaseItem.CRLF);
		sb.append("   compound=true;" + BaseItem.CRLF + BaseItem.CRLF);
		boolean isObjectdiagram = false;
		isObjectdiagram = GraphTokener.OBJECTDIAGRAM.equals(root.getType());

		for (GraphEntity node : root.getNodes()) {
			sb.append(node.getName(false));
			sb.append("[label=<<table border='0' cellborder='1' cellspacing='0'><tr><td><b>");
			if (isObjectdiagram) {
				sb.append("<u>");
			}
			sb.append(node.getName(false) + " : " + getType(node, root.getType(), removePackage));
			if (isObjectdiagram) {
				sb.append("</u>");
			}
			sb.append("</b></td></tr>");
			if (node instanceof Clazz == false) {
				sb.append("</table>>];" + BaseItem.CRLF);
				continue;
			}
			Clazz graphClazz = (Clazz) node;

			StringBuilder childBuilder = new StringBuilder();
			for (Attribute attribute : graphClazz.getAttributes()) {
				// add attribute line
				if (isObjectdiagram) {
					childBuilder.append(BaseItem.CRLF + "<tr><td align='left'>" + attribute.getName() + " = "
							+ attribute.getValue(GraphTokener.OBJECTDIAGRAM, false) + "</td></tr>");
				} else {
					childBuilder.append(BaseItem.CRLF + "<tr><td align='left'>" + attribute.getName() + " : "
							+ attribute.getType().getName(removePackage) + "</td></tr>");
				}
			}
			if (childBuilder.length() > 0) {
				sb.append(BaseItem.CRLF + "<tr><td><table border='0' cellborder='0' cellspacing='0'>");
				sb.append(childBuilder.toString());
				sb.append(BaseItem.CRLF + "</table></td></tr>");
			}
			childBuilder = new StringBuilder();
			for (Method method : graphClazz.getMethods()) {
				// add attribute line
//					if(isObjectdiagram) {
				childBuilder
						.append(BaseItem.CRLF + "<tr><td align='left'>" + method.getName(false, false) + "</td></tr>");
			}
			if (childBuilder.length() > 0) {
				sb.append(BaseItem.CRLF + "<tr><td><table border='0' cellborder='0' cellspacing='0'>");
				sb.append(childBuilder.toString());
				sb.append(BaseItem.CRLF + "</table></td></tr>");
			}
			sb.append("</table>>];" + BaseItem.CRLF);
		}

		root.initSubLinks();
//			// now generate edges from edgeMap
		for (Association edge : root.getAssociations()) {
			Association otherEdge = edge.getOther();
			if (otherEdge.getType() != AssociationTypes.EDGE) {
				// It is bidiAssoc
				sb.append(edge.getClazz().getName(false) + " -- " + otherEdge.getClazz().getName(false));
				sb.append("[headlabel = \"" + edge.getName() + "\" taillabel = \"" + otherEdge.getName() + "\"];"
						+ BaseItem.CRLF);
			} else {
				sb.append(edge.getClazz().getName(false) + " -> " + otherEdge.getClazz().getName(false));
				graphType = "digraph";
				sb.append("[taillabel = \"" + edge.getName() + "\"];" + BaseItem.CRLF);
			}

		}
		sb.append("}");
		return graphType + sb.toString();
	}
}
