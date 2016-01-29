package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
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

public class GraphLabel extends GraphMember {
	public final static GraphLabel CREATE=new GraphLabel().withStyle("create");
	private String style;

	@Override
	public GraphLabel with(String name) {
		super.with(name);
		return this;
	}

	public static GraphLabel create(String value) {
		return new GraphLabel().with(value);
	}

	public static GraphLabel create(String value, String style) {
		return new GraphLabel().with(value).withStyle(style);
	}

	public String getStyle() {
		return style;
	}

	public GraphLabel withStyle(String style) {
		this.style = style;
		return this;
	}
}
