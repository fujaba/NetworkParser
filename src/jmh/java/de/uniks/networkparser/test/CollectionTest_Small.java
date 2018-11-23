package de.uniks.networkparser.test;

import java.util.Iterator;
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
public class CollectionTest_Small extends TestCase {
	public int SIZE_SMALL=100;
	
	@Param({"ArrayList","ArrayDeque","LinkedList","GapList","BigList","IntObjGapList","SimpleList","SpeedList","BasicEList","EObjectResolvingInteger","HashSet","LinkedHashSet","TreeSet","SimpleSet"})
	protected String collectionName;
	
	@Setup(Level.Iteration)
	public void setup() {
		collection = factory.getInstance(collectionName, SIZE_SMALL);
	}

	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void ContainsSmall() {
		int itemTen = collection.size() / 10;
		int count =0 ;
		for(int i=0;i<10;i++) {
			collection.contains(count);
			count +=itemTen;
		}
	}
	
	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void RemoveSmall() {
		int itemTen = collection.size() / 10;
		int count =0 ;
		for(int i=0;i<10;i++) {
			collection.remove(count);
			count +=itemTen;
		}
	}
	
	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void Iterator() {
		for(Iterator<Integer> iterator = collection.iterator();iterator.hasNext();) {
			iterator.next();
		}
	}
}
