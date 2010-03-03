/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.snmp.items;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.ItemListener;
import org.openscada.da.server.common.SuspendableDataItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.snmp.SNMPNode;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.DirectExecutor;
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
    private static final String ATTR_NO_INITIAL_DATA_ERROR = "noInitialData.error";

    private static final String ATTR_READ_ERROR = "read.error";

    private static Logger _log = Logger.getLogger ( SNMPItem.class );

    private SNMPNode _node = null;

    private OID _oid = null;

    public SNMPItem ( final SNMPNode node, final String id, final OID oid )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ), DirectExecutor.INSTANCE );

        this._node = node;
        this._oid = oid;

        updateData ( new Variant (), new MapBuilder<String, Variant> ().put ( ATTR_NO_INITIAL_DATA_ERROR, Variant.TRUE ).getMap (), AttributeMode.SET );
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
        _log.debug ( "Starting item: " + this._oid );
        this._node.getBulkReader ().add ( this );
    }

    public void stop ()
    {
        _log.debug ( "Stopping item: " + this._oid );

        this._node.getBulkReader ().remove ( this );

        updateData ( new Variant (), new HashMap<String, Variant> (), AttributeMode.SET );
    }

    /*
    synchronized private void updateValue ( Variant value )
    {
        _log.debug ( "Value update: " + value.asString ( "<null>" ) );
        if ( !_value.equals ( value ) )
        {
            _value = value;
            notifyData ( _value, null );
        }
    }
    */

    public void run ()
    {
        _log.debug ( "Updating value" );

        try
        {
            read ();
        }
        catch ( final Exception e )
        {
            readFailed ( e );
        }

        _log.debug ( "Update complete" );
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
        _log.debug ( "Variable is: " + v );
        if ( v instanceof Null )
        {
            return new Variant ();
        }
        else if ( v instanceof Counter64 )
        {
            return new Variant ( ( (Counter64)v ).getValue () );
        }
        else if ( v instanceof UnsignedInteger32 )
        {
            return new Variant ( ( (UnsignedInteger32)v ).getValue () );
        }
        else if ( v instanceof Integer32 )
        {
            return new Variant ( ( (Integer32)v ).getValue () );
        }
        else
        {
            return new Variant ( v.toString () );
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
            _log.error ( "Item read failed: ", e );
            setError ( e );
        }
    }

    private void read () throws Exception
    {
        final ResponseEvent response = this._node.getConnection ().sendGET ( this._oid );

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
            _log.info ( "Setting error: " + text );
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( ATTR_READ_ERROR, Variant.valueOf ( text != null ) );
        attributes.put ( "lastReadError.message", new Variant ( text ) );
        attributes.put ( ATTR_NO_INITIAL_DATA_ERROR, null );

        updateData ( null, attributes, AttributeMode.UPDATE );
    }

    public OID getOID ()
    {
        return this._oid;
    }

    public void suspend ()
    {
        stop ();
    }

    public void wakeup ()
    {
        start ();
    }

}
