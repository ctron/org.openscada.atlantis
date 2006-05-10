package org.openscada.da.server.net;

import java.util.Map;
import java.util.Properties;

import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.Session;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.io.Connection;
import org.openscada.net.utils.MessageCreator;

public class ServerConnectionHandler extends ConnectionHandlerBase implements ItemChangeListener {

	private Hive _hive = null;
	private Session _session = null;
	
	public ServerConnectionHandler(Hive hive)
    {
		super();
		
		_hive = hive;
		
		getMessageProcessor().setHandler(Messages.CC_CREATE_SESSION, new MessageListener(){

			public void messageReceived(Connection connection, Message message) {
				createSession ( message );
			}});
		
		getMessageProcessor().setHandler(Messages.CC_CLOSE_SESSION, new MessageListener(){

			public void messageReceived(Connection connection, Message message) {
				closeSession ();
			}});
        
        getMessageProcessor().setHandler(Messages.CC_SUBSCRIBE_ITEM, new MessageListener(){

            public void messageReceived(Connection connection, Message message) {
                subscribe ( message );
            }});
        
        getMessageProcessor().setHandler(Messages.CC_UNSUBSCRIBE_ITEM, new MessageListener(){

            public void messageReceived(Connection connection, Message message) {
                unsubscribe ( message );
            }});
		
	}
	
	private void createSession ( Message message )
	{
		// if session exists this is an error
		if ( _session != null )
		{
			getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Session already exists"));
			return;
		}
		
		Properties props = new Properties();
		for ( Map.Entry<String,Value> entry : message.getValues().entrySet() )
		{
			props.put ( entry.getKey(), entry.getValue().toString() );
		}
		
		_session = _hive.createSession(props);
        _session.setListener(this);
		
		if ( _session == null )
		{
			getConnection().sendMessage(MessageCreator.createFailedMessage(message,"unable to create session"));
			return;
		}
		getConnection().sendMessage(MessageCreator.createACK(message));
	}
	
	private void disposeSession ()
	{
		// if session does not exists, silently ignore it
		if ( _session != null )
		{
			try {
				_hive.closeSession(_session);
			} catch (InvalidSessionException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void closeSession ()
	{
		disposeSession ();
		// also shut down communcation connection
		getConnection().close();
	}
    
    private void subscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"No session"));
            return;
        }
        
        String itemName = message.getValues().get("item-name").toString();
        
        try
        {
            _hive.registerForItem(_session, itemName);
        }
        catch ( InvalidSessionException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid session"));
        }
        catch ( InvalidItemException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid item"));
        }
        
    }
    
    private void unsubscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"No session"));
            return;
        }
        
        String itemName = message.getValues().get("item-name").toString();
        
        try
        {
            _hive.unregisterForItem(_session, itemName);
        }
        catch ( InvalidSessionException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid session"));
        }
        catch ( InvalidItemException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid item"));
        }
    }
	
	private void cleanUp ()
	{
        disposeSession();
	}
	
	@Override
	public void closed()
    {
		cleanUp ();
		super.closed();
	}

    public void valueChanged ( String name, Variant value )
    {
        getConnection().sendMessage(Messages.notifyValue(name, value));
    }

    public void attributesChanged ( String name, Map<String, Variant> attributes )
    {
        getConnection().sendMessage(Messages.notifyAttributes(name, attributes));
    }
	
}
