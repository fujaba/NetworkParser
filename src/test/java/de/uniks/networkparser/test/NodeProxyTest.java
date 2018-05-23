package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.xml.HTMLEntity;

public class NodeProxyTest {

	@Test
	public void UniversityOfMadness() {
		HTMLEntity answer = NodeProxyTCP.getHTTP("avocado.uniks.de", 33000, "user/info");
		System.out.println(answer);
	}

	@Test
	public void UniversityOfMadnessCreate() {
		HTMLEntity answer = NodeProxyTCP.postHTTP("avocado.uniks.de", 33000, "user/create", NodeProxyTCP.BODY_JSON, "Username", "Eraser");
		System.out.println(answer);
	}

}
