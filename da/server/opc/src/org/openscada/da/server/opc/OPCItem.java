package org.openscada.da.server.opc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemBase;
import org.openscada.da.core.common.SuspendableItem;
import org.openscada.da.core.common.WriteAttributesHelper;
import org.openscada.da.core.common.chain.DataItemBaseChained;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.IODirection;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.SyncAccess;

public class OPCItem extends DataItemBase implements SuspendableItem, DataCallback
{
    private static Logger _log = Logger.getLogger ( OPCItem.class );
    
    private String _itemId = null;
    private Item _item = null;
    private OPCConnection _connection = null;
    
    private AttributeManager _attributes = null;
    
    private Variant _value = new Variant ();

    public OPCItem ( DataItemInformation information, OPCConnection connection, String itemId ) throws JIException, AddFailedException
    {
        super ( information );
        _attributes = new AttributeManager ( this );
        
        _itemId = itemId;
        
        _connection = connection;
        
        _item = _connection.getGroup ().addItem ( itemId );
    }
    
    public String getId ()
    {
        return _itemId; 
    }

    public Map<String, Variant> getAttributes ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public synchronized Variant getValue () throws InvalidOperationException
    {
        return _value;
    }

    public Results setAttributes ( Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        if ( !getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
            throw new InvalidOperationException ();
        
        write ( value );
    }

    public void suspend ()
    {
        _log.debug ( "Suspend" );
        _connection.getAccess ().removeItem ( _itemId );
    }

    public void wakeup ()
    {
        _log.debug ( "Wakeup" );
        try
        {
            _connection.getAccess ().addItem ( _itemId, this );
        }
        catch ( JIException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( AddFailedException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void changed ( Item item, ItemState itemState )
    {
        updateValue ( itemState );
    }
    
    protected synchronized void updateValue ( ItemState itemState )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        attributes.put ( "opc.quality", new Variant ( itemState.getQuality () ) );
        attributes.put ( "timestamp", new Variant ( itemState.getTimestamp ().getTimeInMillis () ) );
        try
        {
            attributes.put ( "opc.value-type", new Variant ( itemState.getValue().getType () ) );
        }
        catch ( JIException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Variant newValue = Helper.theirs2ours ( itemState.getValue () ); 
        
        if ( newValue == null )
        {
            attributes.put ( "opc.value.error", new Variant ( "Unable to convert value: " + itemState.getValue ().toString () ) );
        }
        else
        {
            attributes.put ( "opc.value.error", null );
            updateValue ( newValue );
        }
        
        _attributes.update ( attributes );
    }
    
    protected synchronized void updateValue ( Variant value )
    {
        if ( value == null )
            return;
        
        if ( !_value.equals ( value ) )
        {
            _value = value;
            notifyValue ( _value );
        }
    }
    
    protected void write ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        JIVariant variant = Helper.ours2theirs ( value );
        
        if ( variant == null )
            throw new NotConvertableException ();
        
        try
        {
            _item.write ( variant );
            _attributes.update ( "opc.write.last-error-code", null );
        }
        catch ( JIException e )
        {
            _attributes.update ( "opc.write.last-error-code", new Variant ( e.getErrorCode () ) );
            throw new InvalidOperationException ();
        }
    }
}
