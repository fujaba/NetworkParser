package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
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
