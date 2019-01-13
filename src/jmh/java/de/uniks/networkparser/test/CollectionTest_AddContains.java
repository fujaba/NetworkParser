package de.uniks.networkparser.test;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.magicwerk.brownies.collections.primitive.IntObjGapList;
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
public class CollectionTest_Small extends TestCase {
	public int SIZE_SMALL=100;
	
	@Param({"ArrayList","ArrayDeque","LinkedList","GapList","BigList","IntObjGapList","SimpleList","SpeedList","BasicEList","EObjectResolvingInteger","HashSet","LinkedHashSet","TreeSet","SimpleSet"})
	protected String collectionName;
	
	@Setup(Level.Iteration)
	public void setup() {
		collection = factory.getInstance(collectionName);
	}
	
	@Setup(Level.Trial)
	public void setupBefore() {
		array=factory.getArray(SIZE_SMALL);
	}


	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@Parameters()
	public void AddSmall() {
		if(collection instanceof IntObjGapList) {
			IntObjGapList iObjCollection = (IntObjGapList)collection;
			for(int c=0;c<SIZE_SMALL;c++) {
				if(iObjCollection.contains(i)==false) {
					iObjCollection.add(c);
				}
			}
			return;
		}
		@SuppressWarnings("unchecked")
		Collection<Object> eList = (Collection<Object>)collection;
		for(int i=0;i<SIZE_SMALL;i++) {
			if(eList.contains(i)==false) {
				eList.add(array[i]);
			}
		}
	}
	
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Parameters()
	public void AddBig() {
		if(collection instanceof IntObjGapList) {
			IntObjGapList iObjCollection = (IntObjGapList)collection;
			for(int c=0;c<SIZE_BIG;c++) {
				if(iObjCollection.contains(i)==false) {
					iObjCollection.add(c);
				}
			}
			return;
		}
		@SuppressWarnings("unchecked")
		Collection<Object> eList = (Collection<Object>)collection;
		for(int i=0;i<SIZE_BIG;i++) {
			if(eList.contains(i)==false) {
				eList.add(array[i]);
			}
		}
	}
}
