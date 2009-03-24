package org.openscada.core;

public class CreateSessionData {

	public CreateSessionData() {
	}

	public CreateSessionData(

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

	public static CreateSessionData fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		CreateSessionData bean = new CreateSessionData();

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
			final CreateSessionData bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		value.getValues().put(
				"properties",
				org.openscada.core.net.MessageHelper.attributesToMap(bean
						.getProperties()));

		return value;
	}

}
