package de.uniks.networkparser.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.bytes.qr.ByteMatrix;
import de.uniks.networkparser.bytes.qr.DecoderResult;
import de.uniks.networkparser.bytes.qr.ErrorCorrectionLevel;
import de.uniks.networkparser.bytes.qr.QRCode;
import de.uniks.networkparser.bytes.qr.QRTokener;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphLabel;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphPattern;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.json.JsonObject;

public class SVGDrawerTest {
	public static final String CRLF = "\r\n";

	@Test
	public void testGenSVG() throws Exception {
		QRTokener tokener = new QRTokener();

		QRCode encode = tokener.encode("test", ErrorCorrectionLevel.Q);
		ByteMatrix matrix = encode.getMatrix();

		StringBuilder sb=new StringBuilder();
		sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n");
		int posx;
		int posy = 0;
		for(int y=0;y<matrix.getHeight();y++) {
			posx=0;
			for(int x=0;x<matrix.getWidth();x++) {
				if(matrix.get(x, y)>0) {
					sb.append("<rect x=\""+posx+"\" y=\""+posy+"\" width=\"3\" height=\"3\" fill=\"back\"/>");
				}else {
					sb.append("<rect x=\""+posx+"\" y=\""+posy+"\" width=\"3\" height=\"3\" fill=\"white\"/>");
				}
				posx +=3;
			}
			sb.append("\r\n");
			posy+=3;
		}
		sb.append("</svg>");
//		FileWriter writer=new FileWriter(new File("qr.svg"));
//		writer.write(sb.toString());
//		writer.close();

		DecoderResult decode = tokener.decode(matrix.getArray());
		Assert.assertEquals("test", decode.getText());
	}

	@Test
	public void testDrawClazz() throws IOException {
		GraphList map = new GraphList();

		Clazz space=map.with(new Clazz("Space"));
		Clazz modelHistory=map.with(new Clazz("ModelHistory"));
		Clazz networkNode=map.with(new Clazz("NetworkNode"));
		Clazz idMap=map.with(new Clazz("IdMap"));
		Clazz nodeProxy=map.with(new Clazz("NodeProxy"));
		Clazz message=map.with(new Clazz("Message"));

		// Methods
		networkNode.withMethod("sendMessage", DataType.VOID, new Parameter(DataType.create(message)), new Parameter(nodeProxy));

		// Attribute
		networkNode.withAttribute("online", DataType.BOOLEAN);

//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxyTCP"));
//		GraphClazz nodeProxyTCP=map.with(new GraphClazz().withClassName("NodeProxy"));

		// Edges
		GraphUtil.setAssociation(map, Association.create(space, modelHistory) );
		GraphUtil.setAssociation(map, Association.create(space, networkNode) );
		GraphUtil.setAssociation(map, Association.create(space, idMap) );
		GraphUtil.setAssociation(map, Association.create(networkNode, nodeProxy) );

		GraphConverter converter=new GraphConverter();
		writeJson("clazzModel.html", converter.convertToJson(map, false, false));
	}

	@Test
	public void testPattern() throws IOException {
		GraphList map = new GraphList();

		GraphPattern space = map.with(new GraphPattern().with("Space"));
		GraphPattern modelHistory = map.with(new GraphPattern().with("Item").withBounds("create"));
		map.with(new GraphPattern().with("ModelHistory").withBounds("nac"));

		GraphUtil.setAssociation(map, Association.create(space, modelHistory).with(GraphLabel.CREATE) );

		GraphList subGraph = new GraphList();
		GraphPattern person = subGraph.with(new GraphPattern().with("Person"));
		subGraph.withStyle("nac");

		GraphUtil.setAssociation(map, Association.create(space, person));
		map.with(subGraph);

		GraphConverter converter=new GraphConverter();
		writeJson("pattern.html", converter.convertToJson(map, false, false));
	}

	@Test
	public void testPetaF() throws IOException {
		GraphList map = new GraphList();

		Clazz networkParser=map.with(new Clazz("NetworkParser"));
		Clazz networkParserfx=map.with(new Clazz("NetworkParserFX"));
		Clazz petaf=map.with(new Clazz("PetaF"));
		Clazz policy=map.with(new Clazz("Policy"));

		GraphUtil.setAssociation(map, Association.create(networkParser, networkParserfx) );
		GraphUtil.setAssociation(map, Association.create(networkParser, petaf) );
		GraphUtil.setAssociation(map, Association.create(petaf, policy) );

		GraphConverter converter=new GraphConverter();
		writeJson("petaf.html", converter.convertToJson(map, false, false));
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
