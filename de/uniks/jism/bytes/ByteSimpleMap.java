package de.uniks.jism.bytes;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uniks.jism.AbstractIdMap;
import de.uniks.jism.CloneFilter;
import de.uniks.jism.IdMap;
import de.uniks.jism.event.BitEntity;
import de.uniks.jism.event.creator.BitEntityCreator;

public class ByteSimpleMap extends AbstractIdMap{

	@Override
	public Object cloneObject(Object reference, CloneFilter filter) {
		return null;
	}
	
//	public Object decode(ByteBuffer buffer){
//		
//	}
	
	public Object decode(ByteBuffer buffer, BitEntityCreator creator){
		HashMap<String, Object> values=new HashMap<String, Object>();
		BitEntity[] bitProperties = creator.getBitProperties();
		Object newInstance = creator.getSendableInstance(false);
		for(BitEntity entity : bitProperties){
			Object element = getEntity(buffer, entity, values);
			if(element != null){
				creator.setValue(newInstance, entity.getPropertyName(), element, IdMap.NEW);
			}
		}
		return newInstance;
	}
	
	public Object getEntity(ByteBuffer buffer, BitEntity entry, HashMap<String, Object> values){
		if(entry.isTyp(BitEntity.TYP_VALUE)){
			return entry.getPropertyName();
		}else if(entry.isTyp(BitEntity.TYP_REFERENCE)){
			String propertyName = entry.getPropertyName();
			if(values.containsKey(propertyName)){
				return values.get(propertyName);
			}
		}
		
		// Init the Values
		int len = Integer.valueOf(""+getEntity(buffer, entry.getStartBit(), values));;
		int posOfByte = len/8;
		int posOfBit = len%8;
		
		len = Integer.valueOf(""+getEntity(buffer, entry.getLen(), values));
		int noOfByte=len/8;
		if(len%8>0){
			noOfByte++;
		}
		
		int number = 0;
		int resultPos = 0;

		ByteBuffer result = ByteBuffer.allocate(noOfByte);
		byte theByte = buffer.get(posOfByte);
		while(len>0){
			int bitvalue = (theByte >> posOfBit)&1;
			number += bitvalue << resultPos;
			posOfBit++;
			resultPos++;
			if(posOfBit>7){
				posOfBit = 0;
				++posOfByte;
				if(posOfByte<buffer.limit()){
					theByte = buffer.get(posOfByte);
				}
			}
			if(resultPos ==8){
				result.put((byte)number);
				resultPos = 0;
			}
			len--;
		}
		if(resultPos>0){
			result.put((byte)number);
		}
		// Set the Typ
		Object element=null;

		result.flip();
		if(entry.getTyp().equals(BitEntity.BIT_BYTE)){
			byte[] array = result.array();
			if(array.length==1){
				element = new Byte(array[0]);
			}else{
				Byte[] item=new Byte[array.length];
				for(int i=0;i<array.length;i++){
					item[i] = array[i];
				}
				element = item;
			}
		}else if(entry.getTyp().equals(BitEntity.BIT_NUMBER)){
			if(result.limit()== Byte.SIZE/ByteEntity.BITOFBYTE){
				element = result.get();
			}else if(result.limit()== Short.SIZE/ByteEntity.BITOFBYTE){
				element = result.getShort();
			} else if(result.limit()== Integer.SIZE/ByteEntity.BITOFBYTE){
				element = result.getInt();
			} else if(result.limit()== Long.SIZE/ByteEntity.BITOFBYTE){
				element = result.getLong();
			} else if(result.limit()== Float.SIZE/ByteEntity.BITOFBYTE){
				element = result.getFloat();
			} else if(result.limit()== Double.SIZE/ByteEntity.BITOFBYTE){
				element = result.getDouble();
			}else{
				element = result.getInt();
			}
		}else if(entry.getTyp().equals(BitEntity.BIT_STRING)){
			result.flip();
			element = new String(result.array());
			
		}
		values.put(entry.getPropertyName(), element);
		return element;
	}
}
