package de.uniks.jism.yuml;

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
	
	public Cardinality reset(){
		this.showed=false;
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
