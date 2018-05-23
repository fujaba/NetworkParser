package de.uniks.networkparser.ext.story.util;

import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;

import de.uniks.networkparser.ext.story.LogEntry;
import de.uniks.networkparser.ext.story.Task;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.ObjectSet;

public class LogEntrySet extends SimpleSet<LogEntry> {
	public static final LogEntrySet EMPTY_SET = new LogEntrySet().withFlag(LogEntrySet.READONLY);

	public Class<?> getTypClass() {
		return LogEntry.class;
	}

	public LogEntrySet() {
		// empty
	}

	public LogEntrySet(LogEntry... objects) {
		for (LogEntry obj : objects) {
			this.add(obj);
		}
	}

	public LogEntrySet(Collection<LogEntry> objects) {
		this.addAll(objects);
	}


	@Override
	public LogEntrySet getNewList(boolean keyValue) {
		return new LogEntrySet();
	}

	@SuppressWarnings("unchecked")
	public LogEntrySet with(Object value) {
		if (value == null) {
			return this;
		} else if (value instanceof java.util.Collection) {
			this.addAll((Collection<LogEntry>) value);
		} else if (value != null) {
			this.add((LogEntry) value);
		}
		return this;
	}

	public NumberList getCreated() {
		NumberList result = new NumberList();
		for (LogEntry obj : this) {
			result.add(obj.getCreated());
		}
		return result;
	}

	public LogEntrySet filterCreated(int value) {
		LogEntrySet result = new LogEntrySet();
		for (LogEntry obj : this) {
			if (value == obj.getCreated()) {
				result.add(obj);
			}
		}
		return result;
	}

	public LogEntrySet withCreated(int value) {
		for (LogEntry obj : this) {
			obj.setCreated(value);
		}
		return this;
	}

	public StringList getCreater() {
		StringList result = new StringList();
		for (LogEntry obj : this) {
			result.add(obj.getCreater());
		}
		return result;
	}

	public LogEntrySet filterCreater(String value) {
		LogEntrySet result = new LogEntrySet();
		for (LogEntry obj : this) {
			if (value == obj.getCreater()) {
				result.add(obj);
			}
		}
		return result;
	}

	public LogEntrySet withCreater(String value) {
		for (LogEntry obj : this) {
			obj.setCreater(value);
		}
		return this;
	}

	public StringList getType() {
		StringList result = new StringList();
		for (LogEntry obj : this) {
			result.add(obj.getType());
		}
		return result;
	}

	public LogEntrySet filterType(String value) {
		LogEntrySet result = new LogEntrySet();
		for (LogEntry obj : this) {
			if (value == obj.getType()) {
				result.add(obj);
			}
		}
		return result;
	}

	public LogEntrySet withType(String value) {
		for (LogEntry obj : this) {
			obj.setType(value);
		}
		return this;
	}

	public NumberList getValue() {
		NumberList result = new NumberList();
		for (LogEntry obj : this) {
			result.add(obj.getValue());
		}
		return result;
	}

	public LogEntrySet filterValue(int value) {
		LogEntrySet result = new LogEntrySet();
		for (LogEntry obj : this) {
			if (value == obj.getValue()) {
				result.add(obj);
			}
		}
		return result;
	}

	public LogEntrySet withValue(int value) {
		for (LogEntry obj : this) {
			obj.setValue(value);
		}
		return this;
	}

	public LogEntrySet getTask() {
		LogEntrySet result = new LogEntrySet();
		for (LogEntry obj : this) {
			result.with(obj.getTask());
		}
		return result;
	}

	public LogEntrySet filterTask(Object value) {
		ObjectSet neighbors = new ObjectSet();
		if (value instanceof Collection) {
			neighbors.addAll((Collection<?>) value);
		} else {
			neighbors.add(value);
		}
		LogEntrySet answer = new LogEntrySet();
		for (LogEntry obj : this) {
			if (neighbors.contains(obj.getTask()) || (neighbors.isEmpty() && obj.getTask() == null)) {
				answer.add(obj);
			}
		}
		return answer;
	}

	public LogEntrySet withTask(Task value) {
		for (LogEntry obj : this) {
			obj.withTask(value);
		}
		return this;
	}
}