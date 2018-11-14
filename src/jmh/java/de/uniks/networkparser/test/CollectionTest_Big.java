package de.uniks.networkparser.test;

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
@OutputTimeUnit(TimeUnit.SECONDS)
public class CollectionTest_Big extends TestCase {
	public int SIZE_BIG=1000000;
	
	@Param({"ArrayList","ArrayDeque","LinkedList","GapList","BigList","IntObjGapList","SimpleList","SpeedList","BasicEList","EObjectResolvingInteger","HashSet","LinkedHashSet","TreeSet","SimpleSet"})
	protected String collectionName;
	
	@Setup(Level.Iteration)
	public void setup() {
		collection = factory.getInstance(collectionName, SIZE_BIG);
	}

	/*
	 * This is what you do with JMH.
	 */
	@Benchmark
	@Warmup(iterations = 5, time = 1)
	@Measurement(iterations = 5, time = 1)
	@BenchmarkMode(Mode.AverageTime)
	public void ContainsBig() {
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
	public void RemoveBig() {
		int itemTen = collection.size() / 10;
		int count =0 ;
		for(int i=0;i<10;i++) {
			collection.remove(count);
			count +=itemTen;
		}
	}
}
