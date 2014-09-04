package de.uniks.networkparser.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import de.uniks.networkparser.graph.GraphAttribute;
import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphDataType;
import de.uniks.networkparser.graph.GraphEdge;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMethod;
import de.uniks.networkparser.graph.GraphParameter;

public class SVGDrawerTest {
	public static final String CRLF = "\r\n";
	@Test
	public void testDraw() throws IOException {
		GraphList map = new GraphList();
		
		
		GraphClazz space=map.with(new GraphClazz().withClassName("Space"));
		GraphClazz modelHistory=map.with(new GraphClazz().withClassName("ModelHistory"));
		GraphClazz networkNode=map.with(new GraphClazz().withClassName("NetworkNode"));
		GraphClazz idMap=map.with(new GraphClazz().withClassName("IdMap"));
		GraphClazz nodeProxy=map.with(new GraphClazz().withClassName("NodeProxy"));
		GraphClazz message=map.with(new GraphClazz().withClassName("Message"));
		
		// Methods
		networkNode.with(new GraphMethod("sendMessage", new GraphParameter(GraphDataType.ref(message)), new GraphParameter(GraphDataType.ref(nodeProxy))));
		
		// Attribute
		networkNode.with(new GraphAttribute("online", GraphDataType.BOOLEAN));
		
//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxyTCP"));
//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxy"));
		
		
		// Edges
		map.with( GraphEdge.create(space, modelHistory) );
		map.with( GraphEdge.create(space, networkNode) );
		map.with( GraphEdge.create(space, idMap) );
		map.with( GraphEdge.create(networkNode, nodeProxy) );
		
		GraphConverter converter=new GraphConverter();
		
		StringBuilder sb=new StringBuilder();
		sb.append("<html><head>"+CRLF);
		sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../lib/js/classmodel/diagramstyle.css\">"+CRLF);
		sb.append("\t<script src=\"../lib/js/classmodel/graph.js\"></script>"+CRLF);
		sb.append("\t<script src=\"../lib/js/classmodel/dagre.js\"></script>"+CRLF);
		sb.append("\t<script src=\"../lib/js/classmodel/drawer.js\"></script>"+CRLF);
		sb.append("</head><body>"+CRLF);
		sb.append("<script language=\"Javascript\">"+CRLF);
		sb.append("\tvar json="+converter.convertToJson(map, false).toString(2)+";"+CRLF);
		sb.append("\tnew Graph(json).layout();"+CRLF);
		sb.append("</script></body></html>");
		
		
		FileWriter fstream = new FileWriter("build/clazzModel.html");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(sb.toString());
		//Close the output stream
		out.close();
	}
}
