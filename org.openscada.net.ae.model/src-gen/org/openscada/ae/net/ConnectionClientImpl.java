package org.openscada.ae.net;

public abstract class ConnectionClientImpl implements ConnectionClient {

	protected org.openscada.net.mina.Messenger messenger;

	public ConnectionClientImpl(final org.openscada.net.mina.Messenger messenger) {
		this.messenger = messenger;

		this.messenger.setHandler(
				org.openscada.ae.net.QueryUpdateEvent.COMMAND_CODE,
				new org.openscada.net.base.MessageListener() {

					public void messageReceived(
							final org.openscada.net.base.data.Message message)
							throws Exception {

						org.openscada.ae.net.QueryUpdateEvent event = org.openscada.ae.net.QueryUpdateEvent
								.fromMessage(message);
						handleQueryUpdateEvent(event.getValue());

					}
				});

	}

	protected static class ListQueries$ResultImpl extends ListQueries$Result {
		protected synchronized void handleError(final Throwable error) {
			this.signalError(error);
		}

		protected synchronized void handleSuccess(
				final org.openscada.ae.net.ListQueryResult result) {
			this.signalResult(result);
		}

	}

	public ListQueries$Result startListQueries(final long timeout) {

		final ListQueries$ResultImpl result = new ListQueries$ResultImpl();

		final org.openscada.ae.net.ListQueriesRequest request = new org.openscada.ae.net.ListQueriesRequest();

		final org.openscada.net.base.data.Message message = org.openscada.ae.net.ListQueriesRequest
				.toMessage(request);

		this.messenger.sendMessage(message,
				new org.openscada.net.base.MessageStateListener() {

					public void messageReply(
							final org.openscada.net.base.data.Message message) {

						result
								.handleSuccess(org.openscada.ae.net.ListQueriesResponse
										.fromMessage(message).getValue());

					}

					public void messageTimedOut() {
						result.handleError(null);
					}
				}, timeout);

		return result;
	}

	public org.openscada.ae.net.ListQueryResult listQueries() {
		return listQueries(0L);
	}

	public org.openscada.ae.net.ListQueryResult listQueries(final long timeout) {
		final ListQueries$Result result = startListQueries(timeout);
		try {
			return result.waitForResult(timeout);
		} catch (final InterruptedException e) {
			return null;
		}
	}

	protected static class Subscribe$ResultImpl extends Subscribe$Result {
		protected synchronized void handleError(final Throwable error) {
			this.signalError(error);
		}

		protected synchronized void handleSuccess() {
			this.signalResult(null);
		}

	}

	public Subscribe$Result startSubscribe(
			final org.openscada.ae.net.SubscriptionInformation requestValue,
			final long timeout) {

		final Subscribe$ResultImpl result = new Subscribe$ResultImpl();

		final org.openscada.ae.net.Subscribe request = new org.openscada.ae.net.Subscribe(
				requestValue);

		final org.openscada.net.base.data.Message message = org.openscada.ae.net.Subscribe
				.toMessage(request);

		this.messenger.sendMessage(message,
				new org.openscada.net.base.MessageStateListener() {

					public void messageReply(
							final org.openscada.net.base.data.Message message) {

						switch (message.getCommandCode()) {
							case org.openscada.net.base.data.Message.CC_ACK :
								result.handleSuccess();
								break;
							default :
								result.handleError(new Exception(String.format(
										"Invalid reply to message: ", message
												.getCommandCode()))
										.fillInStackTrace());
								break;
						}

					}

					public void messageTimedOut() {
						result.handleError(null);
					}
				}, timeout);

		return result;
	}

	public void subscribe(org.openscada.ae.net.SubscriptionInformation request) {
		subscribe(request, 0L);
	}

	public void subscribe(org.openscada.ae.net.SubscriptionInformation request,
			final long timeout) {
		final Subscribe$Result result = startSubscribe(request, timeout);
		try {
			result.waitForResult(timeout);
		} catch (final InterruptedException e) {
		}
	}

	protected static class Unsubscribe$ResultImpl extends Unsubscribe$Result {
		protected synchronized void handleError(final Throwable error) {
			this.signalError(error);
		}

		protected synchronized void handleSuccess() {
			this.signalResult(null);
		}

	}

