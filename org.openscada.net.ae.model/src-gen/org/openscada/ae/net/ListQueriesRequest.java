package org.openscada.ae.net;

public class ListQueriesRequest {

	public static final int COMMAND_CODE = 2;

	public ListQueriesRequest() {
	}

	public static org.openscada.net.base.data.Message toMessage() {
		return toMessage((ListQueriesRequest) null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final org.openscada.net.base.data.Message requestMessage) {
		return toMessage((ListQueriesRequest) null, requestMessage);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final ListQueriesRequest bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			ListQueriesRequest bean,
			final org.openscada.net.base.data.Message requestMessage) {
		org.openscada.net.base.data.Message message;

		if (requestMessage == null) {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE);
		} else {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE,
					requestMessage.getSequence());
		}

		return message;
	}

	public static ListQueriesRequest fromMessage(
			final org.openscada.net.base.data.Message message) {
		ListQueriesRequest bean = new ListQueriesRequest();

		return bean;
	}

}
