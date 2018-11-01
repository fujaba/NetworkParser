package de.uniks.networkparser.ext.generic;

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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class SimpleSLF4JProvider implements InvocationHandler {
	// public class SimpleSLF4JProvider implements SLF4JServiceProvider,
	// InvocationHandler {
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.8.99"; // !final
	private Object proxy;
	private SimpleKeyValueList<String, String> map = new SimpleKeyValueList<String, String>();
	// Logger Parameter
	private NetworkParserLog logger;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// IMarkerFactory
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
		// Must be method from Logger
		if ("isTraceEnabled".equals(m)) {
			if (args != null && args.length > 0) {
				return isTraceEnabled(args[0]);
			}
			return isTraceEnabled();
		}
		if ("trace".equals(m)) {
			if (args != null && args.length > 0 && args[0] instanceof String) {
				this.trace((String) args[0], convertConvert(args));
			} // not allowed trace(Marker marker, ?)
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
			} // not allowed debug(Marker marker, ?)
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
			} // not allowed debug(Marker marker, ?)
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
			} // not allowed debug(Marker marker, ?)
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
			} // not allowed debug(Marker marker, ?)
		}
		if ("getName".equals(m)) {
			return getName();
		}
		return null;
	}

	public NetworkParserLog getLogger() {
		if (this.logger == null) {
			this.logger = new NetworkParserLog().withListener(new StringPrintStream());
		}
		return logger;
	}

	public SimpleSLF4JProvider withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	public SimpleSLF4JProvider withLevel(byte flag) {
		getLogger().withFlag(flag);
		return this;
	}

	public SimpleSLF4JProvider withListener(ObjectCondition condition) {
		getLogger().withListener(condition);
		return this;
	}

	private String getName() {
		return "SimpleNPLogger";
	}

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

	public Object getLoggerFactory() {
		return proxy;
	}

	public Object getMarkerFactory() {
		return proxy;
	}

	public Object getMDCAdapter() {
		return proxy;
	}

	public String getRequesteApiVersion() {
		return REQUESTED_API_VERSION;
	}

	public void initialize() {
		Class<?>[] proxies = new Class[4];
		proxies[0] = ReflectionLoader.getClass("org.slf4j.ILoggerFactory");
		proxies[1] = ReflectionLoader.getClass("org.slf4j.IMarkerFactory");
		proxies[2] = ReflectionLoader.getClass("org.slf4j.spi.MDCAdapter");
		proxies[3] = ReflectionLoader.getClass("org.slf4j.Logger");

		this.proxy = java.lang.reflect.Proxy.newProxyInstance(SimpleSLF4JProvider.class.getClassLoader(), proxies,
				this);
	}

	// LOGGER
	public boolean isTraceEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_TRACE);
	}

	public void trace(String msg, Object... values) {
		getLogger().trace(this, null, msg, values);
	}

	public boolean isDebugEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_DEBUG);
	}

	public void debug(String msg, Object... values) {
		getLogger().debug(this, null, msg);
	}

	public boolean isInfoEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_INFO);
	}

	public void info(String msg, Object... values) {
		getLogger().info(this, null, msg, values);
	}

	public boolean isWarnEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_WARNING);
	}

	public void warn(String msg, Object... values) {
		getLogger().warn(this, null, msg, values);
	}

	public boolean isErrorEnabled(Object... marker) {
		return getLogger().isLevel(NetworkParserLog.LOGLEVEL_ERROR);
	}

	public void error(String msg, Object... values) {
		getLogger().error(this, null, msg, values);
	}

	// ILoggerFactory
	public Object getLogger(String name) {
		return this.proxy;
	}

	// MARKER
	public Object getMarker(String name) {
		return null;
	}

	public boolean exists(String name) {
		return false;
	}

	public boolean detachMarker(String name) {
		return false;
	}

	public Object getDetachedMarker(String name) {
		return null;
	}

	// MDCAdapter
	public void put(String key, String value) {
		this.map.put(key, value);
	}

	public String get(String key) {
		return this.map.get(key);
	}

	public void remove(String key) {
		this.map.remove(key);
	}

	public void clear() {
		this.map.clear();
	}

	public Map<String, String> getCopyOfContextMap() {
		return this.map;
	}

	public void setContextMap(Map<String, String> contextMap) {
		this.map.withMap(contextMap);
	}
}
