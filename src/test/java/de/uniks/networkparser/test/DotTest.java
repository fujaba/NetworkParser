package de.uniks.networkparser.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.DotIdMap;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.BaseItem;

public class DotTest {
	@Test
	public void testDotShort() {
		String item="strict graph ethane {1}";
		DotIdMap map=new DotIdMap();
		map.decode(item);
	}				
	
	@Test
	public void testDotShortest() {
		String item="graph{1}";
		DotIdMap map=new DotIdMap();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(1, list.getNodes().size());
	}	
	

	@Test
	public void testDotSimple() {
		String item="digraph G {"+BaseItem.CRLF
			  +"\"Welcome\" -> \"To\""+BaseItem.CRLF
			  +"\"To\" -> \"Web\""+BaseItem.CRLF
			  +"\"To\" -> \"GraphViz!\""+BaseItem.CRLF
			+"}";
		DotIdMap map=new DotIdMap();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(4, list.getNodes().size());
	}
	@Test
	public void testDotPM() {
		String item="graph smallworld {"+BaseItem.CRLF
			+"1 -> 2"+BaseItem.CRLF
			+"2 -- 3"+BaseItem.CRLF
			+"3 -- 4"+BaseItem.CRLF
			+"4 -- 1"+BaseItem.CRLF
			+"1 -- 5"+BaseItem.CRLF
			+"2 -- 5"+BaseItem.CRLF
			+"3 -- 5"+BaseItem.CRLF
			+"4 -- 5}";
		
		DotIdMap map=new DotIdMap();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(5, list.getNodes().size());
	}
	@Test
	public void testDotPMAttribute() {
		String item="graph smallworld {"+BaseItem.CRLF
			+"1[BONUS=2,ID=ISLAND] -- 2[BONUS=3]}";
		DotIdMap map=new DotIdMap();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(2, list.getNodes().size());
	}
	
	@Test
	public void testDotConverter() throws IOException {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz().with("Stefan").with("Student"));
		student.withUniDirectional(uni, "owner", Cardinality.ONE);
		
		DotIdMap map=new DotIdMap();
		String convert = map.convert(list, true);
		
		
		new File("build").mkdir();
		FileWriter fstream = new FileWriter("build/dotFile.dot");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(convert);
		//Close the output stream
		out.close();
		
	  //		String[] command = new String[] { makeimageFile, , "." };
		//		String path = "../GraphViz/win32/";
		//		 String[] command = new String[] { path+"dot", "build/dotFile.dot", "-Tsvg", "-o", "build/dotFile.svg" };
		//		 ProcessBuilder processBuilder = new ProcessBuilder(command);
		//		 processBuilder.redirectErrorStream(true);
		//		 processBuilder.redirectOutput(Redirect.INHERIT);
		//		 processBuilder.start();
	}
}
