package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;

import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.DataItem;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class DataItemOutput extends BaseOutput implements OutputDefinition, Observer
{
    private Connection _connection = null;
    private DataItem _dataItem = null;
    
    public DataItemOutput ( Connection connection, String item, String name )
    {
        super ( name );
        _connection = connection;
        _dataItem = new DataItem ( item );
        
        // FIXME: for the moment this is ok
        subscribe ();
    }
    
    protected void subscribe ()
    {
        _dataItem.addObserver ( this );
        _dataItem.register ( _connection );
    }
    
    protected void unsubscribe ()
    {
        _dataItem.deleteObserver ( this );
        _dataItem.unregister ();
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.VARIANT );
    }

    public void update ( Observable o, Object arg )
    {
        fireEvent ( Type.VARIANT, _dataItem.getValue () );
    }

}
