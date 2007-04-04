/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
import org.openscada.da.core.IODirection;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.opc.lib.da.AccessStateListener;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;

public class OPCItem extends DataItemInputOutputChained implements DataCallback, AccessStateListener
{
    private static final String OPC_ATTRIBUTE_PREFIX = "org.openscada.opc";
    private static final String OPC_ATTRIBUTE_VALUE_ERROR = OPC_ATTRIBUTE_PREFIX + ".value.error";
    private static final String OPC_ATTRIBUTE_VALUE_ERROR_MESSAGE = OPC_ATTRIBUTE_PREFIX + ".value.error.message";
    
    private static final String OPC_ATTRIBUTE_READ_ERROR = OPC_ATTRIBUTE_PREFIX + ".read.error";
    private static final String OPC_ATTRIBUTE_READ_ERROR_CODE = OPC_ATTRIBUTE_READ_ERROR + ".code";
    private static final String OPC_ATTRIBUTE_READ_ERROR_MESSAGE = OPC_ATTRIBUTE_READ_ERROR + ".message";
    
    private static final String OPC_ATTRIBUTE_UPDATE_ERROR = OPC_ATTRIBUTE_PREFIX + ".update.error";
    private static final String OPC_ATTRIBUTE_UPDATE_ERROR_CODE = OPC_ATTRIBUTE_UPDATE_ERROR + ".code";
    private static final String OPC_ATTRIBUTE_UPDATE_ERROR_MESSAGE = OPC_ATTRIBUTE_UPDATE_ERROR + ".message";

    private static final String OPC_ATTRIBUTE_QUALITY = OPC_ATTRIBUTE_PREFIX + ".quality";
    private static final String OPC_ATTRIBUTE_VALUE_TYPE = OPC_ATTRIBUTE_PREFIX + ".value-type";
    
    private static Logger _log = Logger.getLogger ( OPCItem.class );

