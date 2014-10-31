/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

    private final SNMPNode node;

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
     * 
     * @throws Exception
     */
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

        final Vector<? extends VariableBinding> vbs = reply.getVariableBindings ();

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
     * 
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
