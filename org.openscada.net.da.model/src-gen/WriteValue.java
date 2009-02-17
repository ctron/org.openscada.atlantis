public class WriteValue {

	public static final long MESSAGE_CODE = 1203L;

	public WriteValue() {
	}

	public WriteValue(

	final String itemId, final org.openscada.core.Variant value

	) {

		this.itemId = itemId;

		this.value = value;

	}

	private String itemId;

	public void setItemId(final String itemId) {
		this.itemId = itemId;
	}

	public String getItemId() {
		return this.itemId;
	}

	private org.openscada.core.Variant value;

	public void setValue(final org.openscada.core.Variant value) {
		this.value = value;
	}

	public org.openscada.core.Variant getValue() {
		return this.value;
	}

	public static WriteValue fromMessage(
			final org.openscada.net.base.data.Message message) {
		WriteValue bean = new WriteValue();

		{
			org.openscada.net.base.data.Value value = message.getValues().get(
					"itemId");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean
						.setItemId(((org.openscada.net.base.data.StringValue) value)
								.getValue());
			}
		}

		{
			org.openscada.net.base.data.Value value = message.getValues().get(
					"value");
			bean.setValue(org.openscada.core.net.MessageHelper.valueToVariant(
					value, new org.openscada.core.Variant()));
		}

		return bean;
	}

	public static org.openscada.net.base.data.Message toMessage(
			final WriteValue bean,
			final org.openscada.net.base.data.Message requestMessage) {
		final org.openscada.net.base.data.Message message;
		if (requestMessage != null)
			message = new org.openscada.net.base.data.Message(1203,
					requestMessage.getSequence());
		else
			message = new org.openscada.net.base.data.Message(1203);

		message.getValues().put("itemId",
				new org.openscada.net.base.data.StringValue(bean.getItemId()));

		message.getValues().put(
				"value",
				org.openscada.core.net.MessageHelper.variantToValue(bean
						.getValue()));

		return message;
	}

}
