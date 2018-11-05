package de.uniks.networkparser.ext;

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
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

public class MergeFeature extends Feature {
	public static final String IGNORE = "ignore";
	public static final String OVERRIDE = "override";
	public static final String CONFLICT = "conflict";
	public static final String CUSTOM = "custom";
	private SimpleEventCondition condition;

	public MergeFeature() {
		super("");
	}
	protected MergeFeature(Feature name) {
		super(name);
	}

	@Override
	protected Feature newInstance(Feature ref) {
		return new MergeFeature(ref);
	}

	/**
	 * @return the condition
	 */
	public SimpleEventCondition getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 * @return ThisComponent
	 */
	public MergeFeature withCondition(SimpleEventCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public MergeFeature withStringValue(String value) {
		super.withStringValue(value);
		return this;
	}

	public static MergeFeature createIgnore() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(IGNORE);
	}

	public static MergeFeature createOverride() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(OVERRIDE);
	}

	public static MergeFeature createConflict() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(CONFLICT);
	}

	public static MergeFeature createCustom() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(CUSTOM);
	}
}