	public Unsubscribe$Result startUnsubscribe(
			final org.openscada.ae.net.SubscriptionInformation requestValue,
			final long timeout) {

		final Unsubscribe$ResultImpl result = new Unsubscribe$ResultImpl();

		final org.openscada.ae.net.Unsubscribe request = new org.openscada.ae.net.Unsubscribe(
				requestValue);

		final org.openscada.net.base.data.Message message = org.openscada.ae.net.Unsubscribe
				.toMessage(request);

		this.messenger.sendMessage(message,
				new org.openscada.net.base.MessageStateListener() {

					public void messageReply(
							final org.openscada.net.base.data.Message message) {

						switch (message.getCommandCode()) {
							case org.openscada.net.base.data.Message.CC_ACK :
								result.handleSuccess();
								break;
							default :
								result.handleError(new Exception(String.format(
										"Invalid reply to message: ", message
												.getCommandCode()))
										.fillInStackTrace());
								break;
						}

					}

					public void messageTimedOut() {
						result.handleError(null);
					}
				}, timeout);

		return result;
	}

	public void unsubscribe(org.openscada.ae.net.SubscriptionInformation request) {
		unsubscribe(request, 0L);
	}

	public void unsubscribe(
			org.openscada.ae.net.SubscriptionInformation request,
			final long timeout) {
		final Unsubscribe$Result result = startUnsubscribe(request, timeout);
		try {
			result.waitForResult(timeout);
		} catch (final InterruptedException e) {
		}
	}

	protected static class CreateSession$ResultImpl
			extends
				CreateSession$Result {
		protected synchronized void handleError(final Throwable error) {
			this.signalError(error);
		}

		protected synchronized void handleSuccess() {
			this.signalResult(null);
		}

	}

	public CreateSession$Result startCreateSession(final long timeout) {

		final CreateSession$ResultImpl result = new CreateSession$ResultImpl();

		final org.openscada.core.CreateSessionRequest request = new org.openscada.core.CreateSessionRequest();

		final org.openscada.net.base.data.Message message = org.openscada.core.CreateSessionRequest
				.toMessage(request);

		this.messenger.sendMessage(message,
				new org.openscada.net.base.MessageStateListener() {

					public void messageReply(
							final org.openscada.net.base.data.Message message) {

						switch (message.getCommandCode()) {
							case org.openscada.net.base.data.Message.CC_ACK :
								result.handleSuccess();
								break;
							default :
								result.handleError(new Exception(String.format(
										"Invalid reply to message: ", message
												.getCommandCode()))
										.fillInStackTrace());
								break;
						}

					}

					public void messageTimedOut() {
						result.handleError(null);
					}
				}, timeout);

		return result;
	}

	public void createSession() {
		createSession(0L);
	}

	public void createSession(final long timeout) {
		final CreateSession$Result result = startCreateSession(timeout);
		try {
			result.waitForResult(timeout);
		} catch (final InterruptedException e) {
		}
	}

	protected static class CloseSession$ResultImpl extends CloseSession$Result {
		protected synchronized void handleError(final Throwable error) {
			this.signalError(error);
		}

		protected synchronized void handleSuccess() {
			this.signalResult(null);
		}

	}

	public CloseSession$Result startCloseSession(final long timeout) {

		final CloseSession$ResultImpl result = new CloseSession$ResultImpl();

		final org.openscada.core.CloseSessionRequest request = new org.openscada.core.CloseSessionRequest();

		final org.openscada.net.base.data.Message message = org.openscada.core.CloseSessionRequest
				.toMessage(request);

		this.messenger.sendMessage(message,
				new org.openscada.net.base.MessageStateListener() {

					public void messageReply(
							final org.openscada.net.base.data.Message message) {

						switch (message.getCommandCode()) {
							case org.openscada.net.base.data.Message.CC_ACK :
								result.handleSuccess();
								break;
							default :
								result.handleError(new Exception(String.format(
										"Invalid reply to message: ", message
												.getCommandCode()))
										.fillInStackTrace());
								break;
						}

					}

					public void messageTimedOut() {
						result.handleError(null);
					}
				}, timeout);

		return result;
	}

	public void closeSession() {
		closeSession(0L);
	}

	public void closeSession(final long timeout) {
		final CloseSession$Result result = startCloseSession(timeout);
		try {
			result.waitForResult(timeout);
		} catch (final InterruptedException e) {
		}
	}

}
