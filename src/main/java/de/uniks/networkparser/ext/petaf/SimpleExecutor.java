package de.uniks.networkparser.ext.petaf;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
 * Simple Execution.
 *
 * @author Stefan Lindel
 */
public class SimpleExecutor implements TaskExecutor {
    private boolean isCancel;
    private DateTimeEntity lastRun = new DateTimeEntity();
    private Space space;
    private Timer executor;
    private ExecutorService executorService;

    public static SimpleExecutor createSimpleExecutor(String... name) {
        SimpleExecutor executor = new SimpleExecutor();
        if (name != null && name.length > 0) {
            executor.executor = new Timer(name[0]);
        } else {
            executor.executor = new Timer("TimerExecutor");
        }
        return executor;
    }

    /**
     * Cancel.
     */
    public void cancel() {
        this.isCancel = true;
        if (executor != null) {
            executor.cancel();
            executor = null;
        }
    }

    /**
     * Checks if is cancel.
     *
     * @return true, if is cancel
     */
    public boolean isCancel() {
        return isCancel;
    }

    /**
     * Execute task.
     *
     * @param task     the task
     * @param delay    the delay
     * @param interval the interval
     * @return the object
     */
    @Override
    public Object executeTask(Runnable task, int delay, int interval) {
        if (isCancel()) {
            return null;
        }
        try {
            this.lastRun.withValue(System.currentTimeMillis());
            SimpleTimerTask newTask;
            if (task instanceof SimpleTimerTask) {
                newTask = (SimpleTimerTask) task;
            } else {
                newTask = new SimpleTimerTask(space).withTask(task);
            }
            newTask.withDateTime(lastRun);
            if (executor != null) {
                if (interval > 0) {
                    this.executor.schedule(newTask, delay, interval);
                } else {
                    this.executor.schedule(newTask, delay);
                }
            }
            if (executorService != null) {
                executorService.execute(newTask);
            }
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

    /**
     * Execute task.
     *
     * @param task     the task
     * @param delay    the delay
     * @param interval the interval
     * @param unit     the TimeUnit of Delay
     * @return the object
     */
    public Object executeTaskAtFixedRate(Runnable task, int delay, int interval, TimeUnit unit) {
        try {
            this.lastRun.withValue(System.currentTimeMillis());
            if (executor instanceof ScheduledExecutorService) {
                return ((ScheduledExecutorService) this.executor).scheduleAtFixedRate(task, delay, interval, unit);
            }
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

    /**
     * Execute task.
     *
     * @param task  the task
     * @param delay the delay
     * @return the object
     */
    @Override
    public Object executeTask(Runnable task, int delay) {
        if (isCancel()) {
            return null;
        }
        try {
            this.lastRun.withValue(System.currentTimeMillis());
            if (task != null) {
                SimpleTimerTask newTask;
                if (task instanceof SimpleTimerTask) {
                    newTask = (SimpleTimerTask) task;
                } else {
                    newTask = new SimpleTimerTask(space).withTask(task);
                }
                newTask.withDateTime(lastRun);
                if (this.executor != null) {
                    this.executor.schedule(newTask, delay);
                    return null;
                }
                if (executorService != null) {
                    executorService.execute(task);
                    return null;
                }
                task.run();
            }
        } catch (Exception e) {
            if (space != null) {
                space.handleException(e);
            }
        }
        return null;
    }

    /**
     * Handle msg.
     *
     * @param message the message
     * @return true, if successful
     */
    @Override
    public boolean handleMsg(Message message) {
        if (space != null) {
            return space.handleMsg(message);
        }
        return false;
    }

    /**
     * Shutdown.
     */
    @Override
    public void shutdown() {
        this.cancel();
    }

    /**
     * With space.
     *
     * @param space the space
     * @return the simple executor
     */
    @Override
    public SimpleExecutor withSpace(Space space) {
        this.space = space;
        return this;
    }

    /**
     * Gets the space.
     *
     * @return the space
     */
    @Override
    public Space getSpace() {
        return space;
    }

    /**
     * Gets the last run.
     *
     * @return the last run
     */
    @Override
    public DateTimeEntity getLastRun() {
        return lastRun;
    }

    /**
     * With executor service.
     *
     * @param executor the executor
     * @return the task executor
     */
    public SimpleExecutor withExecutorService(ExecutorService executor) {
        this.executorService = executor;
        return this;
    }
}
