package de.uniks.networkparser.test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CollectionTest_Test extends TestCase {
	public int SIZE_SMALL=100;
	public int SIZE_BIG = 1000000;
	
	
//	@Param({"TreeSet", "ArrayList", "SimpleList","BasicEList", "SpeedList", "SimpleSet"})
//	@Param({"SimpleList","SpeedList", "SimpleSet"})
//	@Param({"SimpleSet","SpeedList","SimpleList"})
	@Param({"SpeedList"})
	protected String collectionName;
	
//	private int count;
	
	@Setup(Level.Iteration)
	public void setup() {
		collection = factory.getInstance(collectionName);
//		System.out.println(collectionName+": "+collection+":"+collection.elments+collection.size()+":"+collection.getClass().getName() + "@" + Integer.toHexString(collection.hashCode()));
//		factory.setNewValue(collection, array);
	}
	
	@Setup(Level.Trial)
	public void setupBefore() {
		array=factory.getArray(SIZE_BIG+1);
	}
	
//	@Benchmark
//	@Warmup(iterations = 5, time = 1)
//	@Measurement(iterations = 5, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.NANOSECONDS)
//	@Parameters()
//	public void AddSmall() {
//		if(collection instanceof IntObjGapList) {
//			IntObjGapList iObjCollection = (IntObjGapList)collection;
//			for(int c=0;c<SIZE_SMALL;c++) {
//				iObjCollection.add(c);
//			}
//			return;
//		}
//		@SuppressWarnings("unchecked")
//		Collection<Object> eList = (Collection<Object>)collection;
//		for(int i=0;i<SIZE_SMALL;i++) {
//			eList.add(array[i]);
//		}
//	}
	
	@SuppressWarnings("unchecked")
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.All)
//	@Parameters()
//	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void AddSmallContains() {
//		System.out.println(count++);
//		if(collection instanceof TreeSet<?>) {
//			TreeSet<Object> set = (TreeSet<Object>)collection;
//			for(int c=0;c<SIZE_SMALL;c++) {
//				if(set.contains(c)==false) {
//					set.add(c);
//				}
//			}
//			return;
//		}
//		if(collection instanceof IntObjGapList) {
//			IntObjGapList iObjCollection = (IntObjGapList)collection;
//			for(int c=0;c<SIZE_SMALL;c++) {
//				if(iObjCollection.contains(c)==false) {
//					iObjCollection.add(c);
//				}
//			}
//			return;
//		}
		Collection<Object> eList = (Collection<Object>)collection;
//		SpeedList<?> list = (SpeedList<?>) eList;
//		if(list.elements != null) {
//			System.out.println(list+":"+list.size()+list.elements+":"+list.elements.length);
//		}else {
//			System.out.println(list+":"+list.size()+list.elements);
//		}
//		System.out.println(eList+":"+eList.size());
		for(int i=0;i<SIZE_SMALL;i++) {
			if(eList.contains(i)==false) {
				eList.add(array[i]);
			}
		}
	}
	
//	@Test
//	public void test() {
////		@Param({"SimpleList","SpeedList", "SimpleSet"})
//		array=factory.getArray(SIZE_BIG+1);
//		collection = factory.getInstance("SpeedList");
//		AddSmallContains();
//		collection = factory.getInstance("SpeedList");
//		AddSmallContains();
//		System.out.println(collection);
//	}

//	@Benchmark
//	@Warmup(iterations = 5, time = 1)
//	@Measurement(iterations = 10, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	public void ContainsSmall() {
//		if(collection instanceof IntObjGapList) {
//			for(int i=0;i<array.length;i++) {
//				collection.contains(i);
//			}
//		} else {
//			for(int i=0;i<array.length;i++) {
//				collection.contains(array[i]);
//			}
//		}
//	}

//	@Benchmark
//	@Warmup(iterations = 5, time = 1)
//	@Measurement(iterations = 10, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	public void Iterator() {
//		for(Iterator<?> iterator = collection.iterator();iterator.hasNext();) {
//			iterator.next();
//		}
//	}
//	@Benchmark
//	@Warmup(iterations = 2, time = 1)
//	@Measurement(iterations = 5, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	public void ContainsBig() {
//		if(collection instanceof IntObjGapList) {
//			for(int i=0;i<array.length;i++) {
//				collection.contains(i);
//			}
//		} else {
//			for(int i=0;i<array.length;i++) {
//				collection.contains(array[i]);
//			}
//		}
//	}
//	
//	@Benchmark
//	@Warmup(iterations = 2, time = 1)
//	@Measurement(iterations = 5, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	public void RemoveBig() {
//		if(collection instanceof IntObjGapList) {
//			for(int i=0;i<array.length;i++) {
//				collection.remove(i);
//			}
//		} else {
//			for(int i=0;i<array.length;i++) {
//				collection.remove(array[i]);
//			}
//		}
//	}


//	@SuppressWarnings("unchecked")
//	@Benchmark
//	@Warmup(iterations = 5, time = 1)
//	@Measurement(iterations = 5, time = 1)
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.MILLISECONDS)
//	public void AddBigContains() {
//		if(collection instanceof TreeSet<?>) {
//			TreeSet<Object> set = (TreeSet<Object>)collection;
//			for(int c=0;c<SIZE_BIG;c++) {
//				if(set.contains(c)==false) {
//					set.add(c);
//				}
//			}
//			return;
//		}
//		if(collection instanceof IntObjGapList) {
//			IntObjGapList iObjCollection = (IntObjGapList)collection;
//			for(int c=0;c<SIZE_BIG;c++) {
//				if(iObjCollection.contains(c)==false) {
//					iObjCollection.add(c);
//				}
//			}
//			return;
//		}
//		Collection<Object> eList = (Collection<Object>)collection;
//		for(int i=0;i<SIZE_BIG;i++) {
//			if(eList.contains(i)==false) {
//				eList.add(array[i]);
//			}
//		}
//	}
}
