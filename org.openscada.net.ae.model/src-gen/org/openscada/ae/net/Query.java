package org.openscada.ae.net;

public class Query {

	public Query() {
	}

	public Query(

	final String name,
			final java.util.Map<String, org.openscada.core.Variant> attributes

	) {

		this.name = name;

		this.attributes = attributes;

	}

	private String name;

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private java.util.Map<String, org.openscada.core.Variant> attributes;

	public void setAttributes(
			final java.util.Map<String, org.openscada.core.Variant> attributes) {
		this.attributes = attributes;
	}

	public java.util.Map<String, org.openscada.core.Variant> getAttributes() {
		return attributes;
	}

	public static Query fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		Query bean = new Query();

		{
			org.openscada.net.base.data.Value value = entityValue.get("name");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean.setName(((org.openscada.net.base.data.StringValue) value)
						.getValue());
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("attributes");
			if (value instanceof org.openscada.net.base.data.MapValue) {
				bean
						.setAttributes(org.openscada.core.net.MessageHelper
								.mapToAttributes((org.openscada.net.base.data.MapValue) value));
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(final Query bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		value.getValues().put("name",
				new org.openscada.net.base.data.StringValue(bean.getName()));

		value.getValues().put(
				"attributes",
				org.openscada.core.net.MessageHelper.attributesToMap(bean
						.getAttributes()));

		return value;
	}

}
