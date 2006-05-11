package org.openscada.da.client.net;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.data.AttributesHelper;
import org.openscada.da.core.data.Variant;

public class DataItem
{
    private String _itemName;
    private Connection _connection = null;
    
    private Variant _value = new Variant();
    private Map<String,Variant> _attributes = new HashMap<String,Variant>();
    
    private ItemUpdateListener _listener = new ItemUpdateListener(){

        public void notifyValueChange ( Variant value, boolean initial )
        {
            performNotifyValueChange ( value, initial );
        }

        public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
        {
            performNotifyAttributeChange ( attributes, initial );
        }};
    
    public DataItem ( String itemName )
    {
        _itemName = new String(itemName);
    }
    
    public DataItem ( String itemName, Connection connection )
    {
        this(itemName);
        
        register ( connection );
    }
    
    synchronized public void register ( Connection connection )
    {
        if ( _connection == connection )
            return;
        
        _connection = connection;
        _connection.addItemUpdateListener ( _itemName, true, _listener );
    }
    
    synchronized public void unregister ()
    {
        if ( _connection == null )
            return;
        
        _connection.removeItemUpdateListener ( _itemName, _listener );
    }
    
    private void performNotifyValueChange ( Variant value, boolean initial )
    {
        _value = value;
    }

    private void performNotifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
    {
        if ( initial )
            _attributes = new HashMap<String,Variant> ( attributes );
        else
            AttributesHelper.mergeAttributes ( _attributes, attributes );
    }
    
    /**
     * Fetch the current cached value.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current value
     */
    public Variant getValue ()
    {
        return _value;
    }
    
    /**
     * Fetch the current cached attributes.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current attributes
     */
    public Map<String,Variant> getAttributes ()
    {
        return _attributes;
    }
    
}
