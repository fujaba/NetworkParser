package de.uniks.networkparser.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Test;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphLabel;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.GraphPattern;
import de.uniks.networkparser.json.JsonObject;

public class SVGDrawerTest {
	public static final String CRLF = "\r\n";

	@Test
	public void testDrawClazz() throws IOException {
		GraphList map = new GraphList();


		Clazz space=map.with(new Clazz().with("Space"));
		Clazz modelHistory=map.with(new Clazz().with("ModelHistory"));
		Clazz networkNode=map.with(new Clazz().with("NetworkNode"));
		Clazz idMap=map.with(new Clazz().with("IdMap"));
		Clazz nodeProxy=map.with(new Clazz().with("NodeProxy"));
		Clazz message=map.with(new Clazz().with("Message"));

		// Methods
		networkNode.with(new Method("sendMessage", new Parameter(DataType.create(message)), new Parameter(nodeProxy)));

		// Attribute
		networkNode.with(new Attribute("online", DataType.BOOLEAN));

//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxyTCP"));
//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxy"));


		// Edges
		map.with( Association.create(space, modelHistory) );
		map.with( Association.create(space, networkNode) );
		map.with( Association.create(space, idMap) );
		map.with( Association.create(networkNode, nodeProxy) );

		GraphConverter converter=new GraphConverter();
		writeJson("clazzModel.html", converter.convertToJson(map, false));
	}

	@Test
	public void testPattern() throws IOException {
		GraphList map = new GraphList();

		GraphPattern space = map.with(new GraphPattern().with("Space"));
		GraphPattern modelHistory = map.with(new GraphPattern().with("Item").withBounds("create"));
		map.with(new GraphPattern().with("ModelHistory").withBounds("nac"));

		map.with( Association.create(space, modelHistory).with(GraphLabel.CREATE) );

		GraphList subGraph = new GraphList();
		GraphPattern person = subGraph.with(new GraphPattern().with("Person"));
		subGraph.withStyle("nac");

		map.with(Association.create(space, person));
		map.with(subGraph);

		GraphConverter converter=new GraphConverter();
		writeJson("pattern.html", converter.convertToJson(map, false));
	}



	@Test
	public void testPetaF() throws IOException {
		GraphList map = new GraphList();


		Clazz networkParser=map.with(new Clazz().with("NetworkParser"));
		Clazz networkParserfx=map.with(new Clazz().with("NetworkParserFX"));
		Clazz petaf=map.with(new Clazz().with("PetaF"));
		Clazz policy=map.with(new Clazz().with("Policy"));


		map.with( Association.create(networkParser, networkParserfx) );
		map.with( Association.create(networkParser, petaf) );
		map.with( Association.create(petaf, policy) );


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
