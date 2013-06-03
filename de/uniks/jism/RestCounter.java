package de.uniks.jism;

public class RestCounter extends SimpleIdCounter{
	
	public RestCounter(String path){
		if(path.endsWith("/")){
			this.prefixId = path;
		}else{
			this.prefixId = path+"/";
		}
	}
	
	@Override
	public String getId(Object obj) {
		String key;

		// new object generate key and add to tables
		// <session id>.<first char><running number>
		if (obj == null) {
			Exception e = new Exception("NullPointer: " + obj);
			e.printStackTrace();
			return "";
		}
		String className = obj.getClass().getName();
		key = this.prefixId + className.toLowerCase() +"/"+ this.number;
		this.number++;
		return key;
	}
	
	@Override
	public boolean isSimpleObject() {
		return true;
	}
}
