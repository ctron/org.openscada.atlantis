package org.openscada.da.client.viewer.model.impl.items;

import java.net.URI;
import java.net.URISyntaxException;

import org.openscada.da.client.net.Connection;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class DataItemObject extends BaseDynamicObject
{
    protected static ConnectionPool _connectionPool = new ConnectionPool ();
    
    private DataItemOutput _output = null;
    private DataItemInput _input = null;
    
    private String _item = null;
    private String _connectionURI = null;
    
    public DataItemObject ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "connection" ) );
        addInput ( new PropertyInput ( this, "item" ) );
    }
    
    public void setItem ( String item )
    {
        _item = item;
        update ();
    }
    
    public void setConnection ( String connectionURI )
    {
        _connectionURI = connectionURI;
        update ();
    }
    
    protected void update ()
    {
        if ( _item != null && _connectionURI != null && _output == null )
        {
            try
            {
                _output = new DataItemOutput ( getConnection (), _item, "value" );
                addOutput ( _output );
                _input = new DataItemInput ( getConnection (), _item, "value" );
                addInput ( _input );
            }
            catch ( Exception e )
            {
                // FIXME: report that
            }
        }
    }
    
    protected Connection getConnection () throws URISyntaxException
    {
        return _connectionPool.getConnection ( new URI ( _connectionURI ) );
    }
}
