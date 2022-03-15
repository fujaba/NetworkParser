package de.uniks.networkparser.test.list;


import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.test.model.Wallet;
import de.uniks.networkparser.test.model.util.WalletCreator;

public class TestListWithKey {

	@Test
	public void testList()
	{
		SortedSet<Wallet> list = new SortedSet<>(Wallet.PROPERTY_SUM, new WalletCreator());
		int[] add= new int[] {2,4,8,1};
		
		for(int i=0;i<10;i++) {
		//for(int i=0;i<5_000_000;i++) {
			list.add(new Wallet().withSum(i+add[i%4]));
		}
		
		double sum = 0;
		for(Wallet value : list) {
			if(value.getSum()<sum) {
				fail();
			}
			sum = value.getSum();
		}
	}
}
