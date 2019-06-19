package de.uniks.networkparser.logic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.beans.PropertyChangeListener;
import java.util.Collection;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.ConditionSet;

public class ChainCondition extends ListCondition {
	public ChainCondition enableHook() {
		this.chain = false;
		return this;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// CHAIN CANT CREATE
	}

	@Override
	public ChainCondition with(ObjectCondition... values) {
		super.with(values);
		return this;
	}

	@Override
	public ChainCondition with(PropertyChangeListener... values) {
		super.with(values);
		return this;
	}

	public ChainCondition with(Collection<ObjectCondition> values) {
		ConditionSet list;

		if (this.list instanceof ConditionSet) {
			list = (ConditionSet) this.list;
		} else {
			list = new ConditionSet();
			list.with(this.list);
			this.list = list;
		}
		list.withList(values);
		return this;
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new ChainCondition();
	}
}
