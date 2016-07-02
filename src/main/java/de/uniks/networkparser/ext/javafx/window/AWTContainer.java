package de.uniks.networkparser.ext.javafx.window;

/*
NetworkParser
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;

public class AWTContainer implements Runnable{
	private StageEvent value;
	private Lock lock;
	private Condition condition;
	private boolean isDisposed=false;

	public AWTContainer(StageEvent value) {
		this.value = value;
		lock = new ReentrantLock();
	}

	/* Invokes a Runnable in JFX Thread and waits while it's finished. Like
	* SwingUtilities.invokeAndWait does for EDT.
	*
	* @param run					The Runnable that has to be called on JFX thread.
	* @throws InterruptedException	the execution is interrupted.
	* @throws ExecutionException	If a exception is occurred in the run method of the Runnable
	*/
	@Override
	public void run() {
		if (Platform.isFxApplicationThread() && !isDisposed) {
//			lock.lock();
			condition = lock.newCondition();
			try {
				if(condition.await(1000, TimeUnit.MILLISECONDS) == false) {
					return;
				}

				Platform.runLater(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit() {
		condition.signal();
//		lock.unlock();
		isDisposed=true;
	}

	public StageEvent getStage() {
		return value;
	}
}
