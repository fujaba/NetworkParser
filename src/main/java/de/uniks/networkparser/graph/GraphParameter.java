/*
   Copyright (c) 2014 zuendorf
 
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
 
   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.
 
   The Software shall be used for Good, not Evil.
 
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.graph;


public class GraphParameter extends GraphValue {
	public static final String PROPERTY_METHOD = "method";
	private GraphMethod method = null;

	protected GraphParameter() {

	}

	public GraphParameter(GraphDataType type) {
		with(type);
	}

	public GraphParameter(String name, GraphDataType type) {
		with(name);
		with(type);
	}

	public GraphMethod getMethod() {
		return this.method;
	}

	public GraphParameter with(GraphMethod value) {
		if (this.method != value) {
			GraphMethod oldValue = this.method;

			if (this.method != null) {
				this.method = null;
				oldValue.without(this);
			}

			this.method = value;

			if (value != null) {
				value.with(this);
			}
		}
		return this;
	}

	// Redirect
	@Override
	public GraphParameter with(String string) {
		super.with(string);
		return this;
	}

	@Override
	public GraphParameter with(GraphDataType value) {
		super.with(value);
		return this;
	}

	@Override
	public GraphParameter getNewList(boolean keyValue) {
		return new GraphParameter();
	}

	@Override
	public Object getValueItem(Object key) {
		if(PROPERTY_METHOD.equals(key)) {
			return method;
		}
		return null;
	}
}
