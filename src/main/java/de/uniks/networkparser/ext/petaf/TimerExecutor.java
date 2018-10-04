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
import java.util.Timer;
import de.uniks.networkparser.DateTimeEntity;

public class TimerExecutor extends Timer implements TaskExecutor {
	private boolean isCancel;
	private Space space;
	private DateTimeEntity lastRun = new DateTimeEntity();

	public TimerExecutor withSpace(Space space) {
		this.space = space;
		return this;
	}

	@Override
	public Space getSpace() {
		return space;
	}

	public TimerExecutor(String value) {
		super(value != null ? value : "TimerExecutor");
	}

	@Override
	public void cancel() {
		this.isCancel = true;
		super.cancel();
	}

	public boolean isCancel() {
		return isCancel;
	}

	@Override
	public Object executeTask(Runnable task, int delay, int interval) {
		if (isCancel()) {
			return null;
		}
		SimpleTimerTask newTask;
		if (task instanceof SimpleTimerTask) {
			newTask = (SimpleTimerTask) task;
		} else {
			newTask = new SimpleTimerTask(space).withTask(task);
		}
		newTask.withDateTime(lastRun);
		if (interval > 0) {
			schedule(newTask, delay, interval);
		} else {
			schedule(newTask, delay);
		}
		return null;
	}

	@Override
	public Object executeTask(Runnable task, int delay) {
		if (isCancel()) {
			return null;
		}
		SimpleTimerTask newTask;
		if (task instanceof SimpleTimerTask) {
			newTask = (SimpleTimerTask) task;
		} else {
			newTask = new SimpleTimerTask(space).withTask(task);
		}
		newTask.withDateTime(lastRun);
		schedule(newTask, delay);
		return null;
	}

	@Override
	public void shutdown() {
		this.isCancel = true;
		this.cancel();
	}

	@Override
	public DateTimeEntity getLastRun() {
		return lastRun;
	}

	@Override
	public boolean handleMsg(Message message) {
		if (space != null) {
			return space.handleMsg(message);
		}
		return false;
	}
}
