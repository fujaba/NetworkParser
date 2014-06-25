/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uniks.networkparser;

/**
 * A simple logging interface abstracting logging APIs.  In order to be
 * instantiated successfully by Apache Common Logging, classes that implement
 * this interface must have a constructor that takes a single String
 * parameter representing the "name" of this Log.
 * <p>
 * The six logging levels used by <code>Log</code> are (in order):
 * <ol>
 * <li>trace (the least serious)</li>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal (the most serious)</li>
 * </ol>
 * The mapping of these log levels to the concepts used by the underlying
 * logging system is implementation dependent.
 * The implementation should ensure, though, that this ordering behaves
 * as expected.
 * <p>
 * Performance is often a logging concern.
 * By examining the appropriate property,
 * a component can avoid expensive operations (producing information
 * to be logged).
 * <p>
 * For example,
 * <code>
 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * </code>
 * <p>
 * Configuration of the underlying logging system will generally be done
 * external to the Logging APIs, through whatever mechanism is supported by
 * that system.
 *
 * @version $Id: Log.java 1432663 2013-01-13 17:24:18Z tn $
 */
public class NetworkParserLog {
	public static final String ERROR_TYP_PARSING="PARSING";
	public static final String ERROR_TYP_CONCURRENTMODIFICATION="CONCURRENTMODIFICATION";
	public static final String ERROR_TYP_NOCREATOR="NOCREATORFOUND";
	public static final String ERROR_TYP_DUPPLICATE="DUPPLICATE";
	private boolean isError=true;
    /**
     * Log a message with debug log level.
     *
     * @param owner The Element with call the Methods
     * @param method The Caller-Method
     * @param message log this message
     */
    public void debug(Object owner, String method, String message){
    	System.out.println("DEBUG: "+message);
    }

    /**
     * Log a message with info log level.
     *
     * @param owner The Element with call the Methods
     * @param method The Caller-Method
     * @param message log this message
     */
    public void info(Object owner, String method, String message){
    	System.out.println("INFO: "+message);
    }

    /**
     * Log a message with warn log level.
     *
     * @param owner The Element with call the Methods
     * @param method The Caller-Method
     * @param message log this message
     */
    public void warn(Object owner, String method, String message){
    	System.err.println("WARN: "+message);
    }

    /**
     * Log a message with error log level.
     * 
     * @param owner The Element with call the Methods
     * @param method The Caller-Method
     * @param params The Original Parameters
     * @return boolean if method must Cancel
     */
    public boolean error(Object owner, String method, String typ, Object... params){
    	return this.isError;
    }
    
	public boolean isError() {
		return isError;
	}

	/**
	 * @param value is Break for Error
	 * @return Itself
	 */
	public NetworkParserLog withError(boolean value) {
		this.isError = value;
		return this;
	}
}
