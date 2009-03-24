package org.openscada.ae.net;

public class TestMessage {

	public static final int COMMAND_CODE = 1;

	public TestMessage() {
	}

	public static org.openscada.net.base.data.Message toMessage(
			final TestMessage bean) {
		return toMessage(bean, null);
	}

	public static org.openscada.net.base.data.Message toMessage(
			TestMessage bean,
			final org.openscada.net.base.data.Message requestMessage) {
		org.openscada.net.base.data.Message message;

		if (requestMessage == null) {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE);
		} else {
			message = new org.openscada.net.base.data.Message(COMMAND_CODE,
					requestMessage.getSequence());
		}

		message.setValues(org.openscada.ae.net.TestEntity.toValue(bean
				.getValue()));

		return message;
	}

	public static TestMessage fromMessage(
			final org.openscada.net.base.data.Message message) {
		TestMessage bean = new TestMessage();

		bean.setValue(org.openscada.ae.net.TestEntity.fromValue(message
				.getValues()));

		return bean;
	}

	public TestMessage(final org.openscada.ae.net.TestEntity value) {
		this.value = value;
	}

	private org.openscada.ae.net.TestEntity value;

	public org.openscada.ae.net.TestEntity getValue() {
		return this.value;
	}

	public void setValue(final org.openscada.ae.net.TestEntity value) {
		this.value = value;
	}

}
