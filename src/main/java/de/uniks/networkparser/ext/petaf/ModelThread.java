package de.uniks.networkparser.ext.petaf;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.ErrorHandler;

public class ModelThread {
	private IdMap map;
	private ThreadPoolExecutor execute;
	private ErrorHandler errorHandler = new ErrorHandler();

	public ModelThread(IdMap map) {
		this.map = map;
		this.execute = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	}
	public void execute(Object element, String property, Object newValue, Object...values ) {
		Object oldValue = null;
		if(values != null && values.length>0) {
			oldValue = values[0];
		}
		UpdateModel msg = new UpdateModel(this, element, property, oldValue, newValue);
		this.execute.execute(msg);
	}

	public IdMap getMap() {
		return map;
	}
	

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
}
