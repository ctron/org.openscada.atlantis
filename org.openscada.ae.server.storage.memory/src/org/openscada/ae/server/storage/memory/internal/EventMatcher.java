package org.openscada.ae.server.storage.memory.internal;

import org.openscada.ae.Event;
import org.openscada.core.Variant;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.Operator;

public class EventMatcher {

	public enum MatchMode {
		HANDLE_UNKNOWN_AS_TRUE, HANDLE_UNKNOWN_AS_FALSE;
	}

	public static boolean matches(Event event, Filter filter,
			MatchMode matchMode) {
		if (filter.isEmpty()) {
			return true;
		}
		if (filter.isAssertion()) {
			return matchesAssertion(event, (FilterAssertion) filter, matchMode);
		} else if (filter.isExpression()) {
			return matchesExpression(event, (FilterExpression) filter,
					matchMode);
		}
		return false;
	}

	private static boolean matchesExpression(Event event,
			FilterExpression filter, MatchMode matchMode) {
		if (filter.getOperator() == Operator.NOT) {
			return !matches(event, filter.getFilterSet().get(0), matchMode);
		}
		if (filter.getOperator() == Operator.OR) {
			boolean match = false;
			for (Filter	f : filter.getFilterSet()) {
				match = match || matches(event, f, matchMode);
			}
			return match;
		}
		if (filter.getOperator() == Operator.AND) {
			boolean match = true;
			for (Filter	f : filter.getFilterSet()) {
				match = match && matches(event, f, matchMode);
			}
			return match;
		}
		return false;
	}

	private static boolean matchesAssertion(Event event,
			FilterAssertion filter, MatchMode matchMode) {
		if (!hasAttribute(event, filter.getAttribute())) {
			if (matchMode == MatchMode.HANDLE_UNKNOWN_AS_FALSE) {
				return false;
			}
			if (matchMode == MatchMode.HANDLE_UNKNOWN_AS_TRUE) {
				return true;
			}
		}
		return compare(filter.getAssertion(), getEventValue(event, filter.getAttribute()), (Variant) filter.getValue());
	}

	private static boolean compare(Assertion assertion, Object eventValue,
			Object filterValue) {
		if (eventValue == null && filterValue == null) {
			return true;
		}
		if (eventValue == null && filterValue != null) {
			return false;
		}
		if (eventValue != null && filterValue == null) {
			return false;
		}
		if (assertion == Assertion.EQUALITY) {
			return eventValue.equals(filterValue);
		}
		return false;
	}

	private static Object getEventValue(Event event, String attribute) {
		if ("id".equals(attribute)) {
			return new Variant(event.getId().toString());
		}
		if ("sourceTimestamp".equals(attribute)) {
			return event.getSourceTimestamp();
		}
		if ("entryTimestamp".equals(attribute)) {
			return event.getEntryTimestamp();
		}
		return event.getAttributes().get(attribute);
	}
	
	private static boolean hasAttribute(Event event, String attribute) {
		if ("id".equals(attribute)) {
			return true;
		}
		if ("sourceTimestamp".equals(attribute)) {
			return true;
		}
		if ("entryTimestamp".equals(attribute)) {
			return true;
		}
		if (event.getAttributes().containsKey(attribute)) {
			return true;
		}
		return false;
	}
}
