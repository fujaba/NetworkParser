package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.ObjectInstance;
import de.uniks.networkparser.graph.ObjectModel;
import de.uniks.networkparser.parser.TemplateResultFragment;

public class TestModel {

	@Test()
	public void testModel() {
		ObjectModel model = new ObjectModel();
		ObjectInstance alice = model.createObject("alice", "Person");
		alice.createAttribute("name", DataType.STRING).withValue("Alice");
		ObjectInstance uni = model.createObject("uni", "University");
		uni.createAttribute("name", DataType.STRING).withValue("Uni Kassel");
		
		uni.withBidirectional(alice, "student", Association.MANY, "owner", Association.ONE);
		
		
		GraphConverter converter = new GraphConverter();
		TemplateResultFragment convertToTestCode = converter.convertToTestCode(model, true);
		Assert.assertNotNull(convertToTestCode);
	}
	
    public static void main(String args[]) {
//    	  Result result = new Result();
//    	    RunListener listener = result.createListener();
//    	    RunNotifier notifier = new RunNotifier();
//    	    notifier.addFirstListener(listener);
//    	    TestCase testCase = new TestCase() {
//    	    };
//    	    TestListener adaptingListener = new NPListner();
//    	    adaptingListener.addFailure(testCase, new AssertionFailedError());
    }
}
