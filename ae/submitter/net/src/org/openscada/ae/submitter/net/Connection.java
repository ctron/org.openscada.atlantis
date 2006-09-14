package org.openscada.ae.submitter.net;

import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Submission;
import org.openscada.ae.net.SubmitEventMessage;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;

public class Connection extends ConnectionBase implements Submission
{

    public Connection ( ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );
    }

    @Override
    protected void onConnectionBound ()
    {
    }

    @Override
    protected void onConnectionClosed ()
    {
    }

    @Override
    protected void onConnectionEstablished ()
    {
        // we don't need no binding for submitting an event
        setState ( State.BOUND, null );
    }

    public void submitEvent ( Properties properties, Event event ) throws Exception
    {
        SubmitEventMessage message = new SubmitEventMessage ();
        message.setEvent ( event );
        message.setProperties ( properties );
        
        final SubmissionResult result = new SubmissionResult ();
        
        synchronized ( result )
        {
            sendMessage ( message.toMessage (), new MessageStateListener () {

                public void messageReply ( Message message )
                {
                    if ( message.getCommandCode () == Message.CC_ACK )
                    {
                        result.complete ();
                    }
                    else
                    {
                        result.fail ( new Exception ( "received invalid response" ) );
                    }
                }

                public void messageTimedOut ()
                {
                    result.fail ( (Exception)new Exception ( "Message timed out").fillInStackTrace () );

                }}, Integer.getInteger ( "openscada.ae.message.timeout", 10 * 1000 ) );
            
            result.wait ();
        }
        
        if ( !result.isSuccess () )
            throw result.getError ();
    }

    
}
