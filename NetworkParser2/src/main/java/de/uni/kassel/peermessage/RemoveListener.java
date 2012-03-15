package de.uni.kassel.peermessage;

public class RemoveListener {
	private IdMap<?> map;
	private Object entity;

	public RemoveListener(IdMap<?> map, Object entity){
		this.map=map;
		this.entity=entity;
	}
	public boolean exeucte(){
		return map.remove(entity);
	}
}
