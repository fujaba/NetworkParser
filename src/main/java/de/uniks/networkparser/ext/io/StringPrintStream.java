package de.uniks.networkparser.ext.io;

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
import java.io.OutputStream;
import java.io.PrintStream;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class StringPrintStream extends PrintStream implements ObjectCondition {
	private ErrorHandler handler;
	private boolean error;

	public StringPrintStream() {
		super(new StringOutputStream());
	}

	public StringPrintStream(ErrorHandler handler, boolean error) {
		super(new StringOutputStream());
		this.handler = handler;
		this.error = error;
	}

	public StringPrintStream(OutputStream out) {
		super(out);
	}

	public StringPrintStream withListener(ErrorHandler value) {
		this.handler = value;
		return this;
	}

	public void print(Object value) {
		if (handler != null && value != null) {
			handler.writeOutput(value.toString(), error);
		}
	}

	public void print(String value) {
		if (handler != null) {
			handler.writeOutput(value, error);
		}
	}

	public void println() {
		if (handler != null) {
			handler.writeOutput(BaseItem.CRLF, error);
		}
	}

	public void println(String value) {
		if (handler != null) {
			handler.writeOutput(value, error);
			handler.writeOutput(BaseItem.CRLF, error);
		}
	}

	public void println(Object value) {
		if (handler != null && value != null) {
			handler.writeOutput(value.toString(), error);
			handler.writeOutput(BaseItem.CRLF, error);
		}
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		Object newValue = event.getNewValue();
		if (newValue != null) {
			String msg = "" + newValue;
			if (msg.length() > 0) {
				if (NetworkParserLog.ERROR.equals(event.getType())) {
					System.err.println(event.getType() + ": " + msg);
				} else {
					System.out.println(event.getType() + ": " + msg);
				}
			}
		}
		return false;
	}
//	java.io.PrintStream.printf(String, Object...)
//	java.io.PrintStream.printf(Locale, String, Object...)
//	java.io.PrintStream.println(boolean)
//	java.io.PrintStream.println(char)
//	java.io.PrintStream.println(char[])
//	java.io.PrintStream.println(double)
//	java.io.PrintStream.println(float)
//	java.io.PrintStream.println(int)
//	java.io.PrintStream.println(long)
//	public void print(boolean value) {
//	public void print(char value) {
//	public void print(char[] value) {
//	public void print(double value) {
//	public void print(float value) {
//	public void print(int value) {
//	public void print(long value) {

}
