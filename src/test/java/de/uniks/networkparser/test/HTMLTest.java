package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.GUILine;
import de.uniks.networkparser.Style;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class HTMLTest {

	@Test
	public void testHTML(){
		EntityUtil html = new EntityUtil();
		String txt = "Stefan <Test>";
		String encode = html.encode(txt);
		Assert.assertEquals("Stefan &lt;Test&gt;", encode);

		Assert.assertEquals(txt, html.decode(encode));
	}
	@Test
	public void testSimpleHTMLFile(){
		HTMLEntity file=new HTMLEntity();
		file.withText("Hallo Welt");
		Style style = new Style().withBorder(GUIPosition.ALL, new GUILine().withColor("black").withWidth("1px"));
		style.toString();
		file.addStyle("Table", ".Table{border:1px solid black}");
		file.addStyle("Table", ".div{border:1px solid black}");
		file.withNewLine();
		file.withText("Second Line");
		Assert.assertNotNull(file.toString());
	}
	
	@Test
	public void testSimpleJSoup(){
		StringBuilder sb=new StringBuilder("<div id=\"mp-itn\">");
		sb.append("<ul>");
		sb.append("<li>");
		sb.append("<b>");
		sb.append("<a href=\"/wiki/2016_Kaikoura_earthquake\" title=\"2016 Kaikoura earthquake\">An earthquake</a>");
		sb.append("</b>");
		sb.append("</li>");
		sb.append("<li>Canadian singer, songwriter, and poet <b><a href=\"/wiki/Leonard_Cohen\" title=\"Leonard Cohen\">Leonard Cohen</a></b> <i>(pictured)</i> dies at the age of 82.</li>");
		sb.append("</ul>");
		sb.append("</div>");
		HTMLEntity entity = new HTMLEntity().with(sb.toString());
		XMLEntity list = entity.getElementsBy(EntityUtil.CLASS, "#mp-itn b a");
		Assert.assertEquals(2, list.sizeChildren());
		for(int i=0;i<list.sizeChildren();i++) {
			EntityList child = list.getChild(i);
			if(i==0) {
				Assert.assertEquals("<a href=\"/wiki/2016_Kaikoura_earthquake\" title=\"2016 Kaikoura earthquake\">An earthquake</a>", child.toString());
			} else {
				Assert.assertEquals("<a href=\"/wiki/Leonard_Cohen\" title=\"Leonard Cohen\">Leonard Cohen</a>", child.toString());
			}
		}
	}

}
