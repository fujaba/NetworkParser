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
import java.util.TimerTask;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.ext.ErrorHandler;

public class SimpleTimerTask extends TimerTask {
	protected final ErrorHandler handler = new ErrorHandler();
	protected Runnable task;
	protected Space space;
	protected DateTimeEntity lastRun;
	private Thread simpleExit;

	public SimpleTimerTask(Thread simpleExit) {
		this.simpleExit = simpleExit;
	}

	public SimpleTimerTask(Space space) {
		handler.addListener(space);
		this.space = space;
	}

	public SimpleTimerTask withDateTime(DateTimeEntity entity) {
		this.lastRun = entity;
		return this;
	}

	public SimpleTimerTask withSimpleExit(Thread value) {
		this.simpleExit = value;
		return this;
	}

	public DateTimeEntity getLastRun() {
		return lastRun;
	}

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

	public void updateLastRun() {
		if (lastRun != null) {
			lastRun.withValue(System.currentTimeMillis());
		}
		if (space != null) {
			space.withLastTimerRun(lastRun);
		}
	}

	public boolean runTask() throws Exception {
		if (this.task != null) {
			task.run();
			return true;
		}
		return false;
	}

	public SimpleTimerTask withTask(Runnable task) {
		this.task = task;
		return this;
	}

	public Space getSpace() {
		return space;
	}
}
