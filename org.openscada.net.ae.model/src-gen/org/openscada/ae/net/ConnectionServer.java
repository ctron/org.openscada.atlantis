package org.openscada.ae.net;

public interface ConnectionServer extends Connection {

	public abstract void queryUpdateEvent(
			org.openscada.ae.net.QueryUpdate eventData);

}
