/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.snmp.items;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.data.IODirection;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.ItemListener;
import org.openscada.da.server.common.SuspendableDataItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.snmp.SNMPNode;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.DirectExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * An SNMP data item
 * @author Jens Reimann
 *
 */
public class SNMPItem extends DataItemInputChained implements Runnable, SuspendableDataItem
{
    private final static Logger logger = LoggerFactory.getLogger ( SNMPItem.class );

    private static final String ATTR_NO_INITIAL_DATA_ERROR = "noInitialData.error";

    private static final String ATTR_READ_ERROR = "read.error";

    private SNMPNode node = null;

    private OID oid = null;

    public SNMPItem ( final SNMPNode node, final String id, final OID oid )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ), DirectExecutor.INSTANCE );

        this.node = node;
        this.oid = oid;

        updateData ( Variant.NULL, new MapBuilder<String, Variant> ().put ( ATTR_NO_INITIAL_DATA_ERROR, Variant.TRUE ).getMap (), AttributeMode.SET );
    }

    @Override
    public void setListener ( final ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            wakeup ();
        }
        else
        {
            suspend ();
        }
    }

    public void start ()
    {
        logger.debug ( "Starting item: {}", this.oid );
        this.node.getBulkReader ().add ( this );
    }

    public void stop ()
    {
        logger.debug ( "Stopping item: {}", this.oid );

        this.node.getBulkReader ().remove ( this );

        updateData ( Variant.NULL, new HashMap<String, Variant> (), AttributeMode.SET );
    }

    /*
    synchronized private void updateValue ( Variant value )
    {
        logger.debug ( "Value update: " + value.asString ( "<null>" ) );
        if ( !_value.equals ( value ) )
        {
            _value = value;
            notifyData ( _value, null );
        }
    }
    */

    @Override
    public void run ()
    {
        logger.debug ( "Updating value" );

        try
        {
            read ();
        }
        catch ( final Exception e )
        {
            readFailed ( e );
        }

        logger.debug ( "Update complete" );
    }

    private Variant convertValue ( final VariableBinding vb ) throws Exception
    {
        if ( vb == null )
        {
            throw new Exception ( "Empty reply" );
        }

        if ( vb.isException () )
        {
            throw new Exception ( vb.toString () );
        }

        final Variable v = vb.getVariable ();
        logger.debug ( "Variable is: {}", v );
        if ( v instanceof Null )
        {
            return Variant.NULL;
        }
        else if ( v instanceof Counter64 )
        {
            return Variant.valueOf ( ( (Counter64)v ).getValue () );
        }
        else if ( v instanceof UnsignedInteger32 )
        {
            return Variant.valueOf ( ( (UnsignedInteger32)v ).getValue () );
        }
        else if ( v instanceof Integer32 )
        {
            return Variant.valueOf ( ( (Integer32)v ).getValue () );
        }
        else
        {
            return Variant.valueOf ( v.toString () );
        }
    }

    public void readFailed ( final Throwable t )
    {
        setError ( t.getMessage () );
    }

    public void readComplete ( final VariableBinding vb )
    {
        try
        {
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( ATTR_READ_ERROR, Variant.FALSE );
            attributes.put ( ATTR_NO_INITIAL_DATA_ERROR, null );
            updateData ( convertValue ( vb ), attributes, AttributeMode.UPDATE );
            clearError ();
        }
        catch ( final Exception e )
        {
            logger.error ( "Item read failed", e );
            setError ( e );
        }
    }

    private void read () throws Exception
    {
        final ResponseEvent response = this.node.getConnection ().sendGET ( this.oid );

        if ( response == null )
        {
            throw new Exception ( "No response" );
        }

        final PDU reply = response.getResponse ();
        if ( reply == null )
        {
            throw new Exception ( "No reply" );
        }

        readComplete ( reply.get ( 0 ) );
    }

    private void clearError ()
    {
        setError ( (String)null );
    }

    private void setError ( final Throwable t )
    {
        if ( t.getMessage () == null )
        {
            setError ( t.toString () );
        }
        else
        {
            setError ( t.getMessage () );
        }
    }

    private void setError ( final String text )
    {
        if ( text != null )
        {
            logger.info ( "Setting error: {}", text );
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( ATTR_READ_ERROR, Variant.valueOf ( text != null ) );
        attributes.put ( "lastReadError.message", Variant.valueOf ( text ) );
        attributes.put ( ATTR_NO_INITIAL_DATA_ERROR, null );

        updateData ( null, attributes, AttributeMode.UPDATE );
    }

    public OID getOID ()
    {
        return this.oid;
    }

    @Override
    public void suspend ()
    {
        stop ();
    }

    @Override
    public void wakeup ()
    {
        start ();
    }

}
