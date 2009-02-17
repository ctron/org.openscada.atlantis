public abstract class ConnectionServerImpl implements Connection {

	private final org.openscada.net.base.MessageProcessor messageProcessor;

	public ConnectionServerImpl(
			org.openscada.net.base.MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;

		this.messageProcessor.setHandler(1202,
				new org.openscada.net.base.MessageListener() {
					public void messageReceived(
							org.openscada.net.io.net.Connection connection,
							org.openscada.net.base.data.Message message) {
						ConnectionServerImpl.this.handleWriteAttributes(
								connection, message);
					}
				});

		this.messageProcessor.setHandler(1203,
				new org.openscada.net.base.MessageListener() {
					public void messageReceived(
							org.openscada.net.io.net.Connection connection,
							org.openscada.net.base.data.Message message) {
						ConnectionServerImpl.this.handleWriteValue(connection,
								message);
					}
				});

	}

	private void handleWriteAttributes(
			org.openscada.net.io.net.Connection connection,
			org.openscada.net.base.data.Message message) {

		writeAttributes(WriteAttributes.fromMessage(message));

	}

	private void handleWriteValue(
			org.openscada.net.io.net.Connection connection,
			org.openscada.net.base.data.Message message) {

		FolderSubscribe result;

		result = writeValue(WriteValue.fromMessage(message));

		org.openscada.net.base.data.Message resultMessage = FolderSubscribe
				.toMessage(result, message);
		connection.sendMessage(resultMessage);

	}

}
