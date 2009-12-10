package org.openscada.ae.server.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.memory.internal.EventMatcher;
import org.openscada.ae.server.storage.memory.internal.FilterUtils;
import org.openscada.ae.server.storage.memory.internal.EventMatcher.MatchMode;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterParseException;
import org.openscada.utils.filter.FilterParser;

public class ListQuery implements Query {

	private final Iterator<Event> iterator;
	
	private final Filter filter;

	private final MatchMode matchMode;

	private Event bufferedEvent = null;

	public ListQuery(SortedSet<Event> events, String filter, MatchMode matchMode)
			throws FilterParseException {
		this.filter = new FilterParser(filter).getFilter();
		FilterUtils.toVariant(this.filter);
		this.matchMode = matchMode;
		this.iterator = events.iterator();
	}

	public Collection<Event> getNext(long count) throws Exception {
		List<Event> result = new ArrayList<Event>();

		if (bufferedEvent != null) {
			result.add(bufferedEvent);
			bufferedEvent = null;
			if (count == 1) {
				return result;
			}
		}
		
		while (next() != null) {
			result.add(bufferedEvent);
			bufferedEvent = null;
			if (result.size() == count) {
				break;
			}
		}
		return result;
	}

	public boolean hasMore() {
		if (bufferedEvent == null && iterator.hasNext()) {
			next();
		}
		return bufferedEvent != null;
	}

	private Event next() {
		while (iterator.hasNext()) {
			Event event = (Event) iterator.next();
			if (EventMatcher.matches(event, filter, matchMode)) {
				bufferedEvent = event;
				return event;
			}
		}
		return null;
	}

	public void dispose() {
	}
}
