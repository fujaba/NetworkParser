package de.uniks.networkparser.ext.story;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.GraphImage;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphNode;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class StoryStepDiagram implements ObjectCondition {
	private GraphModel model;
	private StoryObjectFilter filter;

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		Story story = (Story) evt.getSource();

		if (this.model != null) {
			for (String item : HTMLEntity.GRAPHRESOURCES) {
				if (element.getHeader(item) == null) {
					// DEFAULT TO EXTRACT TO DOC-FOLDER
					Story.addScript(story.getPath(), item, element);
				}
			}
			element.withGraph(this.model, null);
		} else if (filter != null) {
			// Objectdiagramm
			IdMap map = story.getMap();
			// Save all Names
			SimpleKeyValueList<Object, String> ids = filter.getIds();
			for (int i = 0; i < ids.size(); i++) {
				Object obj = ids.getKeyByIndex(i);
				String name = ids.getValueByIndex(i);
				map.put(name, obj, false);
			}
			JsonArray jsonArray = new JsonArray();

			SimpleList<Object> elements = filter.getElements();
			// Add All Elements to JsonArray
			for (Object object : elements) {
				JsonObject jsonObject = map.toJsonObject(object, filter);
				jsonArray.add(jsonObject);
			}

			// add icons
			SimpleKeyValueList<String, String> images = filter.getImages();
			for (int i = 0; i < images.size(); i++) {
				String id = images.getKeyByIndex(i);
				JsonObject jsonObject = jsonArray.get(id);
				if (jsonObject != null) {
					String image = images.getValueByIndex(i);
					jsonObject.put("head", new JsonObject().withKeyValue("src", image));
				}
			}
			// new diagram
			GraphConverter graphConverter = new GraphConverter();
			Entity objectModel = graphConverter.convertToJson(GraphTokener.OBJECTDIAGRAM, jsonArray, true);
			element.withGraph(objectModel);
		}
		return true;
	}

	public StoryStepDiagram withModel(GraphModel model) {
		this.model = model;
		return this;
	}

	public void withFilter(StoryObjectFilter filter) {
		this.filter = filter;
	}

	public GraphModel createUseCaseDiagram() {
		GraphList useCase = new GraphList();
		this.model = useCase;
		useCase.withType(GraphTokener.OBJECTDIAGRAM);
		return model;
	}

	public GraphImage cretaeActor() {
		GraphImage actor = GraphImage.createActor();
		if (this.model != null) {
			this.model.add(actor);
		}
		return actor;
	}

	public GraphNode createElement(String value) {
		GraphNode node = new GraphNode().with(value);
		if (this.model != null) {
			this.model.add(node);
		}
		return node;
	}
}