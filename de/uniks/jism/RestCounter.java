package de.uniks.jism;

public class RestCounter extends SimpleIdCounter{
	
	private String path;

	public RestCounter(String path){
		this.path = path;
	}
	
	@Override
	public String getId(Object obj) {
		return path+super.getId(obj);
	}

	@Override
	public boolean isSimpleObject() {
		return true;
	}
}
