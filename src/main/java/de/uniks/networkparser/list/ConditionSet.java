package de.uniks.networkparser.list;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class ConditionSet extends SimpleSet<ObjectCondition>{
	@Override
	public boolean add(ObjectCondition newValue) {
		if(newValue instanceof ChainCondition) {
			ChainCondition cc= (ChainCondition) newValue;
			return super.addAll(cc.getList());
		}
		return super.add(newValue);
	}

	public CharacterBuffer getAllValue(LocalisationInterface variables) {
		CharacterBuffer buffer=new CharacterBuffer();
		for(ObjectCondition item : this) {
			if(item instanceof VariableCondition) {
				VariableCondition vc = (VariableCondition) item;
				Object result = vc.getValue(variables);
				if(result != null) {
					buffer.with(result.toString());
				}
			} else {
				buffer.with(item.toString());
			}
		}
		return buffer;
	}
}
