package de.uniks.jism.yuml;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import de.uniks.jism.Filter;

public class YUmlIdMapFilter extends Filter{
	/** The show line. */
	private boolean isShowLine;
	
	private boolean isShowCardinality;
	
	private int typ;
	
	/** The value yuml. */
//	private HashMap<String, String> valueYUML = new HashMap<String, String>();

	//	/** The link property. */
//	private HashMap<String, String> linkProperty = new HashMap<String, String>();
	
//	/** The link cardinality. */
//	private HashMap<String, String> linkCardinality = new HashMap<String, String>();

	
//	public String getValueYUML(String key) {
//		return valueYUML.get(key);
//	}
//	
//	public boolean addValueYUML(String key, String value) {
//		valueYUML.put(key, value);
//		return true;
//	}
//	
//	public String removeValueYUML(String key) {
//		return valueYUML.remove(key);
//	}
//	
//	public boolean containsKeyValueYUML(String key){
//		return valueYUML.containsKey(key);
//	}


//	@Override
//	public YUmlIdMapFilter withStandard(Filter referenceFilter) {
//		super.withStandard(referenceFilter);
//		this.valueYUML.clear();
//		return this;
//	}
//	
//	public Set<String> getLinkPropertys() {
//		return linkProperty.keySet();
//	}
//
//	public String getLinkProperty(String key) {
//		return linkProperty.get(key);
//	}
//
//	public boolean addLinkProperty(String key, String value) {
//		linkProperty.put(key, value);
//		return true;
//	}
	
//
//	public String getLinkCardinality(String key) {
//		return linkCardinality.get(key);
//	}
//	
//	public boolean addLinkCardinality(String key, String value) {
//		linkCardinality.put(key, value);
//		return true;
//	}
//	

	/**
	 * Checks if is show line.
	 *
	 * @return true, if is show line for objects
	 */
	public boolean isShowLine() {
		return this.isShowLine;
	}

	/**
	 * Sets the show line.
	 *
	 * @param value
	 *			the new show line
	 * @return 
	 */
	public YUmlIdMapFilter withShowLine(boolean value) {
		this.isShowLine = value;
		return this;
	}

	public boolean isShowCardinality() {
		return isShowCardinality;
	}

	public YUmlIdMapFilter withShowCardinality(boolean value) {
		this.isShowCardinality = value;
		return this;
	}
	
	@Override
	public YUmlIdMapFilter clone(Filter newInstance) {
		return (YUmlIdMapFilter) super.clone(newInstance);
	}

	public int getTyp() {
		return typ;
	}

	public YUmlIdMapFilter withTyp(int typ) {
		this.typ = typ;
		return this;
	}
}
