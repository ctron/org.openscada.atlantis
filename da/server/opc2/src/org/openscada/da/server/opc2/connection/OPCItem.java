/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.connection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.SuspendableDataItem;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.da.server.opc2.Helper;
import org.openscada.da.server.opc2.Hive;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.WriteRequest;

public class OPCItem extends DataItemInputOutputChained implements SuspendableDataItem
{
    private static Logger logger = Logger.getLogger ( OPCItem.class );
    private OPCITEMDEF opcDefinition;
    private OPCItemManager manager;
    private boolean suspended = true;

    private Variant lastValue;

    public OPCItem ( Hive hive, OPCItemManager manager, DataItemInformation di, KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        super ( di );
        this.manager = manager;
        this.opcDefinition = entry.getKey ();
        
        intialData ( entry );
    }

    private void intialData ( KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        this.updateData ( null, Helper.convertToAttributes ( entry ), AttributeMode.SET );
    }

    @Override
    protected void writeCalculatedValue ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        if ( !this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
        {
            logger.warn ( String.format ( "Failed to write to read-only item ()", opcDefinition.getItemID () ) );
            throw new InvalidOperationException ();
        }

        // check if the conversion works ... will be performed again by the addWriteRequest call
        JIVariant variant = Helper.ours2theirs ( value );
        logger.debug ( String.format ( "Converting write request from %s to %s", value, variant ) );
        if ( variant == null )
        {
            // unable to convert write request
            logger.info ( "Unable to convert write request" );
            throw new NotConvertableException ();
        }
        
        this.manager.addWriteRequest ( this.opcDefinition.getItemID (), value );
    }

    public void suspend ()
    {
        suspended = true;
        this.manager.suspendItem ( this.opcDefinition.getItemID () );
    }

    public void wakeup ()
    {
        suspended = false;
        this.manager.wakeupItem ( this.opcDefinition.getItemID () );
    }

    public void updateStatus ( KeyedResult<Integer, OPCITEMSTATE> entry, String errorMessage )
    {
        if ( suspended )
        {
            return;
        }

        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        OPCITEMSTATE state = entry.getValue ();

        if ( entry.isFailed () )
        {
            attributes.put ( "opc.read.error", new Variant ( true ) );
            attributes.put ( "opc.read.error.code", new Variant ( entry.getErrorCode () ) );
            attributes.put ( "opc.read.error.message", new Variant ( String.format ( "0x%08x: %s",
                    entry.getErrorCode (), errorMessage ) ) );

            attributes.put ( "opc.quality", null );
            attributes.put ( "timestamp", null );
            attributes.put ( "timestamp.message", null );
            attributes.put ( "opc.value.type", null );

            attributes.put ( "opc.value.conversion.error", null );
            attributes.put ( "opc.value.conversion.source", null );

            lastValue = null;

            updateData ( new Variant (), attributes, AttributeMode.UPDATE );
        }
        else
        {
            attributes.put ( "opc.read.error", null );
            attributes.put ( "opc.read.error.code", null );
            attributes.put ( "opc.read.error.message", null );

            attributes.put ( "opc.quality", new Variant ( state.getQuality () ) );
            attributes.put ( "opc.value.type", null );
            try
            {
                attributes.put ( "opc.value.type", new Variant ( state.getValue ().getType () ) );
            }
            catch ( Throwable e )
            {
            }

            Variant value = Helper.theirs2ours ( state.getValue () );

            if ( value == null )
            {
                value = new Variant ();
                attributes.put ( "opc.value.conversion.error", new Variant ( true ) );
                attributes.put ( "opc.value.conversion.source", new Variant ( state.getValue ().toString () ) );
            }
            else
            {
                attributes.put ( "opc.value.conversion.error", null );
                attributes.put ( "opc.value.conversion.source", null );

                if ( lastValue == null || ( !lastValue.equals ( value ) ) )
                {
                    attributes.put ( "timestamp", new Variant (
                            state.getTimestamp ().asCalendar ().getTimeInMillis () ) );
                    attributes.put ( "timestamp.message", new Variant ( String.format ( "%tc",
                            state.getTimestamp ().asCalendar () ) ) );
                }

            }

            updateData ( value, attributes, AttributeMode.UPDATE );

            lastValue = value;
        }
    }

    /**
     * Setting the last write error information
     * @param result the write result that caused the error or <code>null</code> in case the reason is unknown
     */
    public void setLastWriteError ( Result<WriteRequest> result )
    {
        Calendar c = Calendar.getInstance ();

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        if ( result != null )
        {
            attributes.put ( "opc.lastWriteError.code", new Variant ( result.getErrorCode () ) );
            attributes.put ( "opc.lastWriteError.message", new Variant ( String.format ( "0x%08x", result.getErrorCode () ) ) );
        }
        else
        {
            attributes.put ( "opc.lastWriteError.code", new Variant ( -1 ) );
            attributes.put ( "opc.lastWriteError.message", new Variant ( "unknown error" ) );
        }
        attributes.put ( "opc.lastWriteError.timestamp", new Variant ( c.getTimeInMillis () ) );
        attributes.put ( "opc.lastWriteError.timestamp.message", new Variant ( String.format ( "%tc", c ) ) );
        updateData ( null, attributes, AttributeMode.UPDATE );
    }

}
