package org.openscada.ae.net;

public class SubscriptionInformation {

	public SubscriptionInformation() {
	}

	public SubscriptionInformation(

	final String query

	) {

		this.query = query;

	}

	private String query;

	public void setQuery(final String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public static SubscriptionInformation fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		SubscriptionInformation bean = new SubscriptionInformation();

		{
			org.openscada.net.base.data.Value value = entityValue.get("query");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean.setQuery(((org.openscada.net.base.data.StringValue) value)
						.getValue());
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(
			final SubscriptionInformation bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		value.getValues().put("query",
				new org.openscada.net.base.data.StringValue(bean.getQuery()));

		return value;
	}

}
