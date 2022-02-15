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
import de.uniks.networkparser.DateTimeEntity;

/**
 * Interfae for Execution.
 *
 * @author Stefan Lindel
 */
public interface TaskExecutor {
	
	/**
	 * Execute task.
	 *
	 * @param task the task
	 * @param delay the delay
	 * @param interval the interval
	 * @return the object
	 */
	public Object executeTask(Runnable task, int delay, int interval);

	/**
	 * Execute task.
	 *
	 * @param task the task
	 * @param delay the delay
	 * @return the object
	 */
	public Object executeTask(Runnable task, int delay);

	/**
	 * Handle msg.
	 *
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean handleMsg(Message message);

	/**
	 * Shutdown.
	 */
	public void shutdown();

	/**
	 * With space.
	 *
	 * @param space the space
	 * @return the task executor
	 */
	public TaskExecutor withSpace(Space space);

	/**
	 * Gets the space.
	 *
	 * @return the space
	 */
	public Space getSpace();

	/**
	 * Gets the last run.
	 *
	 * @return the last run
	 */
	public DateTimeEntity getLastRun();
}
