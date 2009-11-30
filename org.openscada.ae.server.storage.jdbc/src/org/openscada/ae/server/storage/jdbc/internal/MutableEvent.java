package org.openscada.ae.server.storage.jdbc.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;

/**
 * just used by hibernate. All other communication with other systems should
 * only happen through the immutable Event class
 * 
 * @author jrose
 */
public class MutableEvent {

	private UUID id;

	private Date sourceTimestamp;

	private Date entryTimestamp;

	// often used fields

	private String type;

	private String source;

	private Integer priority;

	// all other fields

	private Map<String, Variant> attributes = new HashMap<String, Variant>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getSourceTimestamp() {
		return sourceTimestamp;
	}

	public void setSourceTimestamp(Date sourceTimestamp) {
		this.sourceTimestamp = sourceTimestamp;
	}

	public Date getEntryTimestamp() {
		return entryTimestamp;
	}

	public void setEntryTimestamp(Date entryTimestamp) {
		this.entryTimestamp = entryTimestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Map<String, Variant> getAttributes() {
		return attributes;
	}

	public static Event toEvent(MutableEvent m) {
		Map<String, Variant> attr = new HashMap<String, Variant>(m
				.getAttributes());
		// often used fields
		attr.put(Event.Fields.SOURCE.getName(), new Variant(m.source));
		attr.put(Event.Fields.TYPE.getName(), new Variant(m.type));
		attr.put(Event.Fields.PRIORITY.getName(), new Variant(m.priority));
		return Event.create().id(m.id).sourceTimestamp(m.sourceTimestamp)
				.entryTimestamp(m.entryTimestamp).attributes(attr).build();
	}

	public static MutableEvent fromEvent(Event e) {
		MutableEvent m = new MutableEvent();
		// important fields
		m.setId(e.getId());
		m.setSourceTimestamp(e.getSourceTimestamp());
		m.setEntryTimestamp(e.getEntryTimestamp());
		// often used fields
		Map<String, Variant> attr = new HashMap<String, Variant>(e
				.getAttributes());
		Variant v;
		v = attr.remove(Event.Fields.SOURCE.getName());
		try {
			m.setSource(v == null ? null : v.asString());
		} catch (NullValueException ex) {
			m.setSource(null);
		}
		v = attr.remove(Event.Fields.TYPE.getName());
		try {
			m.setType(v == null ? null : v.asString());
		} catch (NullValueException ex) {
			m.setType(null);
		}
		v = attr.remove(Event.Fields.PRIORITY.getName());
		try {
			m.setPriority(v == null ? null : v.asInteger());
		} catch (NullValueException ex) {
			m.setPriority(null);
		} catch (NotConvertableException ex) {
			m.setPriority(null);
		}
		// all other
		m.getAttributes().putAll(attr);
		return m;
	}
	
	@Override
	public String toString ()
	{
	    return toEvent ( this ).toString ();
	}
}
