package de.uniks.networkparser.test;

import java.lang.reflect.Method;

import org.junit.Test;

import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;

public class RefactoringMethodBody {
	@Test
	public void testMethodBody() throws NoSuchMethodException, SecurityException {
		Apple apple = new Apple();
		Method method = apple.getClass().getMethod("setOwner", AppleTree.class);
		
		method.getModifiers();
	}
}
