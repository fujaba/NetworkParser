package de.uniks.networkparser.test;

import de.uniks.networkparser.ext.ErrorHandler;

public class ErrorHandlerTest {

	public static void main(String[] args) {
		ErrorHandler errorHandler = new ErrorHandler();
		errorHandler.withPath("error");
		
		System.out.println(errorHandler.getJVMStartUp().toString("dd.mm.yyyy HH:MM:SS"));
		errorHandler.saveHeapSpace(errorHandler.getPrefix());
	}
}
