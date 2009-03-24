package org.openscada.ae.net;

public class ListQueryResult {

	public ListQueryResult() {
	}

	public ListQueryResult(

	final java.util.Collection<org.openscada.ae.net.ListQueryResult> queries

	) {

		this.queries = queries;

	}

	private java.util.Collection<org.openscada.ae.net.ListQueryResult> queries;

	public void setQueries(
			final java.util.Collection<org.openscada.ae.net.ListQueryResult> queries) {
		this.queries = queries;
	}

	public java.util.Collection<org.openscada.ae.net.ListQueryResult> getQueries() {
		return queries;
	}

	public static ListQueryResult fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		ListQueryResult bean = new ListQueryResult();

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("queries");
			if (value != null
					&& value instanceof org.openscada.net.base.data.ListValue) {
				java.util.Collection<org.openscada.ae.net.ListQueryResult> list = new java.util.LinkedList<org.openscada.ae.net.ListQueryResult>();

				for (org.openscada.net.base.data.Value entry : ((org.openscada.net.base.data.ListValue) value)
						.getValues()) {
					if (entry instanceof org.openscada.net.base.data.MapValue) {
						list
								.add(org.openscada.ae.net.ListQueryResult
										.fromValue((org.openscada.net.base.data.MapValue) value));
					}
				}

				bean.setQueries(list);
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(
			final ListQueryResult bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final org.openscada.ae.net.ListQueryResult entry : bean
					.getQueries()) {
				listValue.add(org.openscada.ae.net.ListQueryResult
						.toValue(entry));
			}
			value.getValues().put("queries", listValue);
		}

		return value;
	}

}
