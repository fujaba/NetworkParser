package de.uniks.networkparser.calculator;

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

public class RegExParser {
	public static Boolean regex(String str, String pattern)
	    {
	        if ( str == null || str.isEmpty() || pattern == null || pattern.isEmpty()) {
	            return false;
	        }

	        for (int i = 0; i < str.length(); i++) {
	            if ( match(str.substring(i), pattern) ) {
	                return true;
	            }
	        }

	        return false;
	    }

	    private static Boolean match(String str, String pattern)
	    {
	        if ( pattern.length() == 2 && pattern.charAt(1) == '*') {
	            return true;
	        }
	        else if ( str.isEmpty()  || pattern.isEmpty() ) {
	            return false;
	        }
	        else {
	            if ( (pattern.length() > 1 && pattern.charAt(1) == '*') ) {
	                int index = 0;
	                while (index < str.length()  && 
	                       (pattern.charAt(0) == str.charAt(index) || pattern.charAt(0) == '.') ) {
	                    if (match(str.substring(index + 1), pattern)){
	                        return true;
	                    }
	                    index++;
	                }
	                return match(str, pattern.substring(2)) || pattern.length() == 2;
	            }
	            else if ( (pattern.length() > 1 && pattern.charAt(1) == '+')  )
	            {
	                int index = 0;
	                boolean match = false;
	                while (index < str.length()  && 
	                       (pattern.charAt(0) == str.charAt(index) || pattern.charAt(0) == '.') ) {
	                    match = true;
	                    if (match(str.substring(index + 1), pattern)){
	                        return true;
	                    }
	                    index++;
	                }
	                return match && match(str.substring(1), pattern.substring(2)) || match && pattern.length() == 2;

	            }
	            else if ( pattern.charAt(0) == str.charAt(0) || pattern.charAt(0) == '.' ) {
	                if (pattern.length() == 1) {
	                    return true;
	                }
	                else {
	                    return match(str.substring(1), pattern.substring(1));
	                }
	            }
	            else {
	                return false;
	            }
	        }
	    }
	}
