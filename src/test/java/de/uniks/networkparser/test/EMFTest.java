package de.uniks.networkparser.test;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EMFIdMap;
import de.uniks.networkparser.graph.GraphList;

public class EMFTest extends IOClasses{

	@Test
	public void testEMF() {
//		StringBuffer value = readFile("testcase4-in.petrinet");
//		EMFIdMap map=new EMFIdMap();
//		Object decode = map.decode(value.toString());
//		out.println(decode);
	}

	@Test
	public void testEMFDecode() {
		StringBuffer value = readFile("railway.ecore");
		GraphList model = EMFIdMap.decoding(value.toString());
		Assert.assertEquals(9, model.getClazzes().size()); 
		Assert.assertEquals("[Segment|length:int],[TrackElement]-[TrackElement],[Switch|currentPosition:Position]-[SwitchPosition|position:Position],[Route]-[SwitchPosition],[Route]-[RailwayContainer],[Semaphore|signal:Signal]-[Route],[Semaphore]-[Route],[RailwayElement|id:int]-[RailwayContainer],[Sensor]-[TrackElement]", model.toString());
	}
	
	@Test
	public void testEMFTTC2014() throws FileNotFoundException {
//		EMFIdMap map=new EMFIdMap();
//		String absolutePath = getAbsolutePath("imdb.movies");
//		String absolutePath = getAbsolutePath("railway.ecore");
//		Object decode = map.decode(new FileBuffer().withFile(absolutePath));
//		Assert.assertNotNull(decode.toString());
	}
}
