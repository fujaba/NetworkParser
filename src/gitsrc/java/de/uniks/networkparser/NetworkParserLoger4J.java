package de.uniks.networkparser;

import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import de.uniks.networkparser.ext.io.StringPrintStream;

public class NetworkParserLoger4J extends NetworkParserLog implements SLF4JServiceProvider, ILoggerFactory, IMarkerFactory, MDCAdapter, Logger {
//	private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<String, Marker>();

	@Override
	public void initialize() {
		super.initialize();
		this.condition = new StringPrintStream();
	}
	
    public void clear() {
    }

    public String get(String key) {
        return null;
    }
    public void put(String key, String val) {
    }

    public void remove(String key) {
    }

    public Map<String, String> getCopyOfContextMap() {
        return null;
    }

    public void setContextMap(Map<String, String> contextMap) {
        // NOP
    }
    
	public boolean detachMarker(String arg0) {
		return false;
	}

	public boolean exists(String arg0) {
		return false;
	}
	
	@Override
	public ILoggerFactory getLoggerFactory() {
		return this;
	}

	@Override
	public IMarkerFactory getMarkerFactory() {
		return this;
	}

	@Override
	public MDCAdapter getMDCAdapter() {
		return this;
	}

	@Override
	public Marker getDetachedMarker(String arg0) {
		return null;
	}

	@Override
	public Marker getMarker(String arg0) {
		return null;
	}

	@Override
	public Logger getLogger(String arg0) {
		return this;
	}

	@Override
	public void debug(String arg0) {
		super.log(DEBUG, arg0);
	}

	@Override
	public void debug(String arg0, Object arg1) {
		super.log(DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, Object... arg1) {
		super.log(DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		super.log(DEBUG, arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, String arg1) {
		super.log(DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		super.log(DEBUG, arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		super.log(DEBUG, arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object... arg2) {
		super.log(DEBUG, arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		super.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		super.log(DEBUG, arg0, arg1, arg2, arg3);
	}

	@Override
	public void error(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void info(String arg0) {
		log(INFO, arg0); 
	}

	@Override
	public void info(String arg0, Object arg1) {
		log(INFO, arg0, arg1); 
	}

	@Override
	public void info(String arg0, Object... arg1) {
		log(INFO, arg0, arg1); 
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		log(INFO, arg0, arg1); 
	}

	@Override
	public void info(Marker arg0, String arg1) {
		log(INFO, arg0, arg1); 
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		log(INFO, arg0, arg1, arg2); 
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		log(INFO, arg0, arg1, arg2); 
	}

	@Override
	public void info(Marker arg0, String arg1, Object... arg2) {
		log(INFO, arg0, arg1, arg2); 
	}

	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		log(INFO, arg0, arg1, arg2); 
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(INFO, arg0, arg1, arg2, arg3); 
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public boolean isDebugEnabled(Marker arg0) {
		return false;
	}

	@Override
	public boolean isErrorEnabled() {
		return false;
	}

	@Override
	public boolean isErrorEnabled(Marker arg0) {
		return false;
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public boolean isInfoEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		return false;
	}

	@Override
	public boolean isTraceEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWarnEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

}