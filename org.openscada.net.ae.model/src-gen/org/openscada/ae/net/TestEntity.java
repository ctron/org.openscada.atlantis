package org.openscada.ae.net;

public class TestEntity {

	public TestEntity() {
	}

	public TestEntity(

			final org.openscada.core.Variant variant,
			final java.util.Collection<org.openscada.core.Variant> listVariant,
			final String basicValue,
			final java.util.Collection<String> listBasicValue,
			final java.util.Map<String, org.openscada.core.Variant> mapValue,
			final java.util.Collection<java.util.Map<String, org.openscada.core.Variant>> listMapValue,
			final boolean flagValue

	) {

		this.variant = variant;

		this.listVariant = listVariant;

		this.basicValue = basicValue;

		this.listBasicValue = listBasicValue;

		this.mapValue = mapValue;

		this.listMapValue = listMapValue;

		this.flagValue = flagValue;

	}

	private org.openscada.core.Variant variant;

	public void setVariant(final org.openscada.core.Variant variant) {
		this.variant = variant;
	}

	public org.openscada.core.Variant getVariant() {
		return variant;
	}

	private java.util.Collection<org.openscada.core.Variant> listVariant;

	public void setListVariant(
			final java.util.Collection<org.openscada.core.Variant> listVariant) {
		this.listVariant = listVariant;
	}

	public java.util.Collection<org.openscada.core.Variant> getListVariant() {
		return listVariant;
	}

	private String basicValue;

	public void setBasicValue(final String basicValue) {
		this.basicValue = basicValue;
	}

	public String getBasicValue() {
		return basicValue;
	}

	private java.util.Collection<String> listBasicValue;

	public void setListBasicValue(
			final java.util.Collection<String> listBasicValue) {
		this.listBasicValue = listBasicValue;
	}

	public java.util.Collection<String> getListBasicValue() {
		return listBasicValue;
	}

	private java.util.Map<String, org.openscada.core.Variant> mapValue;

	public void setMapValue(
			final java.util.Map<String, org.openscada.core.Variant> mapValue) {
		this.mapValue = mapValue;
	}

	public java.util.Map<String, org.openscada.core.Variant> getMapValue() {
		return mapValue;
	}

	private java.util.Collection<java.util.Map<String, org.openscada.core.Variant>> listMapValue;

	public void setListMapValue(
			final java.util.Collection<java.util.Map<String, org.openscada.core.Variant>> listMapValue) {
		this.listMapValue = listMapValue;
	}

	public java.util.Collection<java.util.Map<String, org.openscada.core.Variant>> getListMapValue() {
		return listMapValue;
	}

	private boolean flagValue;

	public void setFlagValue(final boolean flagValue) {
		this.flagValue = flagValue;
	}

	public boolean getFlagValue() {
		return flagValue;
	}

	public static TestEntity fromValue(
			final org.openscada.net.base.data.MapValue entityValue) {
		TestEntity bean = new TestEntity();

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("variant");
			bean.setVariant(org.openscada.core.net.MessageHelper
					.valueToVariant(value, new org.openscada.core.Variant()));
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("listVariant");
			if (value != null
					&& value instanceof org.openscada.net.base.data.ListValue) {
				java.util.Collection<org.openscada.core.Variant> list = new java.util.LinkedList<org.openscada.core.Variant>();

				for (org.openscada.net.base.data.Value entry : ((org.openscada.net.base.data.ListValue) value)
						.getValues()) {
					list.add(org.openscada.core.net.MessageHelper
							.valueToVariant(entry,
									new org.openscada.core.Variant()));
				}

				bean.setListVariant(list);
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("basicValue");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean
						.setBasicValue(((org.openscada.net.base.data.StringValue) value)
								.getValue());
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("listBasicValue");
			if (value != null
					&& value instanceof org.openscada.net.base.data.ListValue) {
				java.util.Collection<String> list = new java.util.LinkedList<String>();

				for (org.openscada.net.base.data.Value entry : ((org.openscada.net.base.data.ListValue) value)
						.getValues()) {
					if (entry instanceof org.openscada.net.base.data.StringValue) {
						list
								.add(((org.openscada.net.base.data.StringValue) entry)
										.getValue());
					}
				}

				bean.setListBasicValue(list);
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("mapValue");
			if (value instanceof org.openscada.net.base.data.MapValue) {
				bean
						.setMapValue(org.openscada.core.net.MessageHelper
								.mapToAttributes((org.openscada.net.base.data.MapValue) value));
			}
		}

		{
			org.openscada.net.base.data.Value value = entityValue
					.get("listMapValue");
			if (value != null
					&& value instanceof org.openscada.net.base.data.ListValue) {
				java.util.Collection<java.util.Map<String, org.openscada.core.Variant>> list = new java.util.LinkedList<java.util.Map<String, org.openscada.core.Variant>>();

				for (org.openscada.net.base.data.Value entry : ((org.openscada.net.base.data.ListValue) value)
						.getValues()) {
					if (entry instanceof org.openscada.net.base.data.MapValue) {
						list
								.add(org.openscada.core.net.MessageHelper
										.mapToAttributes((org.openscada.net.base.data.MapValue) entry));
					}
				}

				bean.setListMapValue(list);
			}
		}

		bean.setFlagValue(entityValue.containsKey("flagValue"));

		return bean;
	}

	public static org.openscada.net.base.data.MapValue toValue(
			final TestEntity bean) {
		final org.openscada.net.base.data.MapValue value;
		value = new org.openscada.net.base.data.MapValue();

		value.getValues().put(
				"variant",
				org.openscada.core.net.MessageHelper.variantToValue(bean
						.getVariant()));

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final org.openscada.core.Variant entry : bean.getListVariant()) {
				listValue.add(org.openscada.core.net.MessageHelper
						.variantToValue(entry));
			}
			value.getValues().put("listVariant", listValue);
		}

		value.getValues().put(
				"basicValue",
				new org.openscada.net.base.data.StringValue(bean
						.getBasicValue()));

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final String entry : bean.getListBasicValue()) {
				listValue
						.add(new org.openscada.net.base.data.StringValue(entry));
			}
			value.getValues().put("listBasicValue", listValue);
		}

		value.getValues().put(
				"mapValue",
				org.openscada.core.net.MessageHelper.attributesToMap(bean
						.getMapValue()));

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final java.util.Map<String, org.openscada.core.Variant> entry : bean
					.getListMapValue()) {
				listValue.add(org.openscada.core.net.MessageHelper
						.attributesToMap(entry));
			}
			value.getValues().put("listMapValue", listValue);
		}

		if (bean.getFlagValue()) {
			value.getValues().put("flagValue",
					new org.openscada.net.base.data.VoidValue());
		}

		return value;
	}

}
