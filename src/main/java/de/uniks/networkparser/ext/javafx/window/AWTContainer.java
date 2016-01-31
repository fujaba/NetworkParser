package de.uniks.networkparser.gui.javafx.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
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
	* @param run					 The Runnable that has to be called on JFX thread.
	* @throws InterruptedException   the execution is interrupted.
	* @throws ExecutionException	 If a exception is occurred in the run method of the Runnable
	*/
	@Override
	public void run() {
		if (Platform.isFxApplicationThread() && !isDisposed) {
			lock.lock();
			condition = lock.newCondition();
			try {
				condition.await(1000, TimeUnit.MILLISECONDS);

				Platform.runLater(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit() {
		condition.signal();
		lock.unlock();
		isDisposed=true;
	}

	public StageEvent getStage() {
		return value;
	}
}
