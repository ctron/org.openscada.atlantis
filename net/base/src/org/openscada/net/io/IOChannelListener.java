package org.openscada.net.io;

public interface IOChannelListener {
	public void handleConnect ();
	public void handleRead ();
	public void handleWrite ();
	public void handleAccept ();
}
