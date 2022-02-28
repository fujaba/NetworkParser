package de.uniks.networkparser.ext.promise;

import java.util.function.Consumer;

public class CallBackPromise<T> implements Runnable {
    public final static String SUCCESS="SUCCESS";
    public final static String FAILURE="FAILURE";
//    private final Consumer<?> callback;
    private final Object callback;
    
    private final Promise<T> promise;
    private final String type;
    
    public CallBackPromise(Consumer<?> callback, Promise<T> promise, String type) {
        this.callback = callback;
        this.promise = promise;
        this.type = type;
    }
    public CallBackPromise(Object callback, Promise<T> promise) {
        this.callback = callback;
        this.promise = promise;
        this.type = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            if(callback instanceof Consumer<?>) {
                Consumer<?> consumer = (Consumer<?>) callback;
                if (promise.getFailure() == null && SUCCESS.equalsIgnoreCase(type)) {
                    ((Consumer<T>)callback).accept(promise.getValue());
                } else if(FAILURE.equalsIgnoreCase(type)){
                    ((Consumer<? super Throwable>)callback).accept(promise.getFailure());
                }
            } else if(callback instanceof Runnable) {
                ((Runnable)callback).run();
            }
        } catch (Throwable e) {
            promise.fail(e);
        }
    }
    
    public String getType() {
        return type;
    }
}
