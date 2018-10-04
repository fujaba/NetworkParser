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
import de.uniks.networkparser.DateTimeEntity;

public class SimpleExecutor implements TaskExecutor {
	private DateTimeEntity lastRun = new DateTimeEntity();
	private Space space;

	@Override
	public Object executeTask(Runnable task, int delay, int interval) {
		try {
			this.lastRun.withValue(System.currentTimeMillis());
			if (task != null) {
				task.run();
			}
		} catch (Exception e) {
			if (space != null) {
				space.handleException(e);
			}
		}
		return null;
	}

	@Override
	public Object executeTask(Runnable task, int delay) {
		try {
			this.lastRun.withValue(System.currentTimeMillis());
			if (task != null) {
				task.run();
			}
		} catch (Exception e) {
			if (space != null) {
				space.handleException(e);
			}
		}
		return null;
	}

	@Override
	public boolean handleMsg(Message message) {
		if (space != null) {
			return space.handleMsg(message);
		}
		return false;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public SimpleExecutor withSpace(Space space) {
		this.space = space;
		return this;
	}

	@Override
	public Space getSpace() {
		return space;
	}

	@Override
	public DateTimeEntity getLastRun() {
		return lastRun;
	}
}
