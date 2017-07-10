package de.uniks.networkparser.ext.javafx;

import java.lang.Thread.UncaughtExceptionHandler;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.json.JsonObject;

public final class DiagrammCallBack {
	private DiagramController owner;

	public DiagrammCallBack(DiagramController owner) {
		this.owner = owner;
	}

	public void exit() {
		System.out.println("Exit");
		ReflectionLoader.call("exit", ReflectionLoader.PLATFORM);
	}

	public void save(Object value) {
		this.owner.save(new JsonObject().withValue((String) value));
	}

	public String generate(String value) {
		try {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					DiagrammCallBack.this.owner.saveException(e);
				}
			});
			this.owner.generate(new JsonObject().withValue(value));
		} catch (RuntimeException e) {
			this.owner.saveException(e);
		} catch (Exception e) {
			this.owner.saveException(e);
		} catch (Throwable e) {
			this.owner.saveException(e);
		}
		return "";
	}
}
