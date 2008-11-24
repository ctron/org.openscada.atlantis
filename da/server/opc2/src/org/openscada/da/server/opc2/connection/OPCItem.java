/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
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
import org.openscada.utils.collection.MapBuilder;

public class OPCItem extends DataItemInputOutputChained implements SuspendableDataItem
{
    private static Logger logger = Logger.getLogger ( OPCItem.class );

    private boolean suspended = true;

    private Variant lastValue;

    private final OPCController controller;

    private final String opcItemId;

    private boolean ignoreTimestampOnlyChange = false;

    public OPCItem ( final Hive hive, final OPCController controller, final DataItemInformation di, final String opcItemId )
    {
        super ( di );
        this.controller = controller;
        this.opcItemId = opcItemId;

        this.ignoreTimestampOnlyChange = controller.getModel ().isIgnoreTimestampOnlyChange ();

        this.updateData ( null, new MapBuilder<String, Variant> ().put ( "opc.connection.error", new Variant ( true ) ).put ( "opc.itemId", new Variant ( opcItemId ) ).getMap (), AttributeMode.SET );
    }

    @Override
    protected void writeCalculatedValue ( final Variant value ) throws NotConvertableException, InvalidOperationException
    {
        if ( !this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
        {
            logger.warn ( String.format ( "Failed to write to read-only item ()", this.opcItemId ) );
            throw new InvalidOperationException ();
        }

        // check if the conversion works ... will be performed again by the addWriteRequest call
        final JIVariant variant = Helper.ours2theirs ( value );
        logger.debug ( String.format ( "Converting write request from %s to %s", value, variant ) );
        if ( variant == null )
        {
            // unable to convert write request
            logger.info ( "Unable to convert write request" );
            throw new NotConvertableException ();
        }

        this.controller.getIoManager ().addWriteRequest ( this.opcItemId, value );
    }

    public void suspend ()
    {
        this.suspended = true;
        this.controller.getIoManager ().suspendItem ( this.opcItemId );
    }

    public void wakeup ()
    {
        this.suspended = false;
        this.controller.getIoManager ().wakeupItem ( this.opcItemId );
    }

    public void updateStatus ( final KeyedResult<Integer, OPCITEMSTATE> entry, final String errorMessage )
    {
        if ( this.suspended )
        {
            return;
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        final OPCITEMSTATE state = entry.getValue ();
        attributes.put ( "opc.connection.error", null );

        if ( entry.isFailed () )
        {
            attributes.put ( "opc.read.error", new Variant ( true ) );
            attributes.put ( "opc.read.error.code", new Variant ( entry.getErrorCode () ) );
            attributes.put ( "opc.read.error.message", new Variant ( String.format ( "0x%08x: %s", entry.getErrorCode (), errorMessage ) ) );

            attributes.put ( "opc.quality", null );
            attributes.put ( "timestamp", null );
            attributes.put ( "timestamp.message", null );
            attributes.put ( "opc.value.type", null );

            attributes.put ( "opc.value.conversion.error", null );
            attributes.put ( "opc.value.conversion.source", null );

            this.lastValue = null;

            updateData ( new Variant (), attributes, AttributeMode.UPDATE );
        }
        else
        {
            attributes.put ( "opc.read.error", null );
            attributes.put ( "opc.read.error.code", null );
            attributes.put ( "opc.read.error.message", null );

            final short quality = state.getQuality ();
            attributes.put ( "opc.quality", new Variant ( quality ) );

            attributes.put ( "opc.quality.error", new Variant ( quality < 192 ) );
            attributes.put ( "opc.quality.manual", new Variant ( quality == 216 ) );
            attributes.put ( "org.openscada.da.manual.active", new Variant ( quality == 216 ) );

            attributes.put ( "opc.value.type", null );
            try
            {
                attributes.put ( "opc.value.type", new Variant ( state.getValue ().getType () ) );
            }
            catch ( final Throwable e )
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

                if ( !this.ignoreTimestampOnlyChange || this.lastValue == null || !this.lastValue.equals ( value ) )
                {
                    attributes.put ( "timestamp", new Variant ( state.getTimestamp ().asCalendar ().getTimeInMillis () ) );
                    attributes.put ( "timestamp.message", new Variant ( String.format ( "%tc", state.getTimestamp ().asCalendar () ) ) );
                }

            }

            updateData ( value, attributes, AttributeMode.UPDATE );

            this.lastValue = value;
        }
    }

    /**
     * Setting the last write error information
     * @param result the write result that caused the error or <code>null</code> in case the reason is unknown
     */
    public void setLastWriteResult ( final Result<WriteRequest> result )
    {
        final Calendar c = Calendar.getInstance ();

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
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

    public void itemRealized ( final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.putAll ( Helper.convertToAttributes ( entry ) );
        attributes.putAll ( Helper.convertToAttributes ( entry.getKey () ) );
        attributes.putAll ( Helper.convertToAttributes ( entry.getValue () ) );

        this.updateData ( null, attributes, AttributeMode.UPDATE );
    }

    public void itemUnrealized ()
    {
        final Map<String, Variant> attributes = Helper.clearAttributes ();
        attributes.put ( "opc.connection.error", new Variant ( true ) );
        this.updateData ( null, attributes, AttributeMode.UPDATE );
    }

}
