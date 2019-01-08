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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CollectionTest_Big extends TestCase {
	public int SIZE_BIG=1000000;
	
	@Param({"ArrayList","ArrayDeque","LinkedList","GapList","BigList","IntObjGapList","SimpleList","SpeedList","BasicEList","EObjectResolvingInteger","HashSet","LinkedHashSet","TreeSet","SimpleSet"})
	protected String collectionName;
	
	@Setup(Level.Iteration)
	public void setup() {
		collection = factory.getInstance(collectionName);
		factory.setNewValue(collection, array);
	}
	
	@Setup(Level.Trial)
	public void setupBefore() {
		array=factory.getArray(SIZE_BIG);
	}

	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 1, time = 1)
	@Measurement(iterations = 1, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void ContainsBig() {
		if(collection instanceof IntObjGapList) {
			for(int i=0;i<array.length;i++) {
				collection.contains(i);
			}
		} else {
			for(int i=0;i<array.length;i++) {
				collection.contains(array[i]);
			}
		}
	}
	
	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 1, time = 1)
	@Measurement(iterations = 1, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void RemoveBig() {
		if(collection instanceof IntObjGapList) {
			for(int i=0;i<array.length;i++) {
				collection.remove(i);
			}
		} else {
			for(int i=0;i<array.length;i++) {
				collection.remove(array[i]);
			}
		}
	}
	

	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 1, time = 1)
	@Measurement(iterations = 1, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void Iterator() {
		for(Iterator<?> iterator = collection.iterator();iterator.hasNext();) {
			iterator.next();
		}
	}
}
