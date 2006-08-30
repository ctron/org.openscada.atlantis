package org.openscada.ae.net;

import java.util.Map;
import java.util.Properties;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class CreateSessionMessage
{
    private Properties _properties = new Properties ();

    public Properties getProperties ()
    {
        return _properties;
    }

    public void setProperties ( Properties properties )
    {
        _properties = properties;
    }
    
    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_CREATE_SESSION );
        for ( Map.Entry<Object,Object> entry : _properties.entrySet () )
        {
             message.getValues ().put ( entry.getKey ().toString (), new StringValue ( entry.getValue ().toString () ) );
        }
        return message;
    }
    
    public static CreateSessionMessage fromMessage ( Message message )
    {
        CreateSessionMessage createSessionMessage = new CreateSessionMessage ();
        for ( Map.Entry<String, Value> entry : message.getValues ().getValues ().entrySet () )
        {
            createSessionMessage.getProperties ().put ( entry.getKey (), entry.getValue ().toString () );
        }
        return createSessionMessage;
    }
}
