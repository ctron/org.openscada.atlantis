package org.openscada.da.client.viewer.model.impl.items;

import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.DataItem;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.impl.BaseOutput;

public class DataItemOutput extends BaseOutput implements OutputDefinition, Observer
{
    private static Logger _log = Logger.getLogger ( DataItemOutput.class );
    
    private Connection _connection = null;
    private DataItem _dataItem = null;
    private boolean _subscribed = false;
    
    public DataItemOutput ( Connection connection, String item, String name )
    {
        super ( name );
        _connection = connection;
        _dataItem = new DataItem ( item );
    }
    
    protected synchronized void subscribe ()
    {
        if ( _subscribed )
            return;
        
        _log.debug ( String.format ( "Subscribing to item" ) );
        
        _dataItem.addObserver ( this );
        _dataItem.register ( _connection );
        _subscribed = true;
    }
    
    protected synchronized void unsubscribe ()
    {
        if ( !_subscribed )
            return;
        
        _log.debug ( String.format ( "Un-Subscribing from item" ) );
        
        _dataItem.deleteObserver ( this );
        _dataItem.unregister ();
        _subscribed = false;
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.VARIANT );
    }

    public void update ( Observable o, Object arg )
    {
        fireEvent ( Type.VARIANT, _dataItem.getValue () );
    }

    @Override
    public synchronized void addListener ( OutputListener listener )
    {
        super.addListener ( listener );
        if ( hasListeners () )
        {
            subscribe ();
        }
    }
    
    @Override
    public synchronized void removeListener ( OutputListener listener )
    {
        super.removeListener ( listener );
        if ( !hasListeners () )
        {
            unsubscribe ();
        }
    }
}
