public class WriteAttributes {

	public static final long MESSAGE_CODE = 1202L;

	public WriteAttributes() {
	}

	public WriteAttributes(

	final String itemId,
			final java.util.Map<String, org.openscada.core.Variant> attributes

	) {

		this.itemId = itemId;

		this.attributes = attributes;

	}

	private String itemId;

	public void setItemId(final String itemId) {
		this.itemId = itemId;
	}

	public String getItemId() {
		return this.itemId;
	}

	private java.util.Map<String, org.openscada.core.Variant> attributes;

	public void setAttributes(
			final java.util.Map<String, org.openscada.core.Variant> attributes) {
		this.attributes = attributes;
	}

	public java.util.Map<String, org.openscada.core.Variant> getAttributes() {
		return this.attributes;
	}

	public static WriteAttributes fromMessage(
			final org.openscada.net.base.data.Message message) {
		WriteAttributes bean = new WriteAttributes();

		{
			org.openscada.net.base.data.Value value = message.getValues().get(
					"item-id");
			if (value != null
					&& value instanceof org.openscada.net.base.data.StringValue) {
				bean
						.setItemId(((org.openscada.net.base.data.StringValue) value)
								.getValue());
			}
		}

		{
			org.openscada.net.base.data.Value value = message.getValues().get(
					"attributes");
			if (value instanceof org.openscada.net.base.data.MapValue) {
				bean
						.setAttributes(org.openscada.core.net.MessageHelper
								.mapToAttributes((org.openscada.net.base.data.MapValue) value));
			}
		}

		return bean;
	}

	public static org.openscada.net.base.data.Message toMessage(
			final WriteAttributes bean,
			final org.openscada.net.base.data.Message requestMessage) {
		final org.openscada.net.base.data.Message message;
		if (requestMessage != null)
			message = new org.openscada.net.base.data.Message(1202,
					requestMessage.getSequence());
		else
			message = new org.openscada.net.base.data.Message(1202);

		message.getValues().put("item-id",
				new org.openscada.net.base.data.StringValue(bean.getItemId()));

		message.getValues().put(
				"attributes",
				org.openscada.core.net.MessageHelper.attributesToMap(bean
						.getAttributes()));

		return message;
	}

}
