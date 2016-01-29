package de.uniks.networkparser.test.model.util;

import java.util.ArrayList;
import de.uniks.networkparser.bytes.BitEntity;
import de.uniks.networkparser.bytes.BitValue;
import de.uniks.networkparser.bytes.util.BitEntityCreator;
import de.uniks.networkparser.test.model.NumberFormat;

public class NumberFormatCreator implements BitEntityCreator{
	private ArrayList<BitEntity> entities= new ArrayList<BitEntity>();

	public NumberFormatCreator(String propertyName, int start, int len){
		entities.add(new BitEntity().with(propertyName, BitEntity.BIT_BYTE).withStartLen(start, len));
	}

//	public NumberFormatCreator(String propertyName, String start, String startTyp, String len, String lenTyp){
//		addBitEntity(propertyName, start, startTyp, len, lenTyp);
//	}
//
//	public void addBitEntity(String propertyName, String start, String startTyp, String len, String lenTyp){
//		entities.add(new BitEntity(propertyName, BitEntity.BIT_BYTE, start, startTyp, len, lenTyp));
//	}
	public void addBitEntity(String propertyName, byte bitNumber, String start, byte bitString, String len, byte bitReference){
		BitEntity bitEntity = new BitEntity().with(propertyName, bitNumber);
		bitEntity.add(new BitValue(bitString, start, bitReference, len));
		entities.add(bitEntity);
	}
//		entities.add(new BitEntity(propertyName, dataTyp, start, startTyp, len, lenTyp));
//	}


	public NumberFormatCreator(){
		entities.add(new BitEntity().with("number", BitEntity.BIT_BYTE).withStartLen(0, 8));
	}

	@Override
	public String[] getProperties() {
		ArrayList<String> list= new ArrayList<String>();
		for (BitEntity entity : entities){
			list.add(entity.getPropertyName());
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NumberFormat();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((NumberFormat)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((NumberFormat)entity).set(attribute, value);
	}

	@Override
	public BitEntity[] getBitProperties() {
		return entities.toArray(new BitEntity[entities.size()]);
	}
}
