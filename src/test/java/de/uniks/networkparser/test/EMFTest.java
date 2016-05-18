package de.uniks.networkparser.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
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
		Object model = new IdMap().decodeEMF(value.toString());
		GraphList list = (GraphList) model;
		Assert.assertEquals(9, list.getClazzes().size());
		Assert.assertEquals("[Segment|length:int]-^[TrackElement],[TrackElement]-^[RailwayElement|id:int],[TrackElement]^-[Switch|currentPosition:Position],[TrackElement]->[Sensor],[TrackElement]<-[TrackElement],[Switch]->[SwitchPosition|position:Position],[Route]-^[RailwayElement],[Route]->[Semaphore|signal:Signal],[Route]->[SwitchPosition],[Route]->[Semaphore],[Route]->[Sensor],[Route]<-[RailwayContainer],[Semaphore]-^[RailwayElement],[Semaphore]<-[RailwayContainer],[SwitchPosition]-^[RailwayElement],[RailwayElement]^-[Sensor],[RailwayElement]<-[RailwayContainer]", model.toString());
	}

	@Test
	public void testEMFTTC2014() throws FileNotFoundException {
		IdMap map=new IdMap();
		StringBuffer value = readFile("imdb.movies");
		ArrayList<?> decode = (ArrayList<?>) map.decodeEMF(value.toString());
		Assert.assertEquals(0, decode.size());
	}
	
	@Test
	public void testXMITOEMF() throws FileNotFoundException {
		IdMap map=new IdMap();
		StringBuffer value = readFile("imdb.movies");
		GraphList decode = new GraphList();
		map.decodeEMF(value.toString(), decode);
		Assert.assertEquals(3, decode.getClazzes().size());
	}
}
