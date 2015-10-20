package de.uniks.networkparser.test;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.emf.EMFIdMap;
import de.uniks.networkparser.emf.EMFUtil;
import de.uniks.networkparser.graph.GraphList;

public class EMFTest extends IOClasses{

	@Test
	public void testEMF() {
//		StringBuffer value = readFile("testcase4-in.petrinet");
//		EMFIdMap map=new EMFIdMap();
//		Object decode = map.decode(value.toString());
//		System.out.println(decode);
	}

	@Test
	public void testEMFDecode() {
		StringBuffer value = readFile("railway.ecore");
		GraphList model = EMFUtil.decode(value.toString());
		Assert.assertEquals(2, model.getClazzes().size()); 
		System.out.println(model);
	}
	
	@Test
	public void testEMFTTC2014() throws FileNotFoundException {
		EMFIdMap map=new EMFIdMap();
//		String absolutePath = getAbsolutePath("imdb.movies");
		String absolutePath = getAbsolutePath("railway.ecore");
//		Object decode = map.decode(new FileBuffer().withFile(absolutePath));
//		Assert.assertNotNull(decode.toString());
	}
}
