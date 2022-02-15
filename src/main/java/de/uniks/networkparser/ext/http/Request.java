package de.uniks.networkparser.ext.http;

import java.util.List;

/**
 * The Class Request.
 *
 * @author Stefan
 */
public class Request {
	
	/** The path. */
	public String path;
	
	/** The type. */
	public String type; // GET, OR POST
	
	/** The summery. */
	public String summery;
	
	/** The description. */
	public String description;
	
	/** The produces. */
	public List<String> produces; //"application/json"
	
	
//	"paths": {
//	    "/users": {
//	      "get": {
//	        "summary": "Returns a list of users.",
//	        "description": "Optional extended description in Markdown.",
//	        "produces": [
//	          "application/json"
//	        ],
//	        "responses": {
//	          "200": {
//	            "description": "OK"
//	          }
//	        }
//	      }
//	    }
//	  }
//	}
}
