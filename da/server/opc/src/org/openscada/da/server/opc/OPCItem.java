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
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemBase;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.common.SuspendableItem;
import org.openscada.da.core.common.WriteAttributesHelper;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.IODirection;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.SyncAccessStateListener;

public class OPCItem extends DataItemBase implements SuspendableItem, DataCallback, SyncAccessStateListener
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
        _connection.getAccess ().addStateListener ( this );
    }
    
    /**
     * We might already have data when the listener connects 
     */
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            if ( !_value.isNull () )
                notifyValue ( _value );
            if ( _attributes.get ().size () > 0 )
                notifyAttributes ( _attributes.get () );
        }
    }
    
    public synchronized Item getItem ()
    {
        if ( _item == null )
        {
            try
            {
                _item = _connection.getGroup ().addItem ( _itemId );
            }
            catch ( Throwable t )
            {}
        }
        return _item;
    }
    
    public String getId ()
    {
        return _itemId; 
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes.getCopy ();
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

    /**
     * Callback from sync access object
     */
    public void changed ( Item item, ItemState itemState )
    {
        updateValue ( itemState );
    }
    
    protected synchronized void updateValue ( ItemState itemState )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        attributes.put ( "opc.value-error.message", null );
        attributes.put ( "opc.quality", new Variant ( itemState.getQuality () ) );
        attributes.put ( "timestamp", new Variant ( itemState.getTimestamp ().getTimeInMillis () ) );
        attributes.put ( "opc.update-error.code", null );
        attributes.put ( "opc.update-error.message", null );
        attributes.put ( "opc.read-error.code", null );
        attributes.put ( "opc.read-error.message", null );
        
        try
        {
            attributes.put ( "opc.value-type", new Variant ( itemState.getValue().getType () ) );

            Variant newValue = new Variant ();

            if ( itemState.getErrorCode () != 0 )
            {
                int errorCode = itemState.getErrorCode ();
                attributes.put ( "opc.read-error.code", new Variant ( errorCode ) );
                attributes.put ( "opc.read-error.message", new Variant ( _connection.getServer ().getErrorMessage ( errorCode ) ) );
            }
            else if ( itemState.getValue ().getType () == JIVariant.VT_ERROR )
            {
                int errorCode = itemState.getValue().getObjectAsSCODE ();
                attributes.put ( "opc.read-error.code", new Variant ( errorCode ) );
                attributes.put ( "opc.read-error.message", new Variant ( _connection.getServer ().getErrorMessage ( errorCode ) ) );
            }
            else
            {
                newValue = Helper.theirs2ours ( itemState.getValue () );

                if ( newValue == null )
                {
                    attributes.put ( "opc.value-error.message", new Variant ( "Unable to convert value: " + itemState.getValue ().toString () ) );
                }
                else
                {
                }
            }
            updateValue ( newValue );
        }
        catch ( JIException e )
        {
            attributes.put ( "opc.update-error.code", new Variant ( e.getErrorCode () ) );
            attributes.put ( "opc.update-error.message", new Variant ( e.getMessage () ) );
        }
        
        _attributes.update ( attributes );
    }
    
    protected synchronized void updateValue ( Variant value )
    {
        if ( value == null )
            value = new Variant ();
        
        if ( !_value.equals ( value ) )
        {
            _value = value;
            notifyValue ( _value );
        }
    }
    
    /**
     * perform a write request
     * @param value
     * @throws NotConvertableException
     * @throws InvalidOperationException
     */
    protected void write ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        JIVariant variant = Helper.ours2theirs ( value );
        
        if ( variant == null )
        {
            _log.warn ( "Unable to convert value to JIVariant: " + value.toString () );
            throw new NotConvertableException ();
        }
        
        try
        {
            Item item = getItem ();
            if ( item != null )
            {
                int errorCode = _item.write ( variant );
                _attributes.update ( "opc.write.last-error.code", new Variant ( errorCode ) );
                if ( errorCode != 0 )
                {
                    _attributes.update ( "opc.write.last-error.message", new Variant ( _connection.getServer ().getErrorMessage ( errorCode ) ) );
                    throw new InvalidOperationException ();
                }
                else
                {
                    _attributes.update ( "opc.write.last-error.message", null );
                }
            }
            else
            {
                throw new InvalidOperationException ();
            }
        }
        catch ( JIException e )
        {
            _attributes.update ( "opc.write.last-error-code", new Variant ( e.getErrorCode () ) );
            throw new InvalidOperationException ();
        }
    }

    public void errorOccured ( Throwable t )
    {
        if ( t == null )
        {
            _attributes.update ( "opc.last-error", null );
        }
        else {
            _attributes.update ( "opc.last-error", new Variant ( t.getMessage () ) );        
        }

    }

    public synchronized void stateChanged ( boolean state )
    {   
        _attributes.update ( "connected", new Variant ( state ) );
        
        if ( state )
        {
            
        }
        else
        {
            _item = null;
        }
    }
}
