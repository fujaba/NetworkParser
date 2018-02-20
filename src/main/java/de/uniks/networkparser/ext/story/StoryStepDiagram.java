package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphTokener;
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
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		Story story = (Story) evt.getSource();

		if(this.model != null) {
			element.withGraph(this.model);
		}else if(filter!= null){
			// Objectdiagramm
			IdMap map = story.getMap();
			// Save all Names
			SimpleKeyValueList<Object, String> ids = filter.getIds();
			for(int i=0;i<ids.size();i++) {
				Object obj = ids.getKeyByIndex(i);
				String name = ids.getValueByIndex(i);
				map.put(name, obj, false);
			}
			 JsonArray jsonArray = new JsonArray();
			

			SimpleList<Object> elements = filter.getElements();
			// Add All Elements to JsonArray
			for(Object object : elements) {
				JsonObject jsonObject = map.toJsonObject(object, filter);
				jsonArray.add(jsonObject);
			}

	      // add icons
			SimpleKeyValueList<String, String> images = filter.getImages();
			for(int i=0;i<images.size();i++) {
				String id = images.getKeyByIndex(i);
				JsonObject jsonObject = jsonArray.get(id);
				if (jsonObject != null)
				{
					String image = images.getValueByIndex(i);
					jsonObject.put("head", new JsonObject().withKeyValue("src", image));
				}
			}
		      // new diagram
		      GraphConverter graphConverter = new GraphConverter();
		      JsonObject objectModel = graphConverter.convertToJson(GraphTokener.OBJECT, jsonArray, true);
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
}
