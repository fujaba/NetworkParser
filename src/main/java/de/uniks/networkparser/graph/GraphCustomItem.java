package de.uniks.networkparser.graph;

/*
NetworkParser
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

/**
 * The Class GraphCustomItem.
 *
 * @author Stefan
 */
public class GraphCustomItem extends GraphMember {
	
	/** The Constant CREATE. */
	public static final GraphCustomItem CREATE = new GraphCustomItem().withStyle("create");
	
	/** The Constant ACTOR. */
	public static final String ACTOR = "actor";
	private GraphNode parentNode;

	private String style;

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the graph custom item
	 */
	@Override
	public GraphCustomItem with(String name) {
		super.with(name);
		return this;
	}
	
	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @return the graph custom item
	 */
	public static GraphCustomItem create(String value) {
		return new GraphCustomItem().with(value);
	}

	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @param style the style
	 * @return the graph custom item
	 */
	public static GraphCustomItem create(String value, String style) {
		return new GraphCustomItem().with(value).withStyle(style);
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * With style.
	 *
	 * @param style the style
	 * @return the graph custom item
	 */
	public GraphCustomItem withStyle(String style) {
		this.style = style;
		return this;
	}


	/**
	 * With parent.
	 *
	 * @param value the value
	 * @return the graph custom item
	 */
	public GraphCustomItem withParent(GraphNode value) {
		if (this.parentNode != value) {
			GraphNode oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.remove(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.withChildren(this);
			}
		}
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "" + name;
	}

	/**
	 * Creates the actor image.
	 *
	 * @return the graph custom item
	 */
	public static GraphCustomItem createActorImage() {
		return new GraphCustomItem().with(ACTOR);
	}
}
