package org.openscada.net.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ConnectionListener {
	public void closed ();
	public void read ( ByteBuffer buffer );
	public void written ();
	
	public void connected ();
	public void connectionFailed ( IOException e );
}
