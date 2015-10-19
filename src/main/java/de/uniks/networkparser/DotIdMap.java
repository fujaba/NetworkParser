package de.uniks.networkparser;

import de.uniks.networkparser.graph.GraphAttribute;
import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphDataType;
import de.uniks.networkparser.graph.GraphEdge;
import de.uniks.networkparser.graph.GraphEdgeTypes;
import de.uniks.networkparser.graph.GraphIdMap;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMethod;
import de.uniks.networkparser.graph.GraphNode;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.IdMapDecoder;

//graph	:	[ strict ] (graph | digraph) [ ID ] '{' stmt_list '}'
//stmt_list	:	[ stmt [ ';' ] [ stmt_list ] ]
//stmt	:	node_stmt
//|	edge_stmt
//|	attr_stmt
//|	ID '=' ID
//|	subgraph
//attr_stmt	:	(graph | node | edge) attr_list
//attr_list	:	'[' [ a_list ] ']' [ attr_list ]
//a_list	:	ID '=' ID [ (';' | ',') ] [ a_list ]
//edge_stmt	:	(node_id | subgraph) edgeRHS [ attr_list ]
//edgeRHS	:	edgeop (node_id | subgraph) [ edgeRHS ]
//node_stmt	:	node_id [ attr_list ]
//node_id	:	ID [ port ]
//port	:	':' ID [ ':' compass_pt ]
//|	':' compass_pt
//subgraph	:	[ subgraph [ ID ] ] '{' stmt_list '}'
//compass_pt	:	(n | ne | e | se | s | sw | w | nw | c | _)
public class DotIdMap extends AbstractMap implements IdMapDecoder, Converter {
	@Override
	public Object decode(BaseItem value) {
		if(value instanceof GraphList==false) {
			return null;
		}
		return null;
	}

	@Override
	public Object decode(String value) {
		StringTokener item = new StringTokener();
		item.withBuffer(value);
		return decodeGraph(item);
	}
	Object decodeGraph(StringTokener value) {
		char c = value.nextClean(true);
		StringBuilder sb=new StringBuilder();
//		boolean isQuote = true;
		boolean useStrict=false;
		GraphList graph = new GraphList();
		do {
			c = value.next();
			switch (c) {
			case 0:
				break;
			case ' ':
				if(useStrict == false && "strict".equals(sb.toString())) {
					sb = new StringBuilder();
					useStrict = true;
				}
				break;
			case '[':
			case '{':
			case '\n':
				value.next();
				if(c == '[') {
					decodeAttributes(graph, value);
				}
				decodeEdge(graph, value);
				c =0;
				break;
			default:
				sb.append(c);
			}
		}while(c != 0);
		return graph;
	}
	
	void decodeEdge(GraphList graph, StringTokener value) {
		char endChar;
		do {
			GraphNode node = decodeNode(graph, value);
			graph.withNode(node);
			
			// and Second Node
			if(value.getCurrentChar() == '-') {
				// May Be Edge
				GraphEdge edge = new GraphEdge();
				edge.with(node);
				char c = value.next();
				if(c == '-') {
					// Bidiassoc
				} else if(c == '>') {
					edge.withTyp(GraphEdgeTypes.UNDIRECTIONAL);
				}
				value.next();
				
				GraphEdge otherEdge = new GraphEdge();
				GraphNode otherNode = decodeNode(graph, value);
				otherEdge.with(otherNode);
				graph.withNode(otherNode);
				edge.with(otherEdge);
			}
			endChar = value.getCurrentChar();
		} while(endChar != 0 && endChar != '}');
		value.next();
	}
	GraphNode decodeNode(GraphList graph, StringTokener value) {
		char c = value.nextClean(true);
		StringBuilder sb=new StringBuilder();
		sb.append(c);
//		boolean isQuote = true;
		GraphNode node = null;
		do {
			c = value.next();
			switch (c) {
			case 0:
				break;
			case '[':
			case '-':
			case '}':
			case '\n':
				String id = sb.toString().trim();
				node = graph.getNode(id);
				if(node == null) {
					node = new GraphNode().withId(id);
				}
				if(c == '[') {
					decodeAttributes(node, value);
				}
				if(c == '\n') {
					value.next();
				}
				value.nextClean(true);
				c = 0;
				break;
			default:
				sb.append(c);
			}
		}while(c != 0);
		return node;
	}
	
//	ID '=' ID [ (';' | ',') ]
	void decodeAttributes(GraphNode node, StringTokener value) {
		value.skipChar('[');
		char c;
		do {
			String key = decodeValue(value);
			if(key != null && value.getCurrentChar()=='=') {
				value.next();
				String valueStr = decodeValue(value);
				node.addAttribute(key, GraphDataType.STRING, valueStr);
			}
			c = value.getCurrentChar();
			if(c != ']') {
				c = value.next();
			}
		}while(c != ']');
		value.next();
	}

