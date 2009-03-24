package org.openscada.core;

public class CreateSessionRequest {

	public static final int COMMAND_CODE = 131072;

	public CreateSessionRequest() {
	}

	public static org.openscada.net.base.data.Message toMessage() {
		return toMessage((CreateSessionRequest) null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final org.openscada.net.base.data.Message requestMessage) {
		return toMessage((CreateSessionRequest) null, requestMessage);
	}

	public static org.openscada.net.base.data.Message toMessage(
			final CreateSessionRequest bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			CreateSessionRequest bean,
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

	public static CreateSessionRequest fromMessage(
			final org.openscada.net.base.data.Message message) {
		CreateSessionRequest bean = new CreateSessionRequest();

		return bean;
	}

}
