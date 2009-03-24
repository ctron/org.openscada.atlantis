package org.openscada.ae.net;

public class Subscribe {

	public static final int COMMAND_CODE = 40;

	public Subscribe() {
	}

	public static org.openscada.net.base.data.Message toMessage(
			final Subscribe bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(Subscribe bean,
			final org.openscada.net.base.data.Message requestMessage) {
		org.openscada.net.base.data.Message message;

		if (requestMessage == null) {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE);
		} else {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE,
					requestMessage.getSequence());
		}

		message.setValues(org.openscada.ae.net.SubscriptionInformation
				.toValue(bean.getValue()));

		return message;
	}

	public static Subscribe fromMessage(
			final org.openscada.net.base.data.Message message) {
		Subscribe bean = new Subscribe();

		bean.setValue(org.openscada.ae.net.SubscriptionInformation
				.fromValue(message.getValues()));

		return bean;
	}

	public Subscribe(final org.openscada.ae.net.SubscriptionInformation value) {
		this.value = value;
	}

	private org.openscada.ae.net.SubscriptionInformation value;

	public org.openscada.ae.net.SubscriptionInformation getValue() {
		return this.value;
	}

	public void setValue(
			final org.openscada.ae.net.SubscriptionInformation value) {
		this.value = value;
	}

}
