package org.openscada.ae.net;

public class QueryUpdateEvent {

	public static final int COMMAND_CODE = 60;

	public QueryUpdateEvent() {
	}

	public static org.openscada.net.base.data.Message toMessage(
			final QueryUpdateEvent bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			QueryUpdateEvent bean,
			final org.openscada.net.base.data.Message requestMessage) {
		org.openscada.net.base.data.Message message;

		if (requestMessage == null) {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE);
		} else {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE,
					requestMessage.getSequence());
		}

		message.setValues(org.openscada.ae.net.QueryUpdate.toValue(bean
				.getValue()));

		return message;
	}

	public static QueryUpdateEvent fromMessage(
			final org.openscada.net.base.data.Message message) {
		QueryUpdateEvent bean = new QueryUpdateEvent();

		bean.setValue(org.openscada.ae.net.QueryUpdate.fromValue(message
				.getValues()));

		return bean;
	}

	public QueryUpdateEvent(final org.openscada.ae.net.QueryUpdate value) {
		this.value = value;
	}

	private org.openscada.ae.net.QueryUpdate value;

	public org.openscada.ae.net.QueryUpdate getValue() {
		return this.value;
	}

	public void setValue(final org.openscada.ae.net.QueryUpdate value) {
		this.value = value;
	}

}