	String decodeValue(StringTokener value) {
		char c = value.nextClean(true);
		StringBuilder sb=new StringBuilder();
		sb.append(c);
		do {
			c = value.next();
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
		}while(c != 0);
		return sb.toString();
	}

	
	@Override
	public String convert(GraphList root, boolean removePackage) {
		StringBuilder sb=new StringBuilder();
		String graphTyp = "graph";
		if(GraphIdMap.OBJECT.equals(root.getTyp())) {
			sb.append(" ObjectDiagram {"+BaseItem.CRLF);
		}else{
			sb.append(" ClassDiagram {"+BaseItem.CRLF);
		}
		sb.append("   node [shape = none, fontsize = 10, fontname = \"Arial\"];"+BaseItem.CRLF);
		sb.append("   edge [fontsize = 10, fontname = \"Arial\"];"+BaseItem.CRLF);
		sb.append("   compound=true;" + BaseItem.CRLF +BaseItem.CRLF);
		boolean isObjectdiagram =false;
		isObjectdiagram = GraphIdMap.OBJECT.equals(root.getTyp());

		for(GraphNode node : root.getNodes()) {
			sb.append(node.getId());
			sb.append("[label=<<table border='0' cellborder='1' cellspacing='0'><tr><td><b>");
			if(isObjectdiagram) {
				sb.append("<u>");
			}
			sb.append(node.getId()+" : "+node.getTyp(root.getTyp(), removePackage));
			if(isObjectdiagram) {
				sb.append("</u>"); 
			}
			sb.append("</b></td></tr>");
			if(node instanceof GraphClazz == false) {
				sb.append("</table>>];"+BaseItem.CRLF);
				continue;
			}
			GraphClazz graphClazz = (GraphClazz) node;

			StringBuilder childBuilder = new StringBuilder();
			for(GraphAttribute attribute : graphClazz.getAttributes()) {
				// add attribute line
				if(isObjectdiagram) {
					childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+attribute.getId() +" = "+attribute.getValue()+"</td></tr>");
				} else {
					childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+attribute.getId() +" : "+attribute.getType(removePackage)+"</td></tr>");
				}
			}
			if(childBuilder.length() > 0) {
				sb.append(BaseItem.CRLF+"<tr><td><table border='0' cellborder='0' cellspacing='0'>");
				sb.append(childBuilder.toString());
				sb.append(BaseItem.CRLF+"</table></td></tr>");
			}
			childBuilder = new StringBuilder();
			for(GraphMethod method : graphClazz.getMethods()) {
				// add attribute line
//				if(isObjectdiagram) {
				childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+method.getId() + "</td></tr>");
			}
			if(childBuilder.length() > 0) {
				sb.append(BaseItem.CRLF+"<tr><td><table border='0' cellborder='0' cellspacing='0'>");
				sb.append(childBuilder.toString());
				sb.append(BaseItem.CRLF+"</table></td></tr>");
			}
			sb.append("</table>>];"+BaseItem.CRLF);
		}
		
		root.initSubLinks();
//		// now generate edges from edgeMap
		for(GraphEdge edge : root.getEdges()) {
			GraphEdge otherEdge = edge.getOther();
			if(otherEdge.getTyp()  != GraphEdgeTypes.EDGE) {
				// It is bidiAssoc
				sb.append(edge.getNode().getId() + " -- " + otherEdge.getNode().getId());
				sb.append("[headlabel = \""+edge.getProperty()+"\" taillabel = \""+otherEdge.getProperty()+"\"];"+BaseItem.CRLF);
			} else {
				sb.append(edge.getNode().getId() + " -> " + otherEdge.getNode().getId());
				graphTyp = "digraph";
				sb.append("[taillabel = \""+edge.getProperty()+"\"];"+BaseItem.CRLF);
			}
				
		}
		sb.append("}");
		return graphTyp+sb.toString();
	}
}
