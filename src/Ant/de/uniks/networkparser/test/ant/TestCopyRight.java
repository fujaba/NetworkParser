package de.uniks.networkparser.test.ant;

import java.io.File;
import java.util.HashSet;
import de.uniks.networkparser.test.ant.sources.NetworkParserSources;
import de.uniks.networkparser.test.ant.sources.SourceItem;

public class TestCopyRight {

	public static void main(String[] args) {
		File file = new File( "src/de/uniks/networkparser/TextItems.java" );
		if (!file.exists()) {
			System.out.println("Copyrightfile does not exist");
			return;
		}

//		int fullLines=0;
//		SourceItem source= new SourceItem(file);
//		for (MethodItem method : source.getMethods().getItems()){
//			if(method.isMethod()){
//				System.out.println("Method: " +method.getName() +method.getLinesOfCodeMetric()+ " McCabe:" + method.getMcCabe());
//			}
//			fullLines += method.getLinesOfCodeMetric(). getFullLines();
//		}


//		System.out.println("FullLines: " +fullLines+ " - LOC: " +source.getLineOfCode() );

//		System.out.println("###############################");
//		System.out.println(source.getBody());
//		System.out.println("###############################");

		NetworkParserSources network= new NetworkParserSources();
		HashSet<SourceItem> sources = network.getSources("src/de/uniks/networkparser/");

//		HashSet<SourceItem> sources = new HashSet<SourceItem>();
//		sources.add(new SourceItem(new File( "src/de/uniks/networkparser/TextItems.java" )));
//		sources.add(new SourceItem(new File( "src/de/uniks/networkparser/Tokener.java" )));

//		for (SourceItem source : sources){
//			for (MethodItem method : source.getMethods().getItems()){
//				if(method.isMethod()){
//					System.out.println("Method: " +method.getName() +method.getLinesOfCodeMetric()+ " McCabe:" + method.getMcCabe());
//				}
//				fullLines += method.getLinesOfCodeMetric(). getFullLines();
//			}
//		}

		String packageGraph = network.getPackageGraph(sources);


//		http://yuml.me/diagram/class/[de.uniks.networkparser.gui.theme]-[java.util],[de.uniks.networkparser.gui.theme]-[de.uniks.networkparser.gui],[de.uniks.networkparser.gui]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.gui]-[java.util][java.util],[de.uniks.networkparser.interfaces]-[de.uniks.networkparser.bytes.converter],[de.uniks.networkparser.interfaces]-[java.beans],[de.uniks.networkparser.interfaces]-[de.uniks.networkparser.xml],[de.uniks.networkparser.interfaces]-[java.util],[de.uniks.networkparser]-[java.util],[de.uniks.networkparser]-[java.beans],[de.uniks.networkparser]-[de.uniks.networkparser.exceptions],[de.uniks.networkparser]-[de.uniks.networkparser.logic],[de.uniks.networkparser]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser]-[java.util.Map],[de.uniks.networkparser]-[de.uniks.networkparser.json],[de.uniks.networkparser.json]-[de.uniks.networkparser.logic],[de.uniks.networkparser.json]-[java.util.Map],[de.uniks.networkparser.json]-[de.uniks.networkparser.event],[de.uniks.networkparser.json]-[de.uniks.networkparser.event.creator],[de.uniks.networkparser.json]-[de.uniks.networkparser.sort],[de.uniks.networkparser.json]-[de.uniks.networkparser.exceptions],[de.uniks.networkparser.json]-[java.util],[de.uniks.networkparser.json]-[java.beans],[de.uniks.networkparser.json]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.json]-[de.uniks.networkparser.xml][java.beans],[de.uniks.networkparser.json.creator]-[de.uniks.networkparser.json],[de.uniks.networkparser.json.creator]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.gui.brush]-[java.util.regex],[de.uniks.networkparser.gui.brush]-[de.uniks.networkparser.gui],[de.uniks.networkparser.gui.brush]-[java.util][java.util.regex],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.exceptions],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.event.creator],[de.uniks.networkparser.bytes]-[java.io],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.event],[de.uniks.networkparser.bytes]-[java.util],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.bytes.creator],[de.uniks.networkparser.bytes]-[de.uniks.networkparser],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.bytes.converter],[de.uniks.networkparser.bytes]-[de.uniks.networkparser.interfaces][de.uniks.networkparser.bytes.converter],[de.uniks.networkparser.event.creator]-[de.uniks.networkparser.gui],[de.uniks.networkparser.event.creator]-[java.util],[de.uniks.networkparser.event.creator]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.event.creator]-[java.util.Map],[de.uniks.networkparser.event.creator]-[de.uniks.networkparser.event],[de.uniks.networkparser.event]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.event]-[java.util],[de.uniks.networkparser.event]-[java.util.Map],[de.uniks.networkparser.gui.table.creator]-[de.uniks.networkparser.gui.table],[de.uniks.networkparser.gui.table.creator]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.gui.table.creator]-[de.uniks.networkparser],[de.uniks.networkparser.gui.table]-[de.uniks.networkparser.gui],[de.uniks.networkparser.gui.table]-[de.uniks.networkparser],[de.uniks.networkparser.gui.table]-[de.uniks.networkparser.sort],[de.uniks.networkparser.gui.table]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.gui.table]-[java.beans],[de.uniks.networkparser.gui.table]-[java.util],[de.uniks.networkparser.gui.grid]-[de.uniks.networkparser.gui],[de.uniks.networkparser.gui.grid]-[de.uniks.networkparser.calculator],[de.uniks.networkparser.gui.grid]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.gui.grid]-[java.util],[de.uniks.networkparser.gui.grid]-[java.beans],[de.uniks.networkparser.gui.grid]-[java.util.Map][java.util.Map],[de.uniks.networkparser.xml]-[de.uniks.networkparser.logic],[de.uniks.networkparser.xml]-[de.uniks.networkparser],[de.uniks.networkparser.xml]-[de.uniks.networkparser.exceptions],[de.uniks.networkparser.xml]-[java.util.Map],[de.uniks.networkparser.xml]-[de.uniks.networkparser.gui],[de.uniks.networkparser.xml]-[org.xml.sax],[de.uniks.networkparser.xml]-[java.util],[de.uniks.networkparser.logic]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.logic]-[java.util][java.io][de.uniks.networkparser.exceptions],[de.uniks.networkparser.bytes.checksum]-[de.uniks.networkparser.bytes.converter],[de.uniks.networkparser.calculator]-[de.uniks.networkparser],[de.uniks.networkparser.calculator]-[java.util],[de.uniks.networkparser.yuml]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.yuml]-[java.util],[de.uniks.networkparser.yuml]-[de.uniks.networkparser],[de.uniks.networkparser.yuml]-[java.util.Map],[de.uniks.networkparser.xml.creator]-[java.util],[de.uniks.networkparser.xml.creator]-[de.uniks.networkparser.gui],[de.uniks.networkparser.xml.creator]-[de.uniks.networkparser],[de.uniks.networkparser.xml.creator]-[de.uniks.networkparser.xml],[de.uniks.networkparser.xml.creator]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.sort]-[de.uniks.networkparser],[de.uniks.networkparser.sort]-[java.util],[de.uniks.networkparser.sort]-[de.uniks.networkparser.interfaces],[de.uniks.networkparser.bytes.creator]-[de.uniks.networkparser.interfaces][org.xml.sax],[de.uniks.networkparser.date]-[de.uniks.networkparser],[de.uniks.networkparser.date]-[java.util]

//		packageGraph = packageGraph.replace("[de.uniks.networkparser.", "[");
//		packageGraph = packageGraph.replace("[de.uniks.networkparser]", "[.]");
//		packageGraph = packageGraph.replace("[bytes]", "[1]");
//		packageGraph = packageGraph.replace("[bytes.checksum]", "[2]");
//		packageGraph = packageGraph.replace("[bytes.converter]", "[3]");
//		packageGraph = packageGraph.replace("[bytes.creator]", "[4]");
//		packageGraph = packageGraph.replace("[calculator]", "[5]");
//		packageGraph = packageGraph.replace("[date]", "[6]");
//		packageGraph = packageGraph.replace("[event]", "[7]");
//		packageGraph = packageGraph.replace("[event.creator]", "[8]");
//		packageGraph = packageGraph.replace("[exceptions]", "[9]");
//		packageGraph = packageGraph.replace("[gui]", "[10]");
//		packageGraph = packageGraph.replace("[gui.brush]", "[11]");
//		packageGraph = packageGraph.replace("[gui.grid]", "[12]");
//		packageGraph = packageGraph.replace("[gui.table]", "[13]");
//		packageGraph = packageGraph.replace("[gui.table.creator]", "[14]");
//		packageGraph = packageGraph.replace("[gui.theme]", "[15]");
//		packageGraph = packageGraph.replace("[interfaces]", "[16]");
//		packageGraph = packageGraph.replace("[json]", "[17]");
//		packageGraph = packageGraph.replace("[json.creator]", "[18]");
//		packageGraph = packageGraph.replace("[logic]", "[19]");
//		packageGraph = packageGraph.replace("[event.creator]", "[8]");
//		packageGraph = packageGraph.replace("[event.creator]", "[8]");
//		packageGraph = packageGraph.replace("[checksum]", "[1]");

		System.out.println(packageGraph);

	}
}
