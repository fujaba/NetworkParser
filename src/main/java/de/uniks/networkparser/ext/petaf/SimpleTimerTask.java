package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ErrorHandler;

/**
 * Simple Task.
 *
 * @author Stefan Lindel
 */
public class SimpleTimerTask extends TimerTask implements Callable<Object> {
	protected final ErrorHandler handler = new ErrorHandler();
	protected Runnable task;
	protected Space space;
	protected DateTimeEntity lastRun;
	private Thread simpleExit;
	private SimpleEvent event;
	private ModelExecutor executor;

	/**
	 * Instantiates a new simple timer task.
	 *
	 * @param event the event
	 * @param executor the executor
	 */
	public SimpleTimerTask(SimpleEvent event, ModelExecutor executor) {
		this.event = event;
		this.executor = executor;
	}

	/**
	 * Instantiates a new simple timer task.
	 *
	 * @param simpleExit the simple exit
	 */
	public SimpleTimerTask(Thread simpleExit) {
		this.simpleExit = simpleExit;
	}

	/**
	 * Instantiates a new simple timer task.
	 *
	 * @param space the space
	 */
	public SimpleTimerTask(Space space) {
		handler.addListener(space);
		this.space = space;
	}

	/**
	 * With date time.
	 *
	 * @param entity the entity
	 * @return the simple timer task
	 */
	public SimpleTimerTask withDateTime(DateTimeEntity entity) {
		this.lastRun = entity;
		return this;
	}

	/**
	 * With simple exit.
	 *
	 * @param value the value
	 * @return the simple timer task
	 */
	public SimpleTimerTask withSimpleExit(Thread value) {
		this.simpleExit = value;
		return this;
	}

	/**
	 * Gets the last run.
	 *
	 * @return the last run
	 */
	public DateTimeEntity getLastRun() {
		return lastRun;
	}

	/**
	 * With event.
	 *
	 * @param event the event
	 * @return the simple timer task
	 */
	public SimpleTimerTask withEvent(SimpleEvent event) {
		this.event = event;
		return this;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (simpleExit != null) {
			simpleExit.interrupt();
		}
		try {
			updateLastRun();
			runTask();
		} catch (Exception e) {
			handler.saveException(e, false);
		}
	}

	/**
	 * Update last run.
	 */
	public void updateLastRun() {
		if (lastRun != null) {
			lastRun.withValue(System.currentTimeMillis());
		}
		if (space != null) {
			space.withLastTimerRun(lastRun);
		}
	}

	/**
	 * Run task.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean runTask() throws Exception {
		if (this.task != null) {
			task.run();
			return true;
		}
		return false;
	}

	/**
	 * With task.
	 *
	 * @param task the task
	 * @return the simple timer task
	 */
	public SimpleTimerTask withTask(Runnable task) {
		this.task = task;
		return this;
	}

	/**
	 * Gets the space.
	 *
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	/**
	 * Call.
	 *
	 * @return the object
	 * @throws Exception the exception
	 */
	@Override
	public Object call() throws Exception {
		if (this.event == null || this.executor == null) {
			return null;
		}
		return this.executor.execute(event);
	}
}
