package org.openscada.ae.net;

public class SessionRequest {

	public SessionRequest() {
	}

	public SessionRequest(

	final java.util.Map<String, org.openscada.core.Variant> properties

	) {

		this.properties = properties;

	}

	private java.util.Map<String, org.openscada.core.Variant> properties;

	public void setProperties(
			final java.util.Map<String, org.openscada.core.Variant> properties) {
		this.properties = properties;
	}

	public java.util.Map<String, org.openscada.core.Variant> getProperties() {
		return properties;
	}

	public static SessionRequest fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		SessionRequest bean = new SessionRequest();

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("properties");
			if (value instanceof org.openscada.net.base.data.MapValue) {
				bean
						.setProperties(org.openscada.core.net.MessageHelper
								.mapToAttributes((org.openscada.net.base.data.MapValue) value));
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(
			final SessionRequest bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		value.getValues().put(
				"properties",
				org.openscada.core.net.MessageHelper.attributesToMap(bean
						.getProperties()));

		return value;
	}

}
