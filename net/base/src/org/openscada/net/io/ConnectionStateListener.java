package org.openscada.net.io;

public interface ConnectionStateListener {
	public void closed ();
	public void opened ();
}
