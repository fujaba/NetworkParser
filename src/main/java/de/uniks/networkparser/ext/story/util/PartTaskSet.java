package de.uniks.networkparser.ext.story.util;

import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.ext.story.PartTask;
import de.uniks.networkparser.ext.story.Task;
import de.uniks.networkparser.list.ObjectSet;

public class PartTaskSet extends SimpleSet<PartTask> {

	public Class<?> getTypClass() {
		return PartTask.class;
	}

	public PartTaskSet() {
		// empty
	}

	public PartTaskSet(PartTask... objects) {
		for (PartTask obj : objects) {
			this.add(obj);
		}
	}

	public PartTaskSet(Collection<PartTask> objects) {
		this.addAll(objects);
	}

	public static final PartTaskSet EMPTY_SET = new PartTaskSet().withFlag(PartTaskSet.READONLY);

	@Override
	public PartTaskSet getNewList(boolean keyValue) {
		return new PartTaskSet();
	}

	@SuppressWarnings("unchecked")
	public PartTaskSet with(Object value) {
		if (value == null) {
			return this;
		} else if (value instanceof java.util.Collection) {
			this.addAll((Collection<PartTask>) value);
		} else if (value != null) {
			this.add((PartTask) value);
		}
		return this;
	}

	public StringList getType() {
		StringList result = new StringList();
		for (PartTask obj : this) {
			result.add(obj.getType());
		}
		return result;
	}

	public PartTaskSet filterType(String value) {
		PartTaskSet result = new PartTaskSet();
		for (PartTask obj : this) {
			if (value == obj.getType()) {
				result.add(obj);
			}
		}
		return result;
	}

	public PartTaskSet withType(String value) {
		for (PartTask obj : this) {
			obj.setType(value);
		}
		return this;
	}

	public StringList getValue() {
		StringList result = new StringList();
		for (PartTask obj : this) {
			result.add(obj.getValue());
		}
		return result;
	}

	public PartTaskSet filterValue(String value) {
		PartTaskSet result = new PartTaskSet();
		for (PartTask obj : this) {
			if (value == obj.getValue()) {
				result.add(obj);
			}
		}
		return result;
	}

	public PartTaskSet withValue(String value) {
		for (PartTask obj : this) {
			obj.setValue(value);
		}
		return this;
	}

	public PartTaskSet getTask() {
		PartTaskSet result = new PartTaskSet();
		for (PartTask obj : this) {
			result.with(obj.getTask());
		}
		return result;
	}

	public PartTaskSet filterTask(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		PartTaskSet answer = new PartTaskSet();
		for (PartTask obj : this) {
			if (neighbors.contains(obj.getTask()) || (neighbors.isEmpty() && obj.getTask() == null)) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public PartTaskSet withTask(Task value) {
		for (PartTask obj : this) {
			obj.withTask(value);
		}
		return this;
	}
}