package de.uniks.networkparser.ext.petaf;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	public void execute(Object element, String property, Object newValue) {
		UpdateModel msg = new UpdateModel(this, element, property, newValue);
		this.execute.execute(msg);
	}
	
	public Future<?> submit(Object element, String property, Object newValue) {
		UpdateModel msg = new UpdateModel(this, element, property, newValue);
		return this.execute.submit((Callable<Object>)msg);
	}

	public CompletableFuture<?> update(Object element, String property, Object newValue) {
		UpdateModel msg = new UpdateModel(this, element, property, newValue);
		return CompletableFuture.supplyAsync(msg, this.execute);
	}
	
	public CompletableFuture<?> get(Object element, String property) {
		GetModel msg = new GetModel(this, element, property);
		return CompletableFuture.supplyAsync(msg, this.execute);
	}

	public IdMap getMap() {
		return map;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
}
