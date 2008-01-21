package org.openscada.net.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.net.Connection;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningState;

public class LongRunningController implements MessageListener
{
    private static Logger _log = Logger.getLogger ( LongRunningController.class );

    private Set<Integer> _commandCodes = new HashSet<Integer> ();
    private ConnectionHandlerBase _connectionHandler = null;

    private Map<Long, LongRunningOperation> _opMap = new HashMap<Long, LongRunningOperation> ();

    public LongRunningController ( ConnectionHandlerBase connectionHandler, int commandCode )
    {
        _connectionHandler = connectionHandler;
        _commandCodes.add ( commandCode );
    }

    public LongRunningController ( ConnectionHandlerBase connectionHandler, Set<Integer> commandCodes )
    {
        _connectionHandler = connectionHandler;
        _commandCodes.addAll ( commandCodes );
    }

    public LongRunningController ( ConnectionHandlerBase connectionHandler, Integer... commandCodes )
    {
        _connectionHandler = connectionHandler;
        _commandCodes.addAll ( Arrays.asList ( commandCodes ) );
    }

    public void register ( MessageProcessor processor )
    {
        for ( Integer commandCode : _commandCodes )
        {
            processor.setHandler ( commandCode, this );
        }
    }

    public void unregister ( MessageProcessor processor )
    {
        for ( Integer commandCode : _commandCodes )
        {
            processor.unsetHandler ( commandCode );
        }
    }

    synchronized public LongRunningOperation start ( Message message, LongRunningListener listener )
    {
        if ( message == null )
        {
            return null;
        }

        final LongRunningOperation op = new LongRunningOperation ( this, listener );

        Connection connection = _connectionHandler.getConnection ();
        if ( connection == null )
        {
            op.fail ( new NoConnectionException () );
            return op;
        }

        _connectionHandler.getConnection ().sendMessage ( message, new MessageStateListener () {

            public void messageReply ( Message message )
            {
                if ( message.getValues ().containsKey ( "id" ) )
                {
                    if ( message.getValues ().get ( "id" ) instanceof LongValue )
                    {
                        long id = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
                        op.granted ( id );
                        assignOperation ( id, op );
                        return;
                    }
                }
                else if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
                {
                    String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
                    op.fail ( new InvalidMessageReplyException ( errorInfo ).fillInStackTrace () );
                    return;
                }
                // else
                op.fail ( new InvalidMessageReplyException ( "Message did not contain 'id' field" ).fillInStackTrace () );
            }

            public void messageTimedOut ()
            {
                op.fail ( new MessageTimeoutException ().fillInStackTrace () );
            }
        } );

        if ( listener != null )
        {
            listener.stateChanged ( op, LongRunningState.REQUESTED, null );
        }

        return op;
    }

    private synchronized void assignOperation ( long id, LongRunningOperation op )
    {
        _opMap.put ( id, op );
    }

    public void messageReceived ( Connection connection, Message message )
    {
        long id = 0;

        if ( message.getValues ().containsKey ( "id" ) )
            if ( message.getValues ().get ( "id" ) instanceof LongValue )
                id = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        _log.debug ( String.format ( "Received long-op reply with id %d", id ) );

        if ( id != 0 )
        {
            LongRunningOperation op = null;
            synchronized ( _opMap )
            {
                op = _opMap.get ( id );
                _opMap.remove ( id );
            }

            if ( op != null )
            {
                op.result ( message );
            }
            else
            {
                _log.warn ( "Received long-op message for unregistered operation" );
            }
        }
    }
}
