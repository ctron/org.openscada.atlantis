package org.openscada.da.server.net;

import org.apache.log4j.Logger;
import org.openscada.da.core.CancellationNotSupportedException;
import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;
import org.openscada.da.core.WriteOperationListener;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.da.handler.Messages;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.Operation;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class WriteOperation implements Operation
{
    private static Logger _log = Logger.getLogger ( WriteOperation.class );
    
    private Hive _hive = null;
    private Session _session = null;
    private ServerConnectionHandler _server = null;
    private String _itemName = null;
    private Variant _value = null;
    
    private Long _hiveID = null;
    
    public WriteOperation ( Hive hive, Session session, ServerConnectionHandler server, String itemName, Variant value )
    {
        _hive = hive;
        _session = session;
        _server = server;
        _itemName = itemName;
        _value = value;
    }
    
    public void cancel () throws CancelNotSupportedException
    {
        try
        {
            _hive.cancelOperation ( _session, _hiveID );
        }
        catch ( CancellationNotSupportedException e )
        {
            throw new CancelNotSupportedException ();
        }
        catch ( InvalidSessionException e )
        {
            // this should never happen
        }
    }

    public void start ( final Handle handle )
    {
        _log.debug ( "Starting write operation" );
        
        try
        {
            _hiveID = _hive.startWrite ( _session, _itemName, _value, new WriteOperationListener () {

                public void failure ( Throwable throwable )
                {
                    handle.completed ();
                    sendError ( handle, throwable );
                }

                public void success ()
                {
                    handle.completed ();
                    sendComplete ( handle );
                }} );
            
            _hive.thawOperation ( _session, _hiveID );
        }
        catch ( InvalidSessionException e )
        {
            sendError ( handle, e );
        }
        catch ( InvalidItemException e )
        {
            sendError ( handle, e );
        }
    }
    
    private void sendError ( Handle handle, Throwable e )
    {
        _log.warn ( "Write operation completed with error", e );
        
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( e.getMessage () ) );
        replyMessage.getValues ().put ( "id", new LongValue ( handle.getId () ) );

        _server.getConnection().sendMessage ( replyMessage );
    }
    
    private void sendComplete ( Handle handle )
    {
        _log.info ( "Write operation completed");
        
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( "id", new LongValue ( handle.getId () ) );
        _server.getConnection().sendMessage ( replyMessage );
    }

}
