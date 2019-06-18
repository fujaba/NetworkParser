package de.uniks.networkparser.ext;

import java.util.Collection;

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
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.ModifyEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.xml.HTMLEntity;

public class ClassModel extends GraphModel {
	public static final int ONE = 1;
	public static final int MANY = 42;

	private ModelGenerator generator = new ModelGenerator().withDefaultModel(this);

	/**
	 * Constructor
	 *
	 * @param packageName PackageName of ClassModel
	 */
	public ClassModel(String packageName) {
		this();
		with(packageName);
	}

	public ClassModel() {
		name = getDefaultPackage();
		setAuthorName(System.getProperty("user.name"));
	}

	public void resetGenerator() {
		this.generator = new ModelGenerator().withDefaultModel(this);
	}

	public ClassModel withFeature(Feature... feature) {
		this.generator.withFeature(feature);
		return this;
	}
	
	public ClassModel withFeature(FeatureSet feature) {
		this.generator.withFeature(feature);
		return this;
	}

	public ClassModel withoutFeature(Feature feature) {
		this.generator.withoutFeature(feature);
		return this;
	}

	@Override
	public boolean remove(GraphMember member) {
		return super.remove(member);
	}

	public ModelGenerator getGenerator(String... params) {
		if (params != null) {
			if (params.length == 1 && params[0] != null) {
				this.generator.withRootDir(params[0]);
			}
		}
		return generator;
	}

	public Feature getFeature(Feature feature, Clazz... clazzes) {
		return this.generator.getFeature(feature, clazzes);
	}

	@Override
	public HTMLEntity dumpHTML(String diagramName, boolean... write) {
		if (diagramName == null || diagramName.length() < 1) {
			diagramName = this.getName();
		}
		if (diagramName == null) {
			diagramName = "Model";
		}
		if (diagramName.length() < 1) {
			return null;
		}
		HTMLEntity entity = super.dumpHTML(diagramName, write);
		
		if(diagramName.indexOf('/')<0) {
			diagramName = "doc/" + diagramName;
		}
		
		diagramName = Story.addResource(entity, diagramName, false);
		
		if (write == null || write.length < 1 || write[0] == false) {
			return entity;
		}
		String htmlText = entity.toString();
		if (FileBuffer.writeFile(diagramName, htmlText) >= 0) {
			return entity;
		}
		return null;
	}

	@Override
	public ClassModel generate(String... rootDir) {
		String path = null;
		if (rootDir != null && rootDir.length > 0) {
			path = rootDir[0];
		}
		getGenerator().generate(path, this);
		return this;
	}

	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return true;
		}
		boolean add = true;
		for (Object item : values) {
			if (item instanceof Collection<?>) {
				Collection<?> items = (Collection<?>) item;
				for (Object i : items) {
					add = add(i);
				}
				continue;
			}
			if (item instanceof Match) {
				/* Change */
				Match match = (Match) item;
				GraphMember member = match.getMatch();
				Clazz clazz = this.createClazz(member.getClazz().getName());
				ModifyEntry modifier = ModifyEntry.createModifier(member);
				GraphUtil.withChildren(clazz, modifier);

				Object newValue = match.getNewValue();
				if (newValue instanceof Attribute) {
					GraphUtil.withChildren(clazz, (GraphMember) newValue);
				} else if (newValue instanceof DataType) {
					if (member instanceof Attribute) {
						clazz.createAttribute(member.getName(), (DataType) newValue);
					}
				}
			}
			if (item instanceof Annotation) {
				super.withAnnotation((Annotation) item);
			} else if (item instanceof Clazz) {
				Clazz clazz = (Clazz) item;
				clazz.setClassModel(this);
			} else {
				add = false;
			}
		}
		return add;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ClassModel();
	}

	/* Override some Method because change ReturnValue */
	@Override
	public ClassModel with(String name) {
		super.with(name);
		return this;
	}

	@Override
	public ClassModel with(Annotation value) {
		super.with(value);
		return this;
	}
}