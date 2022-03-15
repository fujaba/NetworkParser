package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.xml.EMFTokener;
import de.uniks.networkparser.xml.XMLEntity;

public class EMFTest {

	@Test
	public void testEMF() {
//		StringBuffer value = readFile("testcase4-in.petrinet");
//		EMFIdMap map=new EMFIdMap();
//		Object decode = map.decode(value.toString());
//		out.println(decode);
	}

	@Test
	public void testEMFDecode() {
		StringBuffer value = DocEnvironment.readFile("railway.ecore");
		Object model = new IdMap().decodeEMF(value.toString());
		GraphList list = (GraphList) model;
		assertEquals(9, list.getClazzes().size());
		Clazz segment = list.getClazzes().get(0);
		assertEquals("RailwayContainer", segment.toString());
		assertEquals("[RailwayContainer]->[RailwayElement|id:int],[RailwayContainer]->[Route],[RailwayContainer]->[Semaphore|signal:Signal],[RailwayElement]^-[Route],[RailwayElement]^-[Semaphore],[RailwayElement]^-[Sensor],[RailwayElement]^-[SwitchPosition|position:Position],[RailwayElement]^-[TrackElement],[Route]->[Semaphore],[Route]->[Sensor],[Route]->[SwitchPosition],[Segment|length:int]-^[TrackElement],[Sensor]->[TrackElement],[Switch|currentPosition:Position]->[SwitchPosition],[Switch]-^[TrackElement],[TrackElement]<-[TrackElement]", model.toString());
	}

	@Test
	public void testEMFTTC2014() throws FileNotFoundException {
		IdMap map=new IdMap();
		StringBuffer value = DocEnvironment.readFile("imdb.movies");
		ArrayList<?> decode = (ArrayList<?>) map.decodeEMF(value.toString());
		assertEquals(0, decode.size());
	}

	@Test
	public void testXMITOEMF() throws FileNotFoundException {
		IdMap map=new IdMap();
		StringBuffer value = DocEnvironment.readFile("imdb.movies");
		GraphList decode = new GraphList();
		map.decodeEMF(value.toString(), decode);
		assertEquals(3, decode.getClazzes().size());
	}

	@Test
	public void testWriteEMF() {
		IdMap map=new IdMap();
		GraphList list=new GraphList();
		Clazz uni = list.createClazz("University");
		Clazz student = list.createClazz("Student");
		student.withAttribute("semester", DataType.INT);
		student.withAttribute("name", DataType.STRING);
		uni.withBidirectional(student, "student", Association.MANY, "university", Association.ONE);
		XMLEntity item = (XMLEntity) map.encode(list, new EMFTokener());

		XMLEntity root =(XMLEntity) item.getChild(0);
		StringBuilder sb=new StringBuilder();
		sb.append("<ecore:EPackage xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"model\" nsURI=\"http:///model.ecore\" nsPrefix=\"model\">"+
	    		"<eClassifiers xsi:type=\"ecore:EClass\" name=\"Student\">"+
	    		"<eAttributes name=\"name\" eType=\"ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString\"/>"+
	    		"<eAttributes name=\"semester\" eType=\"ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EInt\"/>"+
	    		"<eReferences name=\"university\" eType=\"#//University\" eOpposite=\"#//University/student\" upperBound=\"-1\"/>"+
	    		"</eClassifiers>"+
				"<eClassifiers xsi:type=\"ecore:EClass\" name=\"University\">"+
				"<eReferences name=\"student\" eType=\"#//Student\" eOpposite=\"#//Student/university\" upperBound=\"1\"/>"+
				"</eClassifiers>"+
	    		"</ecore:EPackage>");

		assertEquals(sb.toString(), root.toString());
	}

	@Test
	public void testImportXMI() {
		String xmi="<?xml version=\"1.0\" encoding=\"ASCII\"?>"
						+"<transitiongraph:TransitionGraph xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:transitiongraph=\"http://transitiongraph/1.0\">"
						+"<states outgoing=\"//@transitions.0 //@transitions.1 //@transitions.2 //@transitions.3 //@transitions.4 //@transitions.5 //@transitions.6 //@transitions.7\" incoming=\"//@transitions.24 //@transitions.29\" isInitial=\"true\"/>"
						+"<states id=\"1\" outgoing=\"//@transitions.8\" incoming=\"//@transitions.0\"/>"
						+"<states id=\"2\" outgoing=\"//@transitions.9\" incoming=\"//@transitions.1\"/>";
		IdMap map=new IdMap();
//		map.with(new GenericCreator())
		assertNotNull(xmi);
		assertNotNull(map);
		Object decode = map.decode(xmi);
		assertNotNull(decode);
	}

}
