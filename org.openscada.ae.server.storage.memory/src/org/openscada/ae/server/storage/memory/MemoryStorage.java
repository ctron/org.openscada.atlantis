package org.openscada.ae.server.storage.memory;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.memory.internal.EventMatcher.MatchMode;

public class MemoryStorage implements Storage {

	private List<Event> events = new CopyOnWriteArrayList<Event>();
	
	private final MatchMode matchMode;

	public MemoryStorage(MatchMode matchMode) {
		this.matchMode = matchMode;
	}
	
	public Query query(String filter) throws Exception {
		return new ListQuery(events, filter, matchMode);
	}

	public Event store(Event event) {
		Event storedEvent = Event.create().event(event).id(UUID.randomUUID())
				.entryTimestamp(new GregorianCalendar().getTime()).build();
		events.add(storedEvent);
		return storedEvent;
	}

	public List<Event> getEvents() {
		return Collections.unmodifiableList(events);
	}
}
