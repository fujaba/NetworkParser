package de.uniks.networkparser.ext.promise;

import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class Promise<T> {
    /**
     * The default amount to wait for a promise to resolve.
     */
    public static final int DEFAULT_WAIT_TIMEOUT_MS = 120_000;

    /**
     * A CountDownLatch to manage the resolved state of this Promise.
     * <p>
     * This object is used as the synchronizing object to provide a critical section
     * in {@link #tryResolve(Object, Throwable)} so that only a single thread can
     * write the resolved state variables and open the latch.
     * <p>
     * The resolved state variables, {@link #value} and {@link #fail}, must only be
     * written when the latch is closed (getCount() != 0) and must only be read when
     * the latch is open (getCount() == 0). The latch state must always be checked
     * before writing or reading since the resolved state variables' memory
     * consistency is guarded by the latch.
     */
    private final CountDownLatch resolved = new CountDownLatch(1);

    /**
     * A ConcurrentLinkedQueue to hold the callbacks for this Promise, so no
     * additional synchronization is required to write to or read from the queue.
     */
    private Queue<CallBackPromise<?>> callBacks;

    /**
     * The value of this Promise if successfully resolved.
     *
     * @see #resolved
     */
    // @GuardedBy("resolved")
    private T value;

    /**
     * The failure of this Promise if resolved with a failure or {@code null} if
     * successfully resolved.
     *
     * @see #resolved
     */
    // @GuardedBy("resolved")
    private Throwable fail;

    private PromiseFactory factory;

    /**
     * Returns whether this Promise has been resolved.
     *
     * <p>
     * This Promise may be successfully resolved or resolved with a failure.
     *
     * @return {@code true} if this Promise was resolved either successfully or with
     *         a failure; {@code false} if this Promise is unresolved.
     */
    public boolean isDone() {
        return resolved.getCount() == 0;
    }

    /**
     * Returns the failure of this Promise.
     *
     * <p>
     * If this Promise is not {@link #isDone() resolved}, this method must block and
     * wait for this Promise to be resolved before completing.
     *
     * <p>
     * If this Promise was resolved with a failure, this method returns with the
     * failure of this Promise. If this Promise was successfully resolved, this
     * method must return {@code null}.
     *
     * @return The failure of this resolved Promise or {@code null} if this Promise
     *         was successfully resolved.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */

    public Throwable getFailure() throws InterruptedException {
        return getFailure(DEFAULT_WAIT_TIMEOUT_MS);
    }

    /**
     * The same as {@link #getFailure()}.
     *
     * @param timeoutMs Timeout
     * @return The failure of this resolved Promise or {@code null} if this Promise
     *         was successfully resolved.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    public Throwable getFailure(long timeoutMs) throws InterruptedException {
        // ensure latch open before reading state
        boolean success = resolved.await(timeoutMs, TimeUnit.MILLISECONDS);
        if (!success) {
            return new TimeoutException();
        }

        return fail;
    }

    public T getValue() throws InvocationTargetException, InterruptedException, TimeoutException {
        return getValue(DEFAULT_WAIT_TIMEOUT_MS);
    }

    /**
     * The same as {@link #getValue()}, but waits only the given amount of time for
     * the promise to resolve.
     *
     * @param timeoutMs amount of time to wait for the result.
     * @return The value of this resolved Promise.
     * @throws InvocationTargetException If this Promise was resolved with a
     *                                   failure. The cause of the
     *                                   {@code InvocationTargetException} is the
     *                                   failure exception.
     * @throws InterruptedException      If the current thread was interrupted while
     *                                   waiting.
     * @throws TimeoutException          if the given timeout is exceeded.
     */
    public T getValue(long timeoutMs) throws InvocationTargetException, InterruptedException, TimeoutException {
        boolean success = resolved.await(timeoutMs, TimeUnit.MILLISECONDS);
        if (!success) {
            throw new TimeoutException();
        }

        if (fail == null) {
            return value;
        }
        throw new InvocationTargetException(fail);
    }

    /**
     * Register a callback to be called when this Promise is resolved.
     * <p>
     * The specified callback is called when this Promise is resolved either
     * successfully or with a failure.
     * <p>
     * This method may be called at any time including before and after this Promise
     * has been resolved.
     * <p>
     * Resolving this Promise <i>happens-before</i> any registered callback is
     * called. That is, in a registered callback, {@link #isDone()} must return
     * {@code true} and {@link #getValue()} and {@link #getFailure()} must not
     * block.
     * <p>
     * A callback may be called on a different thread than the thread which
     * registered the callback. So the callback must be thread safe but can rely
     * upon that the registration of the callback <i>happens-before</i> the
     * registered callback is called.
     *
     * @param callback The callback to be called when this Promise is resolved. Must
     *                 not be {@code null}.
     * @return This Promise.
     */
    public Promise<T> onResolve(Runnable callback) {
        return onResolving(new CallBackPromise<T>(callback, this));
    }
    
    private Promise<T> onResolving(CallBackPromise<?> callback) {
        if (callback == null) {
            return this;
        }
        if (factory.allowCurrentThread() && isDone()) {
            callback.run();
        } else {
            if (callBacks == null) {
                callBacks = new ConcurrentLinkedQueue<>();
            }
            callBacks.offer(callback);
            notifyCallbacks(); // call any registered callbacks
        }
        return this;
    }
    

    /**
     * Call any registered callbacks if this Promise is resolved.
     */
    void notifyCallbacks() {
        if (!isDone()) {
            return; // return if not resolved
        }
        /*
         * Note: multiple threads can be in this method removing callbacks from the
         * queue and executing them, so the order in which callbacks are executed cannot
         * be specified.
         */
        if (this.fail == null) {
            try {
                for (CallBackPromise<?> callback = callBacks.poll(); callback != null; callback = callBacks.poll()) {
                    factory.executor().execute(callback);
                }
            } catch (RejectedExecutionException e) {
                this.fail(e);
            }
        }
    }

    /**
     * Register a callback to be called with the result of this Promise when this
     * Promise is resolved successfully. The callback will not be called if this
     * Promise is resolved with a failure.
     * <p>
     * This method may be called at any time including before and after this Promise
     * has been resolved.
     * <p>
     * Resolving this Promise <i>happens-before</i> any registered callback is
     * called. That is, in a registered callback, {@link #isDone()} must return
     * {@code true} and {@link #getValue()} and {@link #getFailure()} must not
     * block.
     * <p>
     * A callback may be called on a different thread than the thread which
     * registered the callback. So the callback must be thread safe but can rely
     * upon that the registration of the callback <i>happens-before</i> the
     * registered callback is called.
     *
     * @param success The Consumer callback that receives the value of this Promise.
     *                Must not be {@code null}.
     * @return This Promise.
     * @since 1.1
     */
    public Promise<T> onSuccess(Consumer<? super T> success) {
        return onResolve(new CallBackPromise<T>(success, this, CallBackPromise.SUCCESS));
    }

    /**
     * Register a callback to be called with the failure for this Promise when this
     * Promise is resolved with a failure. The callback will not be called if this
     * Promise is resolved successfully.
     * <p>
     * This method may be called at any time including before and after this Promise
     * has been resolved.
     * <p>
     * Resolving this Promise <i>happens-before</i> any registered callback is
     * called. That is, in a registered callback, {@link #isDone()} must return
     * {@code true} and {@link #getValue()} and {@link #getFailure()} must not
     * block.
     * <p>
     * A callback may be called on a different thread than the thread which
     * registered the callback. So the callback must be thread safe but can rely
     * upon that the registration of the callback <i>happens-before</i> the
     * registered callback is called.
     *
     * @param failure The Consumer callback that receives the failure of this
     *                Promise. Must not be {@code null}.
     * @return This Promise.
     * @since 1.1
     */
    Promise<T> onFailure(Consumer<? super Throwable> failure) {
        return onResolve(new CallBackPromise<T>(failure, this, CallBackPromise.FAILURE));
    }

    public void fail(Throwable e) {
        this.fail  = e;
        notifyCallbacks();
    }

//    /**
//     * Chain a new Promise to this Promise with Success and Failure callbacks.
//     * <p>
//     * The specified {@link Success} callback is called when this Promise is
//     * successfully resolved and the specified {@link Failure} callback is called
//     * when this Promise is resolved with a failure.
//     * <p>
//     * This method returns a new Promise which is chained to this Promise. The
//     * returned Promise must be resolved when this Promise is resolved after the
//     * specified Success or Failure callback is executed. The result of the executed
//     * callback must be used to resolve the returned Promise. Multiple calls to this
//     * method can be used to create a chain of promises which are resolved in
//     * sequence.
//     * <p>
//     * If this Promise is successfully resolved, the Success callback is executed
//     * and the result Promise, if any, or thrown exception is used to resolve the
//     * returned Promise from this method. If this Promise is resolved with a
//     * failure, the Failure callback is executed and the returned Promise from this
//     * method is failed.
//     * <p>
//     * This method may be called at any time including before and after this Promise
//     * has been resolved.
//     * <p>
//     * Resolving this Promise <i>happens-before</i> any registered callback is
//     * called. That is, in a registered callback, {@link #isDone()} must return
//     * {@code true} and {@link #getValue()} and {@link #getFailure()} must not
//     * block.
//     * <p>
//     * A callback may be called on a different thread than the thread which
//     * registered the callback. So the callback must be thread safe but can rely
//     * upon that the registration of the callback <i>happens-before</i> the
//     * registered callback is called.
//     *
//     * @param <R>     The value type associated with the returned Promise.
//     * @param success The Success callback to be called when this Promise is
//     *                successfully resolved. May be {@code null} if no Success
//     *                callback is required. In this case, the returned Promise must
//     *                be resolved with the value {@code null} when this Promise is
//     *                successfully resolved.
//     * @param failure The Failure callback to be called when this Promise is
//     *                resolved with a failure. May be {@code null} if no Failure
//     *                callback is required.
//     * @return A new Promise which is chained to this Promise. The returned Promise
//     *         must be resolved when this Promise is resolved after the specified
//     *         Success or Failure callback, if any, is executed.
//     */
//    <R> Promise<R> then(Success<? super T, ? extends R> success, Failure failure);
//
//    /**
//     * Chain a new Promise to this Promise with a Success callback.
//     * <p>
//     * This method performs the same function as calling
//     * {@link #then(Success, Failure)} with the specified Success callback and
//     * {@code null} for the Failure callback.
//     *
//     * @param <R>     The value type associated with the returned Promise.
//     * @param success The Success callback to be called when this Promise is
//     *                successfully resolved. May be {@code null} if no Success
//     *                callback is required. In this case, the returned Promise must
//     *                be resolved with the value {@code null} when this Promise is
//     *                successfully resolved.
//     * @return A new Promise which is chained to this Promise. The returned Promise
//     *         must be resolved when this Promise is resolved after the specified
//     *         Success, if any, is executed.
//     * @see #then(Success, Failure)
//     */
//    <R> Promise<R> then(Success<? super T, ? extends R> success);
//
//    /**
//     * Chain a new Promise to this Promise with a Consumer callback that receives
//     * the value of this Promise when it is successfully resolved.
//     * <p>
//     * The specified {@link Consumer} is called when this Promise is resolved
//     * successfully.
//     * <p>
//     * This method returns a new Promise which is chained to this Promise. The
//     * returned Promise must be resolved when this Promise is resolved after the
//     * specified callback is executed. If the callback throws an exception, the
//     * returned Promise is failed with that exception. Otherwise the returned
//     * Promise is resolved with the success value from this Promise.
//     * <p>
//     * This method may be called at any time including before and after this Promise
//     * has been resolved.
//     * <p>
//     * Resolving this Promise <i>happens-before</i> any registered callback is
//     * called. That is, in a registered callback, {@link #isDone()} must return
//     * {@code true} and {@link #getValue()} and {@link #getFailure()} must not
//     * block.
//     * <p>
//     * A callback may be called on a different thread than the thread which
//     * registered the callback. So the callback must be thread safe but can rely
//     * upon that the registration of the callback <i>happens-before</i> the
//     * registered callback is called.
//     *
//     * @param consumer The Consumer callback that receives the value of this
//     *                 Promise. Must not be {@code null}.
//     * @return A new Promise which is chained to this Promise. The returned Promise
//     *         must be resolved when this Promise is resolved after the specified
//     *         Consumer is executed.
//     * @since 1.1
//     */
//    Promise<T> thenAccept(Consumer<? super T> consumer);
//
//    /**
//     * Filter the value of this Promise.
//     * <p>
//     * If this Promise is successfully resolved, the returned Promise must either be
//     * resolved with the value of this Promise, if the specified Predicate accepts
//     * that value, or failed with a {@code NoSuchElementException}, if the specified
//     * Predicate does not accept that value. If the specified Predicate throws an
//     * exception, the returned Promise must be failed with the exception.
//     * <p>
//     * If this Promise is resolved with a failure, the returned Promise must be
//     * failed with that failure.
//     * <p>
//     * This method may be called at any time including before and after this Promise
//     * has been resolved.
//     *
//     * @param predicate The Predicate to evaluate the value of this Promise. Must
//     *                  not be {@code null}.
//     * @return A Promise that filters the value of this Promise.
//     */
//    Promise<T> filter(Predicate<? super T> predicate);
//
//    /**
//     * Map the value of this Promise.
//     *
//     * <p>
//     * If this Promise is successfully resolved, the returned Promise must be
//     * resolved with the value of specified Function as applied to the value of this
//     * Promise. If the specified Function throws an exception, the returned Promise
//     * must be failed with the exception.
//     *
//     * <p>
//     * If this Promise is resolved with a failure, the returned Promise must be
//     * failed with that failure.
//     *
//     * <p>
//     * This method may be called at any time including before and after this Promise
//     * has been resolved.
//     *
//     * @param <R>    The value type associated with the returned Promise.
//     * @param mapper The Function that must map the value of this Promise to the
//     *               value that must be used to resolve the returned Promise. Must
//     *               not be {@code null}.
//     * @return A Promise that returns the value of this Promise as mapped by the
//     *         specified Function.
//     */
//    <R> Promise<R> map(Function<? super T, ? extends R> mapper);
//
//    /**
//     * FlatMap the value of this Promise.
//     *
//     * <p>
//     * If this Promise is successfully resolved, the returned Promise must be
//     * resolved with the Promise from the specified Function as applied to the value
//     * of this Promise. If the specified Function throws an exception, the returned
//     * Promise must be failed with the exception.
//     *
//     * <p>
//     * If this Promise is resolved with a failure, the returned Promise must be
//     * failed with that failure.
//     *
//     * <p>
//     * This method may be called at any time including before and after this Promise
//     * has been resolved.
//     *
//     * @param <R>    The value type associated with the returned Promise.
//     * @param mapper The Function that must flatMap the value of this Promise to a
//     *               Promise that must be used to resolve the returned Promise. Must
//     *               not be {@code null}.
//     * @return A Promise that returns the value of this Promise as mapped by the
//     *         specified Function.
//     */
//    <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> mapper);
//
//    /**
//     * Creates a new promise that relies on the the given {@link PromiseFactory}.
//     *
//     * @param promiseFactory The factory that should be used to executed further
//     *                       callbacks applied to the return promise.
//     * @return a new promise, that is resolved when this promise is resolved, but
//     *         uses the given {@link PromiseFactory}.
//     */
//    Promise<T> changeFactory(PromiseFactory promiseFactory);
}
