package de.uniks.networkparser.ext.promise;

import java.util.concurrent.Executor;

public class PromiseFactory {
//    /**
//     * A ConcurrentLinkedQueue to hold the callbacks for this Promise, so no additional synchronization is required to write to or read
//     * from the queue.
//     */
//    private final ConcurrentLinkedQueue<Runnable> callbacks;
//    
//    /**
//     * The executor to use for scheduled operations. If {@code null}, the default scheduled executor is used.
//     */
//    private final ScheduledExecutorService scheduledExecutor;
//
    private boolean allowCurrentThread = true;
    
    public boolean allowCurrentThread()
    {
       return allowCurrentThread;
    }
//
    /**
     * Returns the executor to use for callbacks.
     *
     * @return The executor to use for callbacks. This will be the default callback executor if {@code null} was specified for the callback
     *         executor when this PromiseFactory was created.
     */
    public Executor executor()
    {
//       if (callbackExecutor == null)
//       {
//          return DefaultExecutors.callbackExecutor();
//       }
//       return callbackExecutor;
        return null;
    }
}
