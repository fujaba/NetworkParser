/*
	Copyright (c) 2014 Stefan
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

package de.uniks.networkparser.test.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Person;

public class PersonSet extends SimpleSet<Person>
{
	protected Class<?> getTypeClass() {
		return Person.class;
	}
	public PersonSet with(Object... values) {
		super.with(values);
		return this;
	}

	public PersonSet without(Person value) {
		this.remove(value);
		return this;
	}

	public ArrayList<String> getName() {
		ArrayList<String> result = new ArrayList<String>();
		for (Person obj : this) {
			result.add(obj.getName());
		}
		return result;
	}

	public PersonSet hasName(String value) {
		PersonSet result = new PersonSet();
		for (Person obj : this) {
			if (value.equals(obj.getName())) {
				result.add(obj);
			}
		}
		return result;
	}

	public PersonSet withName(String value) {
		for (Person obj : this) {
			obj.setName(value);
		}
		return this;
	}

	public NumberList getBalance() {
		NumberList result = new NumberList();
		for (Person obj : this) {
			result.add(obj.getBalance());
		}
		return result;
	}

	public PersonSet hasBalance(double value) {
		PersonSet result = new PersonSet();
		for (Person obj : this) {
			if (value == obj.getBalance()) {
				result.add(obj);
			}
		}
		return result;
	}

	public PersonSet withBalance(double value) {
		for (Person obj : this) {
			obj.setBalance(value);
		}
		return this;
	}

	public GroupAccountSet getParent() {
		GroupAccountSet result = new GroupAccountSet();
		for (Person obj : this) {
			result.add(obj.getParent());
		}
		return result;
	}

	public PersonSet hasParent(Object value) {
		ArrayList<Object> neighbors = new ArrayList<Object>();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		PersonSet answer = new PersonSet();
		for (Person obj : this) {
			if (neighbors.contains(obj.getParent())) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public PersonSet withParent(GroupAccount value) {
		for (Person obj : this) {
			obj.withParent(value);
		}
		return this;
	}

	public ItemSet getItem() {
		ItemSet result = new ItemSet();
		for (Person obj : this) {
			result.addAll(obj.getItem());
		}
		return result;
	}

	public PersonSet hasItem(Object value) {
		ArrayList<Object> neighbors = new ArrayList<Object>();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		PersonSet answer = new PersonSet();
		for (Person obj : this) {
			if ( ! Collections.disjoint(neighbors, obj.getItem())) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public PersonSet withItem(Item value) {
		for (Person obj : this) {
			obj.withItem(value);
		}
		return this;
	}

	public PersonSet withoutItem(Item value) {
		for (Person obj : this) {
			obj.withoutItem(value);
		}
		return this;
	}
}
