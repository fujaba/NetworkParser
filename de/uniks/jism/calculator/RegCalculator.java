package de.uniks.jism.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.uniks.jism.date.StringTokener;

public class RegCalculator {
    /** List of Operators */
    private HashMap<String, Operator> operators = new HashMap<String, Operator>();
 
    /** List of Constants */
    private Map<String, Double> constants = new HashMap<String, Double>();
    
    public RegCalculator withStandard(){
    	withOperator(new Addition());
    	withOperator(new Subtract());
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
    				break;
    			}else if(operators.containsKey(value)){
    				parts.add( value );
    				break;
    			}
    			current = tokener.next();
    			value += current;
    		}
			current=null;
    	}
    	
    	parse(parts);
    	
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
