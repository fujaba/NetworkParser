/*
   Copyright (c) 2014 zuendorf 
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package de.uniks.networkparser.test.ferrymansproblem.util;

import java.util.Collection;
import java.util.Collections;

import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.test.ferrymansproblem.Bank;
import de.uniks.networkparser.test.ferrymansproblem.Boat;
import de.uniks.networkparser.test.ferrymansproblem.River;
import de.uniks.networkparser.test.ferrymansproblem.util.BankSet;
import de.uniks.networkparser.test.ferrymansproblem.util.BoatSet;

/**
 * 
 * @see <a href=
 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
 * @see <a href=
 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
 */
public class RiverSet extends SimpleSet<River> {

	@SuppressWarnings("unchecked")
	public RiverSet with(Object value) {
		if (value instanceof java.util.Collection) {
			this.addAll((Collection<River>) value);
		} else if (value != null) {
			this.add((River) value);
		}

		return this;
	}

	public RiverSet without(River value) {
		this.remove(value);
		return this;
	}

	public BoatSet getBoat() {
		BoatSet result = new BoatSet();

		for (River obj : this) {
			result.add(obj.getBoat());
		}

		return result;
	}

	public RiverSet hasBoat(Object value) {
		ObjectSet neighbors = new ObjectSet();

		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}

		RiverSet answer = new RiverSet();

		for (River obj : this) {
			if (neighbors.contains(obj.getBoat())) {
				answer.add(obj);
			}
		}

		return answer;
	}

	public RiverSet withBoat(Boat value) {
		for (River obj : this) {
			obj.withBoat(value);
		}

		return this;
	}

	/**
	 * 
	 * @see <a href=
	 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
	 * @see <a href=
	 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
	 */
	public BankSet getBanks() {
		BankSet result = new BankSet();

		for (River obj : this) {
			result.addAll(obj.getBanks());
		}

		return result;
	}

	public RiverSet hasBanks(Object value) {
		ObjectSet neighbors = new ObjectSet();

		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}

		RiverSet answer = new RiverSet();

		for (River obj : this) {
			if (!Collections.disjoint(neighbors, obj.getBanks())) {
				answer.add(obj);
			}
		}

		return answer;
	}

	public RiverSet withBanks(Bank value) {
		for (River obj : this) {
			obj.withBanks(value);
		}

		return this;
	}

	public RiverSet withoutBanks(Bank value) {
		for (River obj : this) {
			obj.withoutBanks(value);
		}

		return this;
	}

	public static final RiverSet EMPTY_SET = new RiverSet().withFlag(RiverSet.READONLY);

	public String getEntryType() {
		return "org.sdmlib.test.examples.reachabilitygraphs.ferrymansproblem.River";
	}

	/**
	 * 
	 * @see <a href=
	 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
	 */
	public RiverSet() {
		// empty
	}

	/**
	 * 
	 * @see <a href=
	 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
	 */
	public RiverSet(River... objects) {
		for (River obj : objects) {
			this.add(obj);
		}
	}

	/**
	 * 
	 * @see <a href=
	 *      '../../../../../../../../../../src/test/java/org/sdmlib/test/examples/reachabilitygraphs/ReachabilityGraphFerrymansProblemExample.java'>ReachabilityGraphFerrymansProblemExample.java</a>
	 */
	public RiverSet(Collection<River> objects) {
		this.addAll(objects);
	}

	@Override
	public RiverSet getNewList(boolean keyValue) {
		return new RiverSet();
	}
}
