package de.uniks.networkparser.test.model.util;

import java.util.ArrayList;
import de.uniks.networkparser.bytes.BitEntity;
import de.uniks.networkparser.bytes.BitEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.BitDate;

public class BitDateCreator implements SendableEntityCreator,BitEntityCreator {
	private ArrayList<BitEntity> entities= new ArrayList<BitEntity>();

	public BitDateCreator(){
	}
	public void addField(BitEntity bitEntity){
		this.entities.add(bitEntity);
	}
	public void addField(String field, int start, int len) {
		addField(new BitEntity().with(field, BitEntity.BIT_NUMBER).withStartLen(start, len));
	}
	public void addField(String field, int start, int len, int orientation) {
		addField(new BitEntity().with(field, BitEntity.BIT_NUMBER).withStartLen(start, len).withOrientation(orientation));
	}
//
//		createrClass.addBitEntity("number",BitEntity.BIT_NUMBER,  "8", BitEntity.TYP_VALUE, "len", BitEntity.TYP_REFERENCE);
//		return new String[] { BitDate.PROPERTY_DAY,
//				BitDate.PROPERTY_MONTH, BitDate.PROPERTY_YEAR, BitDate.PROPERTY_HOUR, BitDate.PROPERTY_MINUTE };



	@Override
	public String[] getProperties() {
		ArrayList<String> list= new ArrayList<String>();
		for (BitEntity entity : entities){
			list.add(entity.getPropertyName());
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new BitDate();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((BitDate) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((BitDate) entity).set(attribute, value);
	}

	@Override
	public BitEntity[] getBitProperties() {
		return entities.toArray(new BitEntity[entities.size()]);
	}
}