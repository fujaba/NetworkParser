package test;

import org.junit.Test;

import de.uniks.factory.cpp.CppModelFactory;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphList;

public class TestCppAssociationFactory {

	@Test
	public void testBidirectional() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
	@Test
	public void testBidirectionalOneToOne() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "person", Cardinality.ONE);
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
	@Test
	public void testUnidirectionalOne() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withUniDirectional(room, "room", Cardinality.ONE);
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
	@Test
	public void testUnidirectionalMany() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withUniDirectional(room, "rooms", Cardinality.MANY);
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
}
