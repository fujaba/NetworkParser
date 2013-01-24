package de.uniks.jism.bytes;

import java.nio.ByteBuffer;

import de.uniks.jism.AbstractIdMap;
import de.uniks.jism.CloneFilter;
import de.uniks.jism.IdMap;
import de.uniks.jism.interfaces.BitEntity;
import de.uniks.jism.interfaces.BitEntityCreator;
import de.uniks.jism.test.model.creator.NumberFormatCreator;

public class ByteSimpleMap extends AbstractIdMap{

	@Override
	public Object cloneObject(Object reference, CloneFilter filter) {
		return null;
	}
	
	public Object decode(ByteBuffer buffer, BitEntityCreator creator){
		BitEntity[] bitProperties = creator.getBitProperties();
		Object newInstance = creator.getSendableInstance(false);
		for(BitEntity entity : bitProperties){
			Object element = get(buffer, entity);
			if(element != null){
				creator.setValue(newInstance, entity.getPropertyName(), element, IdMap.NEW);
			}
		}
		return newInstance;
	}
	
	public Object get(ByteBuffer buffer, BitEntity entry)
	{
		//FIXME New Parser
		Object result = null;
		boolean finished=false;
		do
		{
//			finished = current.equals(lsb);
//			result = result << 1;
//			ensureSize(current.bytePos+1);
//			byte theByte = array[current.bytePos];
////			final int i = BYTE_SIZE-currentPosition.bitPos-1;
//			int bitvalue = (theByte >> current.bitPos)&1;
//			result += bitvalue;
//			
//			current.minusIntern(step);
		}
		while (!finished);
		return result;
	}

}
