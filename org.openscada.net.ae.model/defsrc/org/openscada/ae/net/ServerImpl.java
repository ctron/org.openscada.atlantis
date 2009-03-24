package org.openscada.ae.net;

import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;

public abstract class ServerImpl implements Connection
{
    private final Messenger messenger;

    public ServerImpl ( final Messenger messenger )
    {
        this.messenger = messenger;

        this.messenger.setHandler ( ListQueriesRequest.COMMAND_CODE, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ListQueriesRequest.fromMessage ( message );
                final ListQueryResult result = listQueries ();

                final ListQueriesResponse response = new ListQueriesResponse ( result );
                final Message responseMessage = ListQueriesResponse.toMessage ( response, message );

                messenger.sendMessage ( responseMessage );
            }
        } );
    }

}
