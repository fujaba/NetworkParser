 package de.uniks.networkparser.test.scrum;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
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
		
		task.withBidirectional(taskPart, "part", Association.MANY, "task", Association.ONE);
		task.withBidirectional(workprotocol, "update", Association.MANY, "task", Association.ONE);
		task.withBidirectional(board, "owner", Association.ONE, "task", Association.MANY);
		board.withBidirectional(swimline, "part", Association.MANY, "owner", Association.ONE);
		swimline.withBidirectional(task, "children", Association.MANY, "liesOn", Association.ONE);

//		model.withoutFeature(Feature.PatternObject);
//		model.withoutFeature(Feature.ALBERTsSets);
//		model.generate("gen");
//		model.dumpHTML("Scrum");
		
//	      StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
//
//	      StackTraceElement element = stackTrace[0];
//	      String callMethodName = element.getMethodName();
//	      String className = element.getClassName();
	}
	
}
