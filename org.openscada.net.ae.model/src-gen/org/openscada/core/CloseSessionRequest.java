package org.openscada.core;

public class CloseSessionRequest {

	public static final int COMMAND_CODE = 131073;

	public CloseSessionRequest() {
	}

	public static org.openscada.net.base.data.Message toMessage() {
		return toMessage((CloseSessionRequest) null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final org.openscada.net.base.data.Message requestMessage) {
		return toMessage((CloseSessionRequest) null, requestMessage);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final CloseSessionRequest bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			CloseSessionRequest bean,
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

	public static CloseSessionRequest fromMessage(
			final org.openscada.net.base.data.Message message) {
		CloseSessionRequest bean = new CloseSessionRequest();

		return bean;
	}

}
