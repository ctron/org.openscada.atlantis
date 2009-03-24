package org.openscada.ae.net;

public interface ConnectionClient extends Connection {

	public static abstract class ListQueries$Result
			extends
				org.openscada.utils.exec.Result<org.openscada.ae.net.ListQueryResult> {
	}

	public abstract ListQueries$Result startListQueries(long timeout);

	public abstract org.openscada.ae.net.ListQueryResult listQueries(
			long timeout);

	public static abstract class Subscribe$Result
			extends
				org.openscada.utils.exec.Result<Object> {
	}

	public abstract Subscribe$Result startSubscribe(
			org.openscada.ae.net.SubscriptionInformation request, long timeout);

	public static abstract class Unsubscribe$Result
			extends
				org.openscada.utils.exec.Result<Object> {
	}

	public abstract Unsubscribe$Result startUnsubscribe(
			org.openscada.ae.net.SubscriptionInformation request, long timeout);

	public static abstract class CreateSession$Result
			extends
				org.openscada.utils.exec.Result<Object> {
	}

	public abstract CreateSession$Result startCreateSession(long timeout);

	public static abstract class CloseSession$Result
			extends
				org.openscada.utils.exec.Result<Object> {
	}

	public abstract CloseSession$Result startCloseSession(long timeout);

	public abstract void handleQueryUpdateEvent(
			final org.openscada.ae.net.QueryUpdate eventData);

}
