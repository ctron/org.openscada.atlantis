package org.openscada.ae.net;

public class ListQueriesResponse {

	public static final int COMMAND_CODE = 0;

	public ListQueriesResponse() {
	}

	public static org.openscada.net.base.data.Message toMessage(
			final ListQueriesResponse bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			ListQueriesResponse bean,
			final org.openscada.net.base.data.Message requestMessage) {
		org.openscada.net.base.data.Message message;

		if (requestMessage == null) {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE);
		} else {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE,
					requestMessage.getSequence());
		}

		message.setValues(org.openscada.ae.net.ListQueryResult.toValue(bean
				.getValue()));

		return message;
	}

	public static ListQueriesResponse fromMessage(
			final org.openscada.net.base.data.Message message) {
		ListQueriesResponse bean = new ListQueriesResponse();

		bean.setValue(org.openscada.ae.net.ListQueryResult.fromValue(message
				.getValues()));

		return bean;
	}

	public ListQueriesResponse(final org.openscada.ae.net.ListQueryResult value) {
		this.value = value;
	}

	private org.openscada.ae.net.ListQueryResult value;

	public org.openscada.ae.net.ListQueryResult getValue() {
		return this.value;
	}

	public void setValue(final org.openscada.ae.net.ListQueryResult value) {
		this.value = value;
	}

}
