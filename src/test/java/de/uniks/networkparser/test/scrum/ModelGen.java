 package de.uniks.networkparser.test.scrum;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class ModelGen {
	@Test
	public void testGenModel() {
		ClassModel model=new ClassModel("de.uniks.simplescrum.model");

		// Story or Task
		Clazz task = model.createClazz("Task")
					.withAttribute("name", DataType.STRING)
					.withAttribute("description", DataType.STRING)
					.withAttribute("created", DataType.INT)
					.withAttribute("type", DataType.STRING)
					.withAttribute("estimate", DataType.STRING)
					.withAttribute("creater", DataType.STRING)
					.withAttribute("complexity", DataType.STRING);

		// All Property
		Clazz taskPart = model.createClazz("PartTask")
				.withAttribute("type", DataType.STRING)
				.withAttribute("value", DataType.STRING);
		
		
		Clazz workprotocol = model.createClazz("LogEntry")
				.withAttribute("type", DataType.STRING)
				.withAttribute("creater", DataType.STRING)
				.withAttribute("created", DataType.INT)
				.withAttribute("value", DataType.INT);
		
		
		Clazz boardElement = model.createClazz("BoardElement");
		
		Clazz board = model.createClazz("Board");
		Clazz swimline = model.createClazz("Line").withAttribute("caption",DataType.STRING);
		
		

		boardElement.withKidClazzes(board, swimline, workprotocol, taskPart, task);
		
		task.withBidirectional(taskPart, "part", Cardinality.MANY, "task", Cardinality.ONE);
		task.withBidirectional(workprotocol, "update", Cardinality.MANY, "task", Cardinality.ONE);
		task.withBidirectional(board, "owner", Cardinality.ONE, "task", Cardinality.MANY);
		board.withBidirectional(swimline, "part", Cardinality.MANY, "owner", Cardinality.ONE);
		swimline.withBidirectional(task, "children", Cardinality.MANY, "liesOn", Cardinality.ONE);

//		model.withoutFeature(Feature.PatternObject);
//		model.withoutFeature(Feature.ALBERTsSets);
//		model.generate("gen");
//		model.dumpHTML("Scrum");
		
//	      StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
//
//	      StackTraceElement element = stackTrace[0];
//	      String callMethodName = element.getMethodName();
//	      String className = element.getClassName();
//	      System.out.println(className+":"+callMethodName);
	}
	
}
