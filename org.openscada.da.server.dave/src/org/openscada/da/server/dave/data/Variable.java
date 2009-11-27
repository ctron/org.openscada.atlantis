package org.openscada.da.server.dave.data;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.osgi.framework.BundleContext;

public interface Variable
{
    public void start ( String parentName, BundleContext context, final DaveDevice device, DaveRequestBlock block, int offset );

    public void stop ( BundleContext context );

    public void handleError ( int errorCode );

    public void handleFailure ( Throwable e );

    public void handleData ( IoBuffer data );

    public void handleDisconnect ();
}
