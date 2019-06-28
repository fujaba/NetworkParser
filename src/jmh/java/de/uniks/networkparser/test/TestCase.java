package de.uniks.networkparser.test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public abstract class TestCase {
	protected static CollectionsFactory factory = new CollectionsFactory();
//	@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"})
//	@Param({VALUES})
//	@Param({"java.util.ArrayList","java.util.ArrayDeque","java.util.LinkedList","org.magicwerk.brownies.collections.GapList","org.magicwerk.brownies.collections.BigList","org.magicwerk.brownies.collections.primitive.IntObjGapList","de.uniks.networkparser.list.SimpleList","de.uniks.networkparser.list.SpeedList","org.eclipse.emf.common.util.BasicEList","org.eclipse.emf.ecore.util.EObjectResolvingEList","java.util.HashSet","java.util.LinkedHashSet","java.util.TreeSet","de.uniks.networkparser.list.SimpleSet"})

	protected Collection<?> collection;
	
	protected EInt[] array;

//	public abstract void test();

	public abstract void setup();

	@Test
    public void launchBenchmark() throws Exception {
          Options opt = new OptionsBuilder()
              // Specify which benchmarks to run. 
              // You can be more specific if you'd like to run only one benchmark per test.
              .include(this.getClass().getName() + "_A.*")
              // Set the following options as needed
              .mode (Mode.AverageTime)
              .timeUnit(TimeUnit.MICROSECONDS)
              .warmupTime(TimeValue.seconds(1))
              .warmupIterations(2)
              .measurementTime(TimeValue.seconds(1))
              .measurementIterations(2)
              .threads(8)
              .forks(1)
              .shouldFailOnError(false)
              .shouldDoGC(true)
              .output("output.txt")
              //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
              //.addProfiler(WinPerfAsmProfiler.class)
              .build();
          Runner runner = new Runner(opt);
          Field field = runner.getClass().getDeclaredField("list");
          field.setAccessible(true);
//          BenchmarkList list = (BenchmarkList) field.get(runner);
//          list.fromString("");
          runner.run();
      }
}
