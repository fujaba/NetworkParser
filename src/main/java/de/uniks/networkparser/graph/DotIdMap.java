package de.uniks.networkparser.graph;

import de.uniks.networkparser.AbstractMap;
import de.uniks.networkparser.StringTokener;
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
			GraphEntity node = decodeNode(graph, value);
			graph.withNode(node);
			
			// and Second Node
			if(value.getCurrentChar() == '-') {
				// May Be Edge
				Association edge = new Association(node);
				char c = value.next();
				if(c == '-') {
					// Bidiassoc
				} else if(c == '>') {
					edge.with(AssociationTypes.UNDIRECTIONAL);
				}
				value.next();
				
				GraphEntity otherNode = decodeNode(graph, value);
				Association otherEdge = new Association(otherNode);
				otherEdge.with(otherNode);
				graph.withNode(otherNode);
				edge.with(otherEdge);
			}
			endChar = value.getCurrentChar();
		} while(endChar != 0 && endChar != '}');
		value.next();
	}
	GraphEntity decodeNode(GraphList graph, StringTokener value) {
		char c = value.nextClean(true);
		StringBuilder sb=new StringBuilder();
		sb.append(c);
//		boolean isQuote = true;
		GraphEntity node = null;
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
					node = new Clazz().with(id);
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
	void decodeAttributes(GraphEntity node, StringTokener value) {
		value.skipChar('[');
		char c;
		do {
			String key = decodeValue(value);
			if(key != null && value.getCurrentChar()=='=') {
				value.next();
				String valueStr = decodeValue(value);
				if(node instanceof Clazz) {
					Attribute attribute = ((Clazz)node).createAttribute(key,  DataType.STRING);
					attribute.withValue(valueStr);
				}
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

	private String getTyp(GraphEntity item, String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return item.getId();
		} else if (typ.equals(GraphIdMap.CLASS)) {
			return item.getName(shortName);
		}
		return "";
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

		for(GraphEntity node : root.getNodes()) {
			sb.append(node.getName(false));
			sb.append("[label=<<table border='0' cellborder='1' cellspacing='0'><tr><td><b>");
			if(isObjectdiagram) {
				sb.append("<u>");
			}
			sb.append(node.getName(false)+" : "+getTyp(node, root.getTyp(), removePackage));
			if(isObjectdiagram) {
				sb.append("</u>"); 
			}
			sb.append("</b></td></tr>");
			if(node instanceof Clazz == false) {
				sb.append("</table>>];"+BaseItem.CRLF);
				continue;
			}
			Clazz graphClazz = (Clazz) node;

			StringBuilder childBuilder = new StringBuilder();
			for(Attribute attribute : graphClazz.getAttributes()) {
				// add attribute line
				if(isObjectdiagram) {
					childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+attribute.getName() +" = "+attribute.getValue()+"</td></tr>");
				} else {
					childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+attribute.getName() +" : "+attribute.getType(removePackage)+"</td></tr>");
				}
			}
			if(childBuilder.length() > 0) {
				sb.append(BaseItem.CRLF+"<tr><td><table border='0' cellborder='0' cellspacing='0'>");
				sb.append(childBuilder.toString());
				sb.append(BaseItem.CRLF+"</table></td></tr>");
			}
			childBuilder = new StringBuilder();
			for(Method method : graphClazz.getMethods()) {
				// add attribute line
//				if(isObjectdiagram) {
				childBuilder.append(BaseItem.CRLF+"<tr><td align='left'>"+method.getName(false) + "</td></tr>");
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
		for(Association edge : root.getEdges()) {
			Association otherEdge = edge.getOther();
			if(otherEdge.getType()  != AssociationTypes.EDGE) {
				// It is bidiAssoc
				sb.append(edge.getClazz().getName(false) + " -- " + otherEdge.getClazz().getName(false));
				sb.append("[headlabel = \""+edge.getName()+"\" taillabel = \""+otherEdge.getName()+"\"];"+BaseItem.CRLF);
			} else {
				sb.append(edge.getClazz().getName(false) + " -> " + otherEdge.getClazz().getName(false));
				graphTyp = "digraph";
				sb.append("[taillabel = \""+edge.getName()+"\"];"+BaseItem.CRLF);
			}
				
		}
		sb.append("}");
		return graphTyp+sb.toString();
	}
}
