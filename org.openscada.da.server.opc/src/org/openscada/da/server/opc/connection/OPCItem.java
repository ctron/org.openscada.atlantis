/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.opc.connection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.SuspendableDataItem;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.da.server.opc.Helper;
import org.openscada.da.server.opc.Hive;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.DirectExecutor;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class OPCItem extends DataItemInputOutputChained implements SuspendableDataItem
{
    private static Logger logger = Logger.getLogger ( OPCItem.class );

    private volatile boolean suspended = true;

    private Variant lastValue;

    private final OPCController controller;

    private final String opcItemId;

    private boolean ignoreTimestampOnlyChange = false;

    private short qualityErrorIfLessThen = 192;

    public OPCItem ( final Hive hive, final OPCController controller, final DataItemInformation di, final String opcItemId )
    {
        super ( di, DirectExecutor.INSTANCE );
        this.controller = controller;
        this.opcItemId = opcItemId;

        this.ignoreTimestampOnlyChange = controller.getModel ().isIgnoreTimestampOnlyChange ();
        this.qualityErrorIfLessThen = controller.getModel ().getQualityErrorIfLessThen ();

        this.updateData ( null, new MapBuilder<String, Variant> ().put ( "opc.connection.error", Variant.TRUE ).put ( "opc.itemId", new Variant ( opcItemId ) ).getMap (), AttributeMode.SET );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        if ( !this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT ) )
        {
            logger.warn ( String.format ( "Failed to write to read-only item ()", this.opcItemId ) );
            return new InstantErrorFuture<WriteResult> ( new InvalidOperationException ().fillInStackTrace () );
        }

        // check if the conversion works ... will be performed again by the addWriteRequest call
        final JIVariant variant = Helper.ours2theirs ( value );
        logger.debug ( String.format ( "Converting write request from %s to %s", value, variant ) );
        if ( variant == null )
        {
            // unable to convert write request
            logger.info ( "Unable to convert write request" );
            return new InstantErrorFuture<WriteResult> ( new NotConvertableException ( value.getValue () ).fillInStackTrace () );
        }

        return processWriteRequest ( value );
    }

    private NotifyFuture<WriteResult> processWriteRequest ( final Variant value )
    {
        final NotifyFuture<Result<WriteRequest>> future = this.controller.getIoManager ().addWriteRequest ( this.opcItemId, value );
        return new WriteFuture ( this, future );
    }

    public void suspend ()
    {
        logger.info ( "Suspend item: " + this.getInformation ().getName () );

        this.suspended = true;
        this.controller.getIoManager ().suspendItem ( this.opcItemId );
        this.controller.getIoManager ().unrequestItem ( this.opcItemId );
    }

    public void wakeup ()
    {
        logger.info ( "Wakeup item: " + this.getInformation ().getName () );

        this.suspended = false;
        this.controller.getIoManager ().wakeupItem ( this.opcItemId );
    }

    public void updateStatus ( final KeyedResult<Integer, ValueData> entry, final String errorMessage )
    {
        if ( this.suspended )
        {
            return;
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        final ValueData state = entry.getValue ();
        attributes.put ( "opc.connection.error", null );

        if ( entry.isFailed () )
        {
            attributes.put ( "opc.read.error", Variant.TRUE );
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

            attributes.put ( "opc.quality.error", Variant.valueOf ( quality < this.qualityErrorIfLessThen ) );
            attributes.put ( "opc.quality.manual", Variant.valueOf ( quality == 216 ) );
            attributes.put ( "org.openscada.da.manual.active", Variant.valueOf ( quality == 216 ) );

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
                attributes.put ( "opc.value.conversion.error", Variant.TRUE );
                attributes.put ( "opc.value.conversion.source", new Variant ( state.getValue ().toString () ) );
            }
            else
            {
                attributes.put ( "opc.value.conversion.error", null );
                attributes.put ( "opc.value.conversion.source", null );

                if ( !this.ignoreTimestampOnlyChange || this.lastValue == null || !this.lastValue.equals ( value ) )
                {
                    attributes.put ( "timestamp", new Variant ( state.getTimestamp ().getTimeInMillis () ) );
                    attributes.put ( "timestamp.message", new Variant ( String.format ( "%tc", state.getTimestamp () ) ) );
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
    public void setLastWriteError ( final Result<WriteRequest> result )
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
        attributes.put ( "opc.connection.error", Variant.TRUE );
        this.updateData ( null, attributes, AttributeMode.UPDATE );
    }

    @Override
    public WriteAttributeResults processSetAttributes ( final Map<String, Variant> attributes )
    {
        return super.processSetAttributes ( attributes );
    }

}
