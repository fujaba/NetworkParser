package de.uniks.networkparser.yuml;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class Cardinality {
	private String sourceID;
	private String sourceClazz;
	private String targetID;
	private String targetClazz;
	private String cardinality;
	public boolean showed;

	public Cardinality withSource(YUMLEntity element) {
		this.sourceID = element.getId();
		this.sourceClazz = element.getClassName();
		return this;
	}

	public Cardinality withTarget(YUMLEntity element) {
		this.targetID = element.getId();
		this.targetClazz = element.getClassName();
		return this;
	}

	public String getSourceID() {
		return sourceID;
	}

	public Cardinality withSourceID(String sourceID) {
		this.sourceID = sourceID;
		return this;
	}

	public String getSourceClazz() {
		return sourceClazz;
	}

	public Cardinality withSourceClazz(String sourceClazz) {
		this.sourceClazz = sourceClazz;
		return this;
	}

	public String getTargetID() {
		return targetID;
	}

	public Cardinality withTargetID(String targetID) {
		this.targetID = targetID;
		return this;
	}

	public String getTargetClazz() {
		return targetClazz;
	}

	public Cardinality setTargetClazz(String targetClazz) {
		this.targetClazz = targetClazz;
		return this;
	}

	public String getCardinality() {
		return cardinality;
	}

	public Cardinality withCardinality(String cardinality) {
		this.cardinality = cardinality;
		return this;
	}

	public Cardinality reset() {
		this.showed = false;
		return this;
	}

	public boolean isShowed() {
		return showed;
	}

	public Cardinality withShowed(boolean value) {
		this.showed = value;
		return this;
	}
}
