package de.uniks.networkparser.ext.generic;

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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class SimpleSLF4JProvider.
 *
 * @author Stefan
 */
public class SimpleSLF4JProvider implements InvocationHandler {
	
	/** The requested api version. */
	/*
	 * public class SimpleSLF4JProvider implements SLF4JServiceProvider,
	 * InvocationHandler { to avoid constant folding by the compiler, this field
	 * must *not* be final
	 */
	public static String REQUESTED_API_VERSION = "1.8.99";
	private Object proxy;
	private SimpleKeyValueList<String, String> map = new SimpleKeyValueList<String, String>();
	/* Logger Parameter */
	private NetworkParserLog logger;

	/**
	 * Invoke.
	 *
	 * @param proxy the proxy
	 * @param method the method
	 * @param args the args
	 * @return the object
	 * @throws Throwable the throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method == null) {
			return null;
		}
		/* IMarkerFactory */
		String m = method.getName();
		if ("getMarker".equals(m)) {
			return getMarker((String) args[0]);
		}
		if ("exists".equals(m)) {
			return exists((String) args[0]);
		}
		if ("detachMarker".equals(m)) {
			return detachMarker((String) args[0]);
		}
		if ("getDetachedMarker".equals(m)) {
			return getDetachedMarker((String) args[0]);
		}
		if ("getLogger".equals(m)) {
			return getLogger((String) args[0]);
		}
		/* Must be method from Logger */
		if ("isTraceEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isTraceEnabled(args[0]);
			}
			return isTraceEnabled();
		}
		if ("trace".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.trace((String) args[0], convertConvert(args));
			} /* not allowed trace(Marker marker, ?) */
		}
		if ("isDebugEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isDebugEnabled(args[0]);
			}
			return isDebugEnabled();
		}
		if ("debug".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.debug((String) args[0], convertConvert(args));
			} /* not allowed debug(Marker marker, ?) */
		}
		if ("isInfoEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isInfoEnabled(args[0]);
			}
			return isInfoEnabled();
		}
		if ("info".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.info((String) args[0], convertConvert(args));
			} /* not allowed debug(Marker marker, ?) */
		}
		if ("isWarnEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isWarnEnabled(args[0]);
			}
			return isWarnEnabled();
		}
		if ("warn".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.warn((String) args[0], convertConvert(args));
			} /* not allowed debug(Marker marker, ?) */
		}
		if ("isErrorEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isErrorEnabled(args[0]);
			}
			return isErrorEnabled();
		}
		if ("error".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.error((String) args[0], convertConvert(args));
			} /* not allowed debug(Marker marker, ?) */
		}
		if ("getName".equals(m)) {
			return getName();
		}
		return null;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	public NetworkParserLog getLogger() {
		if (this.logger == null) {
			this.logger = new NetworkParserLog().withListener(new StringPrintStream());
		}
		return logger;
	}

	/**
	 * With logger.
	 *
	 * @param logger the logger
	 * @return the simple SLF 4 J provider
	 */
	public SimpleSLF4JProvider withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * With level.
	 *
	 * @param flag the flag
	 * @return the simple SLF 4 J provider
	 */
	public SimpleSLF4JProvider withLevel(byte flag) {
		getLogger().withFlag(flag);
		return this;
	}

	/**
	 * With listener.
	 *
	 * @param condition the condition
	 * @return the simple SLF 4 J provider
	 */
	public SimpleSLF4JProvider withListener(ObjectCondition condition) {
		getLogger().withListener(condition);
		return this;
	}

	private String getName() {
		return "SimpleNPLogger";
	}

	/**
	 * Convert convert.
	 *
	 * @param param the param
	 * @return the object[]
	 */
	public Object[] convertConvert(Object[] param) {
		if (param == null || param.length < 1) {
			return new Object[0];
		}
		Object[] result = new Object[param.length - 1];
		for (int i = 1; i < param.length; i++) {
			result[i - 1] = param[i];
		}
		return result;
	}

	/**
	 * Gets the logger factory.
	 *
	 * @return the logger factory
	 */
	public Object getLoggerFactory() {
		return proxy;
	}

	/**
	 * Gets the marker factory.
	 *
	 * @return the marker factory
	 */
	public Object getMarkerFactory() {
		return proxy;
	}

	/**
	 * Gets the MDC adapter.
	 *
	 * @return the MDC adapter
	 */
	public Object getMDCAdapter() {
		return proxy;
	}

	/**
	 * Gets the requeste api version.
	 *
	 * @return the requeste api version
	 */
	public String getRequesteApiVersion() {
		return REQUESTED_API_VERSION;
	}

	/**
	 * Initialize.
	 */
	public void initialize() {
		Class<?>[] proxies = new Class[4];
		proxies[0] = ReflectionLoader.getClass("org.slf4j.ILoggerFactory");
		proxies[1] = ReflectionLoader.getClass("org.slf4j.IMarkerFactory");
		proxies[2] = ReflectionLoader.getClass("org.slf4j.spi.MDCAdapter");
		proxies[3] = ReflectionLoader.getClass("org.slf4j.Logger");
		if (proxies[0] != null) {
			this.proxy = java.lang.reflect.Proxy.newProxyInstance(SimpleSLF4JProvider.class.getClassLoader(), proxies,
					this);
		}
	}

	/**
	 * Checks if is trace enabled.
	 *
	 * @param marker the marker
	 * @return true, if is trace enabled
	 */
	/* LOGGER */
	public boolean isTraceEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_TRACE);
	}

	/**
	 * Trace.
	 *
	 * @param msg the msg
	 * @param values the values
	 */
	public void trace(String msg, Object... values) {
		getLogger().trace(this, null, msg, values);
	}

	/**
	 * Checks if is debug enabled.
	 *
	 * @param marker the marker
	 * @return true, if is debug enabled
	 */
	public boolean isDebugEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_DEBUG);
	}

	/**
	 * Debug.
	 *
	 * @param msg the msg
	 * @param values the values
	 */
	public void debug(String msg, Object... values) {
		getLogger().debug(this, null, msg);
	}

	/**
	 * Checks if is info enabled.
	 *
	 * @param marker the marker
	 * @return true, if is info enabled
	 */
	public boolean isInfoEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_INFO);
	}

	/**
	 * Info.
	 *
	 * @param msg the msg
	 * @param values the values
	 */
	public void info(String msg, Object... values) {
		getLogger().info(this, null, msg, values);
	}

	/**
	 * Checks if is warn enabled.
	 *
	 * @param marker the marker
	 * @return true, if is warn enabled
	 */
	public boolean isWarnEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_WARNING);
	}

	/**
	 * Warn.
	 *
	 * @param msg the msg
	 * @param values the values
	 */
	public void warn(String msg, Object... values) {
		getLogger().warn(this, null, msg, values);
	}

	/**
	 * Checks if is error enabled.
	 *
	 * @param marker the marker
	 * @return true, if is error enabled
	 */
	public boolean isErrorEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_ERROR);
	}

	/**
	 * Error.
	 *
	 * @param msg the msg
	 * @param values the values
	 */
	public void error(String msg, Object... values) {
		getLogger().error(this, null, msg, values);
	}

	/**
	 * Gets the logger.
	 *
	 * @param name the name
	 * @return the logger
	 */
	/* ILoggerFactory */
	public Object getLogger(String name) {
		return this.proxy;
	}

	/**
	 * Gets the marker.
	 *
	 * @param name the name
	 * @return the marker
	 */
	/* MARKER */
	public Object getMarker(String name) {
		return null;
	}

	/**
	 * Exists.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean exists(String name) {
		return false;
	}

	/**
	 * Detach marker.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean detachMarker(String name) {
		return false;
	}

	/**
	 * Gets the detached marker.
	 *
	 * @param name the name
	 * @return the detached marker
	 */
	public Object getDetachedMarker(String name) {
		return null;
	}

	/* Methods for MDCAdapter */
	/**
	 * Put Value.
	 *
	 * @param key   Key of Value
	 * @param value Th Value
	 */
	public void put(String key, String value) {
		this.map.put(key, value);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String get(String key) {
		return this.map.get(key);
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	public void remove(String key) {
		this.map.remove(key);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.map.clear();
	}

	/**
	 * Gets the copy of context map.
	 *
	 * @return the copy of context map
	 */
	public Map<String, String> getCopyOfContextMap() {
		return this.map;
	}

	/**
	 * Sets the context map.
	 *
	 * @param contextMap the context map
	 */
	public void setContextMap(Map<String, String> contextMap) {
		this.map.withMap(contextMap);
	}
}
