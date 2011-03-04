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

package org.openscada.da.server.snmp.utils;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.openscada.da.server.snmp.Connection;
import org.openscada.da.server.snmp.SNMPNode;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class SNMPBulkReader
{
    private final static Logger logger = LoggerFactory.getLogger ( SNMPBulkReader.class );

    private SNMPNode node = null;

    private final Map<OID, SNMPItem> list = new ConcurrentHashMap<OID, SNMPItem> ();

    public SNMPBulkReader ( final SNMPNode node )
    {
        this.node = node;
    }

    public void add ( final SNMPItem item )
    {
        this.list.put ( item.getOID (), item );
    }

    public void remove ( final SNMPItem item )
    {
        this.list.remove ( item.getOID () );
    }

    public void read ()
    {
        try
        {
            performRead ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "GETBULK failed", e );
            setError ( e );
        }
    }

    /**
     * Perform a read of all stored items
     * @throws Exception
     */
    @SuppressWarnings ( "unchecked" )
    private void performRead () throws Exception
    {
        final Connection connection = this.node.getConnection ();

        final Target target = connection.createTarget ();
        final PDU pdu = connection.createPDU ( target, PDU.GET );

        for ( final OID oid : this.list.keySet () )
        {
            pdu.add ( new VariableBinding ( oid ) );
        }

        if ( pdu.size () <= 0 )
        {
            return;
        }

        logger.debug ( "Sending PDU..." );
        final ResponseEvent response = connection.send ( target, pdu );
        logger.debug ( "Sending PDU...response!" );

        if ( response == null )
        {
            throw new Exception ( "No response" );
        }

        final PDU reply = response.getResponse ();
        if ( reply == null )
        {
            throw new Exception ( "No reply" );
        }

        logger.debug ( "VB size: {}", reply.size () );

        final Vector<VariableBinding> vbs = reply.getVariableBindings ();

        for ( final VariableBinding vb : vbs )
        {
            logger.debug ( "Variable: {}", vb );

            final OID oid = vb.getOid ();

            final SNMPItem item = this.list.get ( oid );

            if ( item == null )
            {
                logger.info ( "Skipping unknown item: {}", oid );
                continue;
            }

            item.readComplete ( vb );
        }
    }

    /**
     * set an error for all items
     * @param t
     */
    private void setError ( final Throwable t )
    {
        for ( final SNMPItem item : this.list.values () )
        {
            item.readFailed ( t );
        }
    }
}
