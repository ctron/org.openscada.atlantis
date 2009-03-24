package org.openscada.ae.net;

public class QueryUpdate {

	public QueryUpdate() {
	}

	public QueryUpdate(

			final boolean fullUpdate,
			final java.util.Collection<org.openscada.ae.net.QueryUpdateEntry> entries,
			final String queryId

	) {

		this.fullUpdate = fullUpdate;

		this.entries = entries;

		this.queryId = queryId;

	}

	private boolean fullUpdate;

	public void setFullUpdate(final boolean fullUpdate) {
		this.fullUpdate = fullUpdate;
	}

	public boolean getFullUpdate() {
		return fullUpdate;
	}

	private java.util.Collection<org.openscada.ae.net.QueryUpdateEntry> entries;

	public void setEntries(
			final java.util.Collection<org.openscada.ae.net.QueryUpdateEntry> entries) {
		this.entries = entries;
	}

	public java.util.Collection<org.openscada.ae.net.QueryUpdateEntry> getEntries() {
		return entries;
	}

	private String queryId;

	public void setQueryId(final String queryId) {
		this.queryId = queryId;
	}

	public String getQueryId() {
		return queryId;
	}

	public static QueryUpdate fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		QueryUpdate bean = new QueryUpdate();

		bean.setFullUpdate(entityValue.containsKey("fullUpdate"));

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("entries");
			if (value != null
					&& value instanceof org.openscada.net.base.data.ListValue) {
				java.util.Collection<org.openscada.ae.net.QueryUpdateEntry> list = new java.util.LinkedList<org.openscada.ae.net.QueryUpdateEntry>();

				for (org.openscada.net.base.data.Value entry : ((org.openscada.net.base.data.ListValue) value)
						.getValues()) {
					if (entry instanceof org.openscada.net.base.data.MapValue) {
						list
								.add(org.openscada.ae.net.QueryUpdateEntry
										.fromValue((org.openscada.net.base.data.MapValue) value));
					}
				}

				bean.setEntries(list);
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("queryId");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean
						.setQueryId(((org.openscada.net.base.data.StringValue) value)
								.getValue());
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(
			final QueryUpdate bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		if (bean.getFullUpdate()) {
			value.getValues().put("fullUpdate",
					new org.openscada.net.base.data.VoidValue());
		}

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final org.openscada.ae.net.QueryUpdateEntry entry : bean
					.getEntries()) {
				listValue.add(org.openscada.ae.net.QueryUpdateEntry
						.toValue(entry));
			}
			value.getValues().put("entries", listValue);
		}

		value.getValues().put("queryId",
				new org.openscada.net.base.data.StringValue(bean.getQueryId()));

		return value;
	}

}
