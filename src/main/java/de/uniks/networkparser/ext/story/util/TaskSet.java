package de.uniks.networkparser.ext.story.util;

import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.ext.story.StoryBook;
import de.uniks.networkparser.ext.story.Line;
import de.uniks.networkparser.ext.story.LogEntry;
import de.uniks.networkparser.ext.story.PartTask;
import de.uniks.networkparser.ext.story.Task;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;

import java.util.Collections;

public class TaskSet extends SimpleSet<Task> {

	public Class<?> getTypClass() {
		return Task.class;
	}

	public TaskSet() {
		// empty
	}

	public TaskSet(Task... objects) {
		for (Task obj : objects) {
			this.add(obj);
		}
	}

	public TaskSet(Collection<Task> objects) {
		this.addAll(objects);
	}

	public static final TaskSet EMPTY_SET = new TaskSet().withFlag(TaskSet.READONLY);

	@Override
	public TaskSet getNewList(boolean keyValue) {
		return new TaskSet();
	}

	@SuppressWarnings("unchecked")
	public TaskSet with(Object value) {
		if (value == null) {
			return this;
		} else if (value instanceof java.util.Collection) {
			this.addAll((Collection<Task>) value);
		} else if (value != null) {
			this.add((Task) value);
		}
		return this;
	}

	public StringList getComplexity() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getComplexity());
		}
		return result;
	}

	public TaskSet filterComplexity(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getComplexity()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withComplexity(String value) {
		for (Task obj : this) {
			obj.setComplexity(value);
		}
		return this;
	}

	public NumberList getCreated() {
		NumberList result = new NumberList();
		for (Task obj : this) {
			result.add(obj.getCreated());
		}
		return result;
	}

	public TaskSet filterCreated(int value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getCreated()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withCreated(int value) {
		for (Task obj : this) {
			obj.setCreated(value);
		}
		return this;
	}

	public StringList getCreater() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getCreater());
		}
		return result;
	}

	public TaskSet filterCreater(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getCreater()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withCreater(String value) {
		for (Task obj : this) {
			obj.setCreater(value);
		}
		return this;
	}

	public StringList getDescription() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getDescription());
		}
		return result;
	}

	public TaskSet filterDescription(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getDescription()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withDescription(String value) {
		for (Task obj : this) {
			obj.setDescription(value);
		}
		return this;
	}

	public StringList getEstimate() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getEstimate());
		}
		return result;
	}

	public TaskSet filterEstimate(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getEstimate()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withEstimate(String value) {
		for (Task obj : this) {
			obj.setEstimate(value);
		}
		return this;
	}

	public StringList getName() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getName());
		}
		return result;
	}

	public TaskSet filterName(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getName()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withName(String value) {
		for (Task obj : this) {
			obj.setName(value);
		}
		return this;
	}

	public StringList getType() {
		StringList result = new StringList();
		for (Task obj : this) {
			result.add(obj.getType());
		}
		return result;
	}

	public TaskSet filterType(String value) {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			if (value == obj.getType()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TaskSet withType(String value) {
		for (Task obj : this) {
			obj.setType(value);
		}
		return this;
	}

	public TaskSet getLiesOn() {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			result.with(obj.getLiesOn());
		}
		return result;
	}

	public TaskSet filterLiesOn(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		TaskSet answer = new TaskSet();
		for (Task obj : this) {
			if (neighbors.contains(obj.getLiesOn()) || (neighbors.isEmpty() && obj.getLiesOn() == null)) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public TaskSet withLiesOn(Line value) {
		for (Task obj : this) {
			obj.withLiesOn(value);
		}
		return this;
	}

	public TaskSet getPart() {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			result.with(obj.getPart());
		}
		return result;
	}

	public TaskSet filterPart(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		TaskSet answer = new TaskSet();
		for (Task obj : this) {
			if (!Collections.disjoint(neighbors, obj.getPart())) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public TaskSet withPart(PartTask value) {
		for (Task obj : this) {
			obj.withPart(value);
		}
		return this;
	}

	public TaskSet getUpdate() {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			result.with(obj.getUpdate());
		}
		return result;
	}

	public TaskSet filterUpdate(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		TaskSet answer = new TaskSet();
		for (Task obj : this) {
			if (!Collections.disjoint(neighbors, obj.getUpdate())) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public TaskSet withUpdate(LogEntry value) {
		for (Task obj : this) {
			obj.withUpdate(value);
		}
		return this;
	}

	public TaskSet getOwner() {
		TaskSet result = new TaskSet();
		for (Task obj : this) {
			result.with(obj.getOwner());
		}
		return result;
	}

	public TaskSet filterOwner(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		TaskSet answer = new TaskSet();
		for (Task obj : this) {
			if (neighbors.contains(obj.getOwner()) || (neighbors.isEmpty() && obj.getOwner() == null)) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public TaskSet withOwner(StoryBook value) {
		for (Task obj : this) {
			obj.withOwner(value);
		}
		return this;
	}
}