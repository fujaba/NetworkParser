package de.uniks.jism.bytes;

import java.nio.ByteBuffer;

import de.uniks.jism.EntityList;
import de.uniks.jism.interfaces.BaseEntity;
import de.uniks.jism.interfaces.ByteItem;

public class ByteList extends EntityList implements ByteItem {
	/** The children of the ByteEntity. */
	private byte typ=0;
	@Override
	public EntityList getNewArray() {
		return new ByteList();
	}

	@Override
	public BaseEntity getNewObject() {
		return new ByteEntity();
	}

	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}

	@Override
	public String toString() {
		ByteBuffer buffer=getBytes(false);
		return ByteUtil.convertString(buffer);
	}
	
	public String toHexString(){
		return ByteUtil.convertHexString(getBytes(false));
	}

	public ByteBuffer getBytes(boolean isDynamic) {
		int len=calcLength(isDynamic);
		ByteBuffer buffer = ByteUtil.getBuffer(len, getTyp());
		for(Object value : values){
			ByteBuffer child=null;
			if(value instanceof ByteItem){
				child=((ByteItem)value).getBytes(isDynamic);
			}
			if(child!=null){
				byte[] array=new byte[child.limit()];
				child.get(array);
				buffer.put(array);
			}
		}
		buffer.flip();
		return buffer;
	}

	public int calcLength(boolean isDynamic) {
		int length=0;
		Object[] valueList=this.values.toArray(new Object[this.values.size()]);
		boolean notLast=true;
		for(int i=valueList.length-1;i>=0;i--){
			if(notLast){
				int len=0;
				if(valueList[i] instanceof ByteList){
					len=((ByteList)valueList[i]).calcLength(isDynamic);
					if(len<1){
						this.values.remove(valueList[i]);
					}else{
						notLast=false;
					}
				}else if(valueList[i] instanceof ByteEntity){
					ByteEntity entity=(ByteEntity)valueList[i];
					len=entity.calcLength(isDynamic);
					if(len==1){
						this.values.remove(valueList[i]);
					}else{
						// SET the LastEntity
						notLast=false;
						if(entity.setLenCheck(false)){
							len=entity.calcLength(isDynamic);
						}
						length+=len;
					}
				}
			}else{
				if(valueList[i] instanceof ByteItem){
					length+=((ByteItem)valueList[i]).calcLength(isDynamic);
				}
			}
		}
		return length;
	}

	public Byte getTyp() {
		return typ;
	}

	public void setTyp(Byte typ) {
		this.typ = typ;
	}
	
}
