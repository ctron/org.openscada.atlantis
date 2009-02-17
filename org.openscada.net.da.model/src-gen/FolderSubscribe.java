public class FolderSubscribe {

	public static final long MESSAGE_CODE = 1204L;

	public FolderSubscribe() {
	}

	public FolderSubscribe(

	final java.util.Collection<String> location, final boolean flag

	) {

		this.location = location;

		this.flag = flag;

	}

	private java.util.Collection<String> location;

	public void setLocation(final java.util.Collection<String> location) {
		this.location = location;
	}

	public java.util.Collection<String> getLocation() {
		return this.location;
	}

	private boolean flag;

	public void setFlag(final boolean flag) {
		this.flag = flag;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public static FolderSubscribe fromMessage(
			final org.openscada.net.base.data.Message message) {
		FolderSubscribe bean = new FolderSubscribe();

		{
			org.openscada.net.base.data.Value value = message.getValues().get(
					"location");
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

				bean.setLocation(list);
			}
		}

		bean.setFlag(message.getValues().containsKey("flag"));

		return bean;
	}

	public static org.openscada.net.base.data.Message toMessage(
			final FolderSubscribe bean,
			final org.openscada.net.base.data.Message requestMessage) {
		final org.openscada.net.base.data.Message message;
		if (requestMessage != null)
			message = new org.openscada.net.base.data.Message(1204,
					requestMessage.getSequence());
		else
			message = new org.openscada.net.base.data.Message(1204);

		{
			org.openscada.net.base.data.ListValue listValue = new org.openscada.net.base.data.ListValue();
			for (final String entry : bean.getLocation()) {
				listValue
						.add(new org.openscada.net.base.data.StringValue(entry));
			}
			message.getValues().put("location", listValue);
		}

		if (bean.getFlag()) {
			message.getValues().put("flag",
					new org.openscada.net.base.data.VoidValue());
		}

		return message;
	}

}
