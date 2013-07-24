package de.uniks.jism.calculator;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.uniks.jism.StringTokener;

public class RegCalculator {
	/** List of Operators */
    private HashMap<String, Operator> operators = new HashMap<String, Operator>();
 
    /** List of Constants */
    private Map<String, Double> constants = new HashMap<String, Double>();
    
    public RegCalculator withStandard(){
    	withOperator(new Addition());
    	withOperator(new Subtract());
    	withOperator(new Multiply());
    	withOperator(new Division());
    	withOperator(new Mod());
    	return this;
    }
    public RegCalculator withOperator(Operator value){
    	this.operators.put(value.getTag(), value);
    	return this;
    }
    
    public RegCalculator withConstants(String tag, double value){
    	constants.put(tag, value);
    	return this;
    }
    
    public Double calculate(String formular){
    	StringTokener tokener = new StringTokener();
    	tokener.withText(formular);

    	ArrayList<String> parts = new ArrayList<String>();
    	Character current =null;
    	if(tokener.getCurrentChar()=='('&&tokener.charAt(tokener.getLength()-1)==')'){
    		tokener.setIndex(1);
    		tokener.setLength(tokener.getLength()-1);
    	}
    	while(!tokener.isEnd()){
    		if(current==null){
    			current = tokener.next();
    		}
			if( Character.isWhitespace( current )){
				current=null;
				continue;
			}
			String value="";
			if( current == '('){
				int count=1;
				value = "(";
				while(!tokener.isEnd()){
					current = tokener.next();
					if(current=='('){
						count++;
					}
					if(current==')'){
						count--;
					}
					value+=current;
					if(count==0){
						break;
					}
				}
				if(count==0){
					parts.add( value );
				}
                current=null;
                continue;
            }

			
			if( Character.isDigit( current ) || current == '.' ){
				while(Character.isDigit( current ) || current == '.'){
					value+= current;
					current = tokener.next();
				}
				parts.add( value );
				continue;
			}
   			value+= current;
			while(!tokener.isEnd() ){
    			if(constants.containsKey(value)){
    				// Its constants
    				parts.add( ""+constants.get(value) );
    				value="";
    				break;
    			}else if(operators.containsKey(value)){
    				parts.add( value );
    				value="";
    				break;
    			}
    			current = tokener.next();
    			value += current;
    		}
			if(value.length()>0){
				if(constants.containsKey(value)){
    				// Its constants
    				parts.add( ""+constants.get(value) );
    			}else if(operators.containsKey(value)){
    				parts.add( value );
    			}
			}
			current=null;
    	}
    	
    	parse(parts);
    	if(parts.size()<1){
    		return 0.0;
    	}
    	return Double.valueOf(parts.get(0));
    }
    
    public void parse(ArrayList<String> parts){
    	// Parsing (
    	for(int i=0;i<parts.size();i++){
    		if(parts.get(i).startsWith("(")){
    			parts.set(i, ""+calculate(parts.get(i)));
    		}
    	}
    	parse(parts, 2);
    	parse(parts, 1);
    }
    
    public void parse(ArrayList<String> parts, int prio){
    	for(int i=0;i<parts.size();i++){
    		Operator operator = operators.get(parts.get(i));
    		if(operator!=null&&operator.getPriority()==prio){
    			if(i==0){
    				if(operator.getTag()=="-"){
    					parts.set(i, ""+(Double.valueOf(parts.get(i+1))*-1));
    					parts.remove(i+1);
    				}else{
    					parts.remove(i);
    				}
    				i=-1;
    				continue;
    			}
    			parts.set(i-1, ""+operator.calculate(Double.valueOf(parts.get(i-1)), Double.valueOf(parts.get(i+1))));
    			parts.remove(i);
    			parts.remove(i);
    			i=i-1;
    		}
    	}
    }
    
 
}
