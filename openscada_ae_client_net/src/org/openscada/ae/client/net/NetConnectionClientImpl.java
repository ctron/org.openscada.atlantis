package org.openscada.ae.client.net;

import org.openscada.ae.net.ConnectionClientImpl;
import org.openscada.ae.net.QueryUpdate;
import org.openscada.net.mina.Messenger;

public class NetConnectionClientImpl extends ConnectionClientImpl
{

    private final Connection connection;

    public NetConnectionClientImpl ( final Messenger messenger, final Connection connection )
    {
        super ( messenger );
        this.connection = connection;
    }

    public void handleQueryUpdateEvent ( final QueryUpdate eventData )
    {

    }

}
