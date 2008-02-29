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
import org.openscada.da.server.common.SuspendableDataItem;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.opc.lib.da.AccessStateListener;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;

public class OPCItem extends DataItemInputOutputChained implements DataCallback, AccessStateListener, SuspendableDataItem
{
    private static final String OPC_ATTRIBUTE_PREFIX = "org.openscada.opc";
    private static final String OPC_ATTRIBUTE_VALUE_ERROR = OPC_ATTRIBUTE_PREFIX + ".value.error";
    private static final String OPC_ATTRIBUTE_VALUE_ERROR_MESSAGE = OPC_ATTRIBUTE_PREFIX + ".value.error.message";

    private static final String OPC_ATTRIBUTE_READ_ERROR = OPC_ATTRIBUTE_PREFIX + ".read.error";
    private static final String OPC_ATTRIBUTE_READ_ERROR_CODE = OPC_ATTRIBUTE_READ_ERROR + ".code";
    private static final String OPC_ATTRIBUTE_READ_ERROR_MESSAGE = OPC_ATTRIBUTE_READ_ERROR + ".message";

    private static final String OPC_ATTRIBUTE_WRITE_ERROR = OPC_ATTRIBUTE_PREFIX + ".write.error";
    private static final String OPC_ATTRIBUTE_WRITE_ERROR_CODE = OPC_ATTRIBUTE_WRITE_ERROR + ".code";
    private static final String OPC_ATTRIBUTE_WRITE_ERROR_MESSAGE = OPC_ATTRIBUTE_WRITE_ERROR + ".message";

    private static final String OPC_ATTRIBUTE_UPDATE_ERROR = OPC_ATTRIBUTE_PREFIX + ".update.error";
    private static final String OPC_ATTRIBUTE_UPDATE_ERROR_CODE = OPC_ATTRIBUTE_UPDATE_ERROR + ".code";
    private static final String OPC_ATTRIBUTE_UPDATE_ERROR_MESSAGE = OPC_ATTRIBUTE_UPDATE_ERROR + ".message";

    private static final String OPC_ATTRIBUTE_QUALITY = OPC_ATTRIBUTE_PREFIX + ".quality";
    private static final String OPC_ATTRIBUTE_VALUE_TYPE = OPC_ATTRIBUTE_PREFIX + ".value-type";

    private static final String OPC_ATTRIBUTE_TIMESTAMP = OPC_ATTRIBUTE_PREFIX + ".timestamp";

    private static Logger _log = Logger.getLogger ( OPCItem.class );

    private String _itemId = null;
    private Object _itemLock = new Object ();
    private Item _item = null;
    private OPCConnection _connection = null;

    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    public OPCItem ( DataItemInformation information, OPCConnection connection, String itemId ) throws JIException, AddFailedException
    {
        super ( information );

        _itemId = itemId;
        _connection = connection;
    }