    private String _itemId = null;
    private Item _item = null;
    private OPCConnection _connection = null;

    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    public OPCItem ( DataItemInformation information, OPCConnection connection, String itemId ) throws JIException, AddFailedException
    {
        super ( information );

        _itemId = itemId;

        _connection = connection;
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
            {
            }
        }
        return _item;
    }

    public String getId ()
    {
        return _itemId;
    }

    /**
     * This method intercepts the write operation call in order to handle the IO
     * capabilities based on the OPC flags.
     * <br/>
     * If the item is capable of OUTPUT then the request will be passed on to the
     * superclass ({@link super#writeValue}).
     */
    @Override
    public void writeValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        /*
         * We need to intercept this call since we handle IO flags seperately. Although
         * we dereived from an IO item we may be reduced in functionality due to the 
         * underlaying OPC item. So we check here if OUTPUT is possible and when 
         * successfull we pass the request on to our superclass.
         */
        if ( !getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
        {
            throw new InvalidOperationException ();
        }

        super.writeValue ( value );
    }

    @Override
    public synchronized void suspend ()
    {
        super.suspend ();

        _log.debug ( "Suspend: " + _itemId );
        _connection.getAccess ().removeItem ( _itemId );
        _connection.countItemState ( this, false );
        _connection.getAccess ().removeStateListener ( this );
        stateChanged ( false );

        updateValue ( (ItemState)null );
    }

    @Override
    public synchronized void wakeup ()
    {
        super.wakeup ();

        _log.debug ( "Wakeup: " + _itemId );
        try
        {
            _connection.getAccess ().addStateListener ( this );
            _connection.getAccess ().addItem ( _itemId, this );
            _connection.countItemState ( this, true );
        }
        catch ( JIException e )
        {
            errorOccured ( e );
        }
        catch ( AddFailedException e )
        {
            errorOccured ( e );
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

        attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR, null );
        attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR_MESSAGE, null );
        attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR, null );
        attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_CODE, null );
        attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_MESSAGE, null );
        attributes.put ( OPC_ATTRIBUTE_READ_ERROR_CODE, null );
        attributes.put ( OPC_ATTRIBUTE_READ_ERROR_MESSAGE, null );
        attributes.put ( OPC_ATTRIBUTE_READ_ERROR, null );
        attributes.put ( OPC_ATTRIBUTE_VALUE_TYPE, null );
        
        Variant newValue = null;

        if ( itemState != null )
        {
            attributes.put ( OPC_ATTRIBUTE_QUALITY, new Variant ( itemState.getQuality () ) );
            attributes.put ( "timestamp", new Variant ( itemState.getTimestamp ().getTimeInMillis () ) );

            try
            {
                attributes.put ( OPC_ATTRIBUTE_VALUE_TYPE, new Variant ( itemState.getValue ().getType () ) );

                newValue = new Variant ();
                
                if ( itemState.getErrorCode () != 0 )
                {
                    int errorCode = itemState.getErrorCode ();
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_CODE, new Variant ( errorCode ) );
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_MESSAGE, new Variant ( _connection.getServer ().getErrorMessage (
                            errorCode ) ) );
                }
                else if ( itemState.getValue ().getType () == JIVariant.VT_ERROR )
                {
                    int errorCode = itemState.getValue ().getObjectAsSCODE ();
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_CODE, new Variant ( errorCode ) );
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_MESSAGE, new Variant ( _connection.getServer ().getErrorMessage (
                            errorCode ) ) );
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR, new Variant ( true ) );
                }
                else
                {
                    newValue = Helper.theirs2ours ( itemState.getValue () );
                }
            }
            catch ( JIException e )
            {
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR, new Variant ( true ) );
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_CODE, new Variant ( e.getErrorCode () ) );
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_MESSAGE, new Variant ( e.getMessage () ) );
            }
            catch ( Throwable e )
            {
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR, new Variant ( true ) );
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_CODE, new Variant ( 0xFFFFFFFF ) );
                attributes.put ( OPC_ATTRIBUTE_UPDATE_ERROR_MESSAGE, new Variant ( e.getMessage () ) );
            }
        }
        else
        {
            attributes.put ( OPC_ATTRIBUTE_QUALITY, null );
            attributes.put ( "timestamp", null );
            attributes.put ( OPC_ATTRIBUTE_VALUE_TYPE, null );
            
            newValue = new Variant ();
        }

        updateAttributes ( attributes );
        if ( newValue != null )
        {
            updateValue ( newValue );
        }
        else
        {
            attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR_MESSAGE, new Variant ( "Unable to convert value: "
                    + itemState.getValue ().toString () ) );
            attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR, new Variant ( true ) );
        }
    }

    protected void updateAttribute ( String name, Variant value )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( name, value );
        updateAttributes ( attributes );
    }

    /**
     * perform a write request
     * @param value
     * @throws NotConvertableException
     * @throws InvalidOperationException
     */
    protected void writeCalculatedValue ( Variant value ) throws NotConvertableException, InvalidOperationException
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
                int errorCode = item.write ( variant );
                updateAttribute ( "org.openscada.opc.write.last-error.code", new Variant ( errorCode ) );
                if ( errorCode != 0 )
                {
                    updateAttribute ( "org.openscada.opc.write.last-error.message", new Variant (
                            _connection.getServer ().getErrorMessage ( errorCode ) ) );
                    throw new InvalidOperationException ();
                }
                else
                {
                    updateAttribute ( "org.openscada.opc.write.last-error.message", null );
                }
            }
            else
            {
                throw new InvalidOperationException ();
            }
        }
        catch ( JIException e )
        {
            updateAttribute ( "org.openscada.opc.write.last-error-code", new Variant ( e.getErrorCode () ) );
            throw new InvalidOperationException ();
        }
    }

    public void errorOccured ( Throwable t )
    {
        if ( t == null )
        {
            updateAttribute ( "org.openscada.opc.last-error", null );
        }
        else
        {
            updateAttribute ( "org.openscada.opc.last-error", new Variant ( t.getMessage () ) );
        }

    }

    public synchronized void stateChanged ( boolean state )
    {
        _log.debug ( String.format ( "State changed: %s", state ) );
        updateAttribute ( "connection.error", new Variant ( !state ) );

        if ( !state )
        {
            _item = null;
        }
    }

    public Map<String, Variant> getBrowserAttributes ()
    {
        return _browserAttributes;
    }

}
