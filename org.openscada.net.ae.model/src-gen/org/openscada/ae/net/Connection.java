package org.openscada.ae.net;

public interface Connection {

	public abstract org.openscada.ae.net.ListQueryResult listQueries();

	public abstract void subscribe(
			org.openscada.ae.net.SubscriptionInformation request);

	public abstract void unsubscribe(
			org.openscada.ae.net.SubscriptionInformation request);

	public abstract void createSession();

	public abstract void closeSession();

}
