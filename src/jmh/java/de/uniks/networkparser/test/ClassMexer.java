package de.uniks.networkparser.test;

import java.util.Collection;

import org.magicwerk.brownies.collections.primitive.IntObjGapList;

import com.javamex.classmexer.MemoryUtil;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.list.SpeedList;

public class ClassMexer {
	// -javaagent:classmexer.jar
	private static int[] SIZES=new int[] {10,100,1000,10000,100000,1000000};
	public static void main(String[] args) {
		ClassMexer classMexer = new ClassMexer();
		CollectionsFactory factory = classMexer.testMemory();
		System.out.println(factory.toString());
	}

	private EInt[] array;
	
	public CollectionsFactory testMemory() {
		CollectionsFactory factory = new CollectionsFactory();
		StringBuilder sb = new StringBuilder();
		
		this.array = factory.getArray(SIZES[SIZES.length-1]+1);
		
		for(int i=0;i<factory.size();i++) {
			testMemory(sb, factory.getInstance(i));
		}
		FileBuffer.writeFile("build/classmexer.csv", sb.toString());
		return factory;
	}
	
	@SuppressWarnings("unchecked")
	public void testMemory(StringBuilder sb, Collection<?> collection) {
		sb.append(collection.getClass().getSimpleName());
		sb.append(';');
		sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		
		if(collection instanceof IntObjGapList) {
			testing(sb, (IntObjGapList)collection);
		} else if(collection instanceof EObjectResolvingInteger) {
			testing(sb, (EObjectResolvingInteger)collection);
		} else {
			testing(sb, (Collection<Object>)collection);
		}
	}

	public void testing(StringBuilder sb, IntObjGapList collection) {
		int i=1;
		for(int c=0;c<SIZES.length;c++) {
			while(i<=SIZES[c]) {
				collection.add(i);
				i++;
			}
			sb.append(';');
			sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		}
		sb.append("\r\n");
	}
	
	public void testing(StringBuilder sb, EObjectResolvingInteger collection) {
		int i=1;
		for(int c=0;c<SIZES.length;c++) {
			while(i<=SIZES[c]) {
				collection.add(this.array[i]);
				i++;
			}
			sb.append(';');
			sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		}
		sb.append("\r\n");
	}
	
	public void testing(StringBuilder sb, Collection<Object> collection) {
		int i=1;
		for(int c=0;c<SIZES.length;c++) {
			while(i<=SIZES[c]) {
				collection.add(i);
				i++;
			}
			if(collection instanceof SpeedList<?>) {
				((SpeedList<?>) collection).pack();
			}
			sb.append(';');
			sb.append(MemoryUtil.deepMemoryUsageOf(collection));
		}
		sb.append("\r\n");
	}
}
