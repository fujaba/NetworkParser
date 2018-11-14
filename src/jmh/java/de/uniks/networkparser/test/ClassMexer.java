package de.uniks.networkparser.test;

import java.util.Collection;

import com.javamex.classmexer.MemoryUtil;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.list.SpeedList;

public class ClassMexer {

	public static void main(String[] args) {
		ClassMexer classMexer = new ClassMexer();
		CollectionsFactory factory = classMexer.testMemory();
		System.out.println(factory.toString());
	}
	
	public CollectionsFactory testMemory() {
		CollectionsFactory factory = new CollectionsFactory();
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<factory.size();i++) {
			testMemory(sb, factory.getInstance(i));
		}
		FileBuffer.writeFile("build/classmexer.csv", sb.toString());
		return factory;
	}
	
	public void testMemory(StringBuilder sb, Collection<Integer> collection) {
		sb.append(collection.getClass().getSimpleName());
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		int i=1;
		while(i<=10) {
			collection.add(i);
			i++;
		}
		if(collection instanceof SpeedList<?>) {
			((SpeedList<?>) collection).pack();
		}
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		while(i<=100) {
			collection.add(i);
			i++;
		}
		if(collection instanceof SpeedList<?>) {
			((SpeedList<?>) collection).pack();
		}
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		while(i<=1000) {
			collection.add(i);
			i++;
		}
		if(collection instanceof SpeedList<?>) {
			((SpeedList<?>) collection).pack();
		}
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		while(i<=1000000) {
			collection.add(i);
			i++;
		}
		if(collection instanceof SpeedList<?>) {
			((SpeedList<?>) collection).pack();
		}
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));

		sb.append("\r\n");
	}
}
