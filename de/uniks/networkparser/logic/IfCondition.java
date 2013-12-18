package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import de.uniks.networkparser.IdMap;

public class IfCondition implements Condition{
	private Condition expression;
	private Condition trueCondition;
	private Condition falseCondition;

	public IfCondition(Condition expression){
		this.expression = expression;
	}
	
	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		if(expression.matches(map, entity, property, value, isMany, deep)){
			if(trueCondition!=null){
				return trueCondition.matches(map, entity, property, value, isMany, deep);
			}
		}else{
			if(falseCondition!=null){
				return falseCondition.matches(map, entity, property, value, isMany, deep);
			}
		}
		return false;
	}
	
	public IfCondition withTrueCondition(Condition value){
		this.trueCondition = value;
		return this;
	}
	
	public IfCondition withFalseCondition(Condition value){
		this.falseCondition = value;
		return this;
	}
}
