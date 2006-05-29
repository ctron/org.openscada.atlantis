package org.openscada.net.io;


public interface ConnectionStateListener
{
	public void closed ( Exception e );
	public void opened ();
}
