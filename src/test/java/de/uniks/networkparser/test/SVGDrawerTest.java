package de.uniks.networkparser.test;

import java.io.BufferedWriter;
import java.io.File;
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
import de.uniks.networkparser.graph.GraphPattern;
import de.uniks.networkparser.json.JsonObject;

public class SVGDrawerTest {
	public static final String CRLF = "\r\n";

	@Test
	public void testDrawClazz() throws IOException {
		GraphList map = new GraphList();
		
		
		GraphClazz space=map.with(new GraphClazz().with("Space"));
		GraphClazz modelHistory=map.with(new GraphClazz().with("ModelHistory"));
		GraphClazz networkNode=map.with(new GraphClazz().with("NetworkNode"));
		GraphClazz idMap=map.with(new GraphClazz().with("IdMap"));
		GraphClazz nodeProxy=map.with(new GraphClazz().with("NodeProxy"));
		GraphClazz message=map.with(new GraphClazz().with("Message"));
		
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
		writeJson("clazzModel.html", converter.convertToJson(map, false));
	}
	
	@Test
	public void testPattern() throws IOException {
		GraphList map = new GraphList();
		
		GraphPattern space = map.with(new GraphPattern().with("Space"));
		GraphPattern modelHistory = map.with(new GraphPattern().with("Item").withBounds("create"));
		map.with(new GraphPattern().with("ModelHistory").withBounds("nac"));
		
		map.with( GraphEdge.create(space, modelHistory).withStyle("create") );

		GraphList subGraph = new GraphList();
		GraphPattern person = subGraph.with(new GraphPattern().with("Person"));
		subGraph.withStyle("nac");
		
		map.with(GraphEdge.create(space, person));
		map.with(subGraph);
		
		GraphConverter converter=new GraphConverter();
		writeJson("pattern.html", converter.convertToJson(map, false));
	}
	
	
	
	@Test
	public void testPetaF() throws IOException {
		GraphList map = new GraphList();
		
		
		GraphClazz networkParser=map.with(new GraphClazz().with("NetworkParser"));
		GraphClazz networkParserfx=map.with(new GraphClazz().with("NetworkParserFX"));
		GraphClazz petaf=map.with(new GraphClazz().with("PetaF"));
		GraphClazz policy=map.with(new GraphClazz().with("Policy"));
		
		
		map.with( GraphEdge.create(networkParser, networkParserfx) );
		map.with( GraphEdge.create(networkParser, petaf) );
		map.with( GraphEdge.create(petaf, policy) );
		
		
		GraphConverter converter=new GraphConverter();
		writeJson("petaf.html", converter.convertToJson(map, false));
		
	}
	
			
	private void writeJson(String fileName, JsonObject item) throws IOException {
		StringBuilder sb=new StringBuilder();
		sb.append("<html><head>"+CRLF);
		sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css\">"+CRLF);
		sb.append("\t<script src=\"../src/main/resources/de/uniks/networkparser/graph/graph.js\"></script>"+CRLF);
		sb.append("\t<script src=\"../src/main/resources/de/uniks/networkparser/graph/dagre.min.js\"></script>"+CRLF);
		sb.append("\t<script src=\"../src/main/resources/de/uniks/networkparser/graph/drawer.js\"></script>"+CRLF);
		sb.append("\t<script src=\"../src/main/resources/de/uniks/networkparser/graph/jspdf.min.js\"></script>"+CRLF);
		sb.append("</head><body>"+CRLF);
		sb.append("<script language=\"Javascript\">"+CRLF);
		sb.append("\tvar json="+item.toString(2)+";"+CRLF);
		sb.append("\tnew Graph(json).layout();"+CRLF);
		sb.append("</script></body></html>");
		
		
		new File("build/").mkdir();
		FileWriter fstream = new FileWriter("build/"+fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(sb.toString());
		//Close the output stream
		out.close();
	}
}
