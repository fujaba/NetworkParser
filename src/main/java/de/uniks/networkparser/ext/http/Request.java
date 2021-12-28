package de.uniks.networkparser.ext.http;

import java.util.List;

public class Request {
	public String path;
	public String type; // GET, OR POST
	public String summery;
	public String description;
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
