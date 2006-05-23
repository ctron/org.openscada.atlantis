package org.openscada.da.server.net;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.ItemListListener;
import org.openscada.da.core.Session;
import org.openscada.da.core.WriteOperationListener;
import org.openscada.da.core.common.impl.WriteOperation;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.EnumEvent;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.io.Connection;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.lang.Holder;

public class ServerConnectionHandler extends ConnectionHandlerBase implements ItemChangeListener, ItemListListener {
    
    private static Logger _log = Logger.getLogger ( ServerConnectionHandler.class );
    
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
        
        getMessageProcessor().setHandler(Messages.CC_ENUM_SUBSCRIBE, new MessageListener(){
            
            public void messageReceived ( Connection connection, Message message )
            {
                enumSubscribe ( message );
            }});
        
        getMessageProcessor().setHandler(Messages.CC_ENUM_UNSUBSCRIBE, new MessageListener(){
            
            public void messageReceived ( Connection connection, Message message )
            {
                enumUnsubscribe ( message );
            }});
        
        getMessageProcessor().setHandler(Messages.CC_WRITE_OPERATION, new MessageListener(){
            
            public void messageReceived ( Connection connection, Message message )
            {
                performWrite ( message );
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
        _session.setListener((ItemListListener)this);
        _session.setListener((ItemChangeListener)this);
        
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
        boolean initial = message.getValues().containsKey("initial");
        
        _log.debug("Subscribe to " + itemName + " initial " + initial );
        
        try
        {
            _hive.registerForItem (_session, itemName, initial );
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
    
    private void enumSubscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"No session"));
            return;
        }
        
        try
        {
            _log.debug("Got request to enum subscription");
            _hive.registerItemList(_session);
        }
        catch ( InvalidSessionException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid session"));
        }
        
    }
    
    private void enumUnsubscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"No session"));
            return;
        }
        
        try
        {
            _hive.unregisterItemList(_session);
        }
        catch ( InvalidSessionException e )
        {
            getConnection().sendMessage(MessageCreator.createFailedMessage(message,"Invalid session"));
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
    
    public void valueChanged ( String name, Variant value, boolean initial )
    {
        getConnection().sendMessage(Messages.notifyValue(name, value, initial));
    }
    
    public void attributesChanged ( String name, Map<String, Variant> attributes, boolean initial )
    {
        getConnection().sendMessage(Messages.notifyAttributes(name, attributes, initial));
    }
    
    public void changed ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        _log.debug("Got enum change event from hive");
        getConnection().sendMessage ( EnumEvent.create ( added, removed, initial ) );
    }
    
    private void performWrite ( final Message message )
    {
        Holder<String> itemName = new Holder<String>();
        Holder<Variant> value = new Holder<Variant>();
        
        org.openscada.net.da.handler.WriteOperation.parse ( message, itemName, value );
        
        _log.debug("Writing to '" + itemName.value + "'" );
        
        try {
            _hive.startWrite ( _session, itemName.value, value.value, new WriteOperationListener(){
                
                public void success ()
                {
                    getConnection().sendMessage ( MessageCreator.createACK(message) ) ;
                }
                
                public void failure ( String errorMessage )
                {
                    getConnection().sendMessage ( MessageCreator.createFailedMessage ( message, errorMessage ) ) ;
                }} );
        }
        catch ( InvalidItemException e )
        {
            getConnection().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }
        catch ( InvalidSessionException e )
        {
            getConnection().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
    }
}