    public Item getItem ()
    {
        Item item = _item;
        Group group = _connection.getGroup ();
        
        // first check if we really have the right item
        if ( item != null )
        {
            if ( group == item.getGroup () )
            {
                // it is our group and our item
                return item;
            }
            // scrap item! wrong group
            _log.warn ( String.format ( "OPC write item '%s' has wrong group attached! Scraping item!!", _itemId ) );
            item = _item = null;
        }

        try
        {
            _log.info ( String.format ( "Binding write item (%s) to OPC", _itemId ) );
            if ( group == null )
            {
                _item = null; // we are not allowed to have an item here
                _log.warn ( String.format ( "Failed to bind write item since we are disconnected (%s)", _itemId ) );
                return null;
            }

            item = group.addItem ( _itemId );
            synchronized ( _itemLock )
            {
                if ( _item == null )
                {
                    _item = item;
                }
                else
                {
                    _log.warn ( String.format ( "Item '%s' already has a write item", _itemId ) );
                }
                // always return the current item
                item = _item;
            }
        }
        catch ( Throwable e )
        {
            _log.info ( "Failed to bind write item", e );
        }
        return item;
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
         * We need to intercept this call since we handle IO flags separately. Although
         * we derived from an IO item we may be reduced in functionality due to the 
         * underlying OPC item. So we check here if OUTPUT is possible and when 
         * successful we pass the request on to our superclass.
         */
        if ( !getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
        {
            _log.warn ( String.format ( "Tried to write to item %s which is read-only", _itemId ) );
            throw new InvalidOperationException ();
        }

        if ( this._connection.getState () == ConnectionState.CONNECTED )
        {
            super.writeValue ( value );
        }
        else
        {
            _log.info ( String.format ( "Failed to write to disconnected item (%s)", _itemId ) );
            throw new InvalidOperationException ();
        }

    }

    public synchronized void suspend ()
    {
        _log.debug ( "Suspend: " + _itemId );
        _connection.getAccess ().removeItem ( _itemId );
        _connection.countItemState ( this, false );
        _connection.getAccess ().removeStateListener ( this );
        stateChanged ( false );

        clearAttributes ();
        updateValue ( (ItemState)null );
    }

    public synchronized void wakeup ()
    {
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
        // attributes.put ( OPC_ATTRIBUTE_TIMESTAMP, null );

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
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_MESSAGE, new Variant (
                            _connection.getServer ().getErrorMessage ( errorCode ) ) );
                }
                else if ( itemState.getValue ().getType () == JIVariant.VT_ERROR )
                {
                    int errorCode = itemState.getValue ().getObjectAsSCODE ();
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_CODE, new Variant ( errorCode ) );
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR_MESSAGE, new Variant (
                            _connection.getServer ().getErrorMessage ( errorCode ) ) );
                    attributes.put ( OPC_ATTRIBUTE_READ_ERROR, new Variant ( true ) );
                }
                else
                {
                    newValue = Helper.theirs2ours ( itemState.getValue () );
                    if ( newValue != null && !newValue.equals ( _primaryValue ) )
                    {
                        // only update timestamp when we detected a change
                        attributes.put ( OPC_ATTRIBUTE_TIMESTAMP, new Variant ( System.currentTimeMillis () ) );
                    }
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

        if ( newValue == null )
        {
            attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR_MESSAGE, new Variant ( "Unable to convert value: "
                    + itemState.getValue ().toString () ) );
            attributes.put ( OPC_ATTRIBUTE_VALUE_ERROR, new Variant ( true ) );
        }

        // definition is to first write the attributes and then the value
        updateAttributes ( attributes );
        if ( newValue != null )
        {
            updateValue ( newValue );
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

        // we cannot write nothing
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
                updateAttribute ( OPC_ATTRIBUTE_WRITE_ERROR_CODE, new Variant ( errorCode ) );
                if ( errorCode != 0 )
                {

                    String errorMessage = _connection.getServer ().getErrorMessage ( errorCode );
                    updateAttribute ( OPC_ATTRIBUTE_WRITE_ERROR_MESSAGE, new Variant ( errorMessage ) );
                    _log.warn ( String.format ( "Failed to write to item %s: 0x%08X - %s", _itemId, errorCode,
                            errorMessage ) );
                    throw new InvalidOperationException ();
                }
                else
                {
                    updateAttribute ( OPC_ATTRIBUTE_WRITE_ERROR_MESSAGE, null );
                }
            }
            else
            {
                _log.warn ( "Failed to write item: currently item is not bound to OPC" );
                throw new InvalidOperationException ();
            }
        }
        catch ( JIException e )
        {
            if ( e.getErrorCode () == 0x8001FFFF )
            {
                // internal error .. socket closed?
            }
            updateAttribute ( OPC_ATTRIBUTE_WRITE_ERROR_CODE, new Variant ( e.getErrorCode () ) );
            _log.warn ( String.format ( "Failed to write to item (call) %s: 0x%08X", _itemId, e.getErrorCode () ), e );
            throw new InvalidOperationException ();
        }
        catch ( InvalidOperationException e )
        {
            throw new InvalidOperationException ();
        }
        catch ( Throwable e )
        {
            _log.warn ( "Failed to write due to unknown error", e );
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
            _log.info ( String.format ( "Item (%s) failed", _itemId ), t );
            updateAttribute ( "org.openscada.opc.last-error", new Variant ( t.getMessage () ) );
        }
    }

    public void stateChanged ( boolean state )
    {
        _log.debug ( String.format ( "State changed: %s", state ) );
        updateAttribute ( "connection.error", new Variant ( !state ) );

        if ( !state )
        {
            synchronized ( _itemLock )
            {
                if ( _item != null )
                {
                    _log.info ( String.format ( "Scraping OPC write item (%s)", _itemId ) );
                    _item = null;
                }
            }
        }
    }

    public Map<String, Variant> getBrowserAttributes ()
    {
        return _browserAttributes;
    }

}
