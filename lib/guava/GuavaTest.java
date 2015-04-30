package guavatests;


import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.TestsForSetsInJavaUtil;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;

import de.uniks.networkparser.list.SimpleSet;


public class GuavaTest {
	protected Collection<Method> suppressForHashSet() {
	    return Collections.emptySet();
	  }
	public Test testsForHashSet() {
	    return SetTestSuiteBuilder
	        .using(new TestStringSetGenerator() {
	            @SuppressWarnings("unchecked")
				@Override public Set<String> create(String[] elements) {
	              return (Set<String>) new SimpleSet<String>().withList(MinimalCollection.of(elements));
	            }
	          })
	        .named("HashSet")
	        .withFeatures(
	            SetFeature.GENERAL_PURPOSE,
	            CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
	            CollectionSize.ANY)
	        .suppressing(suppressForHashSet())
	        .createTestSuite();
//        CollectionFeature.ALLOWS_NULL_VALUES,
//	    CollectionFeature.SERIALIZABLE,
	  }
	
	public static Test suite() {
		return new GuavaTest().allTests();
//	    return new TestsForSetsInJavaUtil().allTests();
	  }

	  public Test allTests() {
	    TestSuite suite = new TestSuite("java.util Sets");
//	    suite.addTest(testsForEmptySet());
//	    suite.addTest(testsForSingletonSet());
	    suite.addTest(testsForHashSet());
//	    suite.addTest(testsForLinkedHashSet());
//	    suite.addTest(testsForEnumSet());
//	    suite.addTest(testsForTreeSetNatural());
//	    suite.addTest(testsForTreCeSetWithComparator());
//	    suite.addTest(testsForCopyOnWriteArraySet());
//	    suite.addTest(testsForUnmodifiableSet());
//	    suite.addTest(testsForCheckedSet());
//	    suite.addTest(testsForAbstractSet());
//	    suite.addTest(testsForBadlyCollidingHashSet());
//	    suite.addTest(testsForConcurrentSkipListSetNatural());
//	    suite.addTest(testsForConcurrentSkipListSetWithComparator());

	    return suite;
	  }
}
