package org.openscada.ae.net;

public abstract class ConnectionServerImpl implements ConnectionServer {

	protected org.openscada.net.mina.Messenger messenger;

	public ConnectionServerImpl(final org.openscada.net.mina.Messenger messenger) {
		this.messenger = messenger;

		this.messenger.setHandler(
				org.openscada.ae.net.ListQueriesRequest.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.net.base.data.Message responseMessage;

						final org.openscada.ae.net.ListQueryResult result = listQueries();

						final org.openscada.ae.net.ListQueriesResponse response = new org.openscada.ae.net.ListQueriesResponse(
								result);
						responseMessage = org.openscada.ae.net.ListQueriesResponse
								.toMessage(response, message);

						messenger.sendMessage(responseMessage);
					}
				});

		this.messenger.setHandler(org.openscada.ae.net.Subscribe.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.ae.net.Subscribe request = org.openscada.ae.net.Subscribe
								.fromMessage(message);

						org.openscada.net.base.data.Message responseMessage;

						try {
							subscribe(request.getValue());
							responseMessage = org.openscada.net.utils.MessageCreator
									.createACK(message);
						} catch (Throwable e) {
							responseMessage = org.openscada.net.utils.MessageCreator
									.createFailedMessage(message, e);
						}

						messenger.sendMessage(responseMessage);
					}
				});

		this.messenger.setHandler(
				org.openscada.ae.net.Unsubscribe.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.ae.net.Unsubscribe request = org.openscada.ae.net.Unsubscribe
								.fromMessage(message);

						org.openscada.net.base.data.Message responseMessage;

						try {
							unsubscribe(request.getValue());
							responseMessage = org.openscada.net.utils.MessageCreator
									.createACK(message);
						} catch (Throwable e) {
							responseMessage = org.openscada.net.utils.MessageCreator
									.createFailedMessage(message, e);
						}

						messenger.sendMessage(responseMessage);
					}
				});

		this.messenger.setHandler(
				org.openscada.core.CreateSessionRequest.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.net.base.data.Message responseMessage;

						try {
							createSession();
							responseMessage = org.openscada.net.utils.MessageCreator
									.createACK(message);
						} catch (Throwable e) {
							responseMessage = org.openscada.net.utils.MessageCreator
									.createFailedMessage(message, e);
						}

						messenger.sendMessage(responseMessage);
					}
				});

		this.messenger.setHandler(
				org.openscada.core.CloseSessionRequest.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.net.base.data.Message responseMessage;

						try {
							closeSession();
							responseMessage = org.openscada.net.utils.MessageCreator
									.createACK(message);
						} catch (Throwable e) {
							responseMessage = org.openscada.net.utils.MessageCreator
									.createFailedMessage(message, e);
						}

						messenger.sendMessage(responseMessage);
					}
				});

	}

	public void queryUpdateEvent(org.openscada.ae.net.QueryUpdate eventData) {
		org.openscada.ae.net.QueryUpdateEvent event = new org.openscada.ae.net.QueryUpdateEvent(
				eventData);

		final org.openscada.net.base.data.Message message;
		message = org.openscada.ae.net.QueryUpdateEvent.toMessage(event);
		messenger.sendMessage(message);
	}

}
