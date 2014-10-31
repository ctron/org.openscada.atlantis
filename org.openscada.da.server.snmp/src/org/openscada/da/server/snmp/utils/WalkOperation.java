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

import java.io.IOException;
import java.util.Vector;

import org.openscada.da.server.snmp.SNMPNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public abstract class WalkOperation
{
    private final static Logger logger = LoggerFactory.getLogger ( WalkOperation.class );

    private final SNMPNode node;

    private final OID oid;

    private final boolean useBulkGet;

    public WalkOperation ( final SNMPNode node, final OID oid, final boolean useBulkGet )
    {
        this.node = node;
        this.oid = oid;
        this.useBulkGet = useBulkGet;
    }

    protected abstract void handleOID ( OID oid );

    private OID processResponse ( final ResponseEvent responseEvent )
    {
        if ( responseEvent == null )
        {
            return null;
        }

        if ( responseEvent.getError () != null )
        {
            logger.warn ( "Listing failed", responseEvent.getError () );
            return null;
        }

        if ( responseEvent.getResponse () == null )
        {
            return null;
        }

        final PDU response = responseEvent.getResponse ();
        if ( response.getErrorStatus () != 0 )
        {
            logger.warn ( String.format ( "Error in reply: %1$d", response.getErrorStatus () ) );
            return null;
        }

        if ( response.getType () == PDU.REPORT )
        {
            return null;
        }

        final Vector<? extends VariableBinding> vbs = response.getVariableBindings ();

        OID nextOID = null;
        for ( final VariableBinding vb : vbs )
        {
            if ( vb.isException () )
            {
                logger.info ( "Binding Exception: {}", vb );
                return null;
            }
            if ( vb.getVariable ().isException () )
            {
                logger.info ( "Variable Exception: {}", vb.getVariable () );
                return null;
            }

            final OID oid = vb.getOid ();
            if ( !oid.startsWith ( this.oid ) )
            {
                logger.info ( "OID has not same root" );
                return null;
            }

            handleOID ( oid );

            nextOID = oid;
        }

        return nextOID;
    }

    public void run ()
    {
        logger.debug ( "Starting walk operation for subtree: {}", this.oid );

        final Target target = this.node.getConnection ().createTarget ();
        final PDU request = this.node.getConnection ().createPDU ( target, this.useBulkGet ? PDU.GETBULK : PDU.GETNEXT );

        if ( this.useBulkGet )
        {
            request.setNonRepeaters ( 0 );
            request.setMaxRepetitions ( 10 );
        }

        OID currentOID = this.oid;
        boolean endOfList = false;

        // add dummy entry .. will be set later
        request.add ( new VariableBinding ( new OID ( this.oid ) ) );

        while ( !endOfList )
        {
            request.set ( 0, new VariableBinding ( currentOID ) );
            request.setRequestID ( new Integer32 ( 0 ) );
            ResponseEvent responseEvent;
            try
            {
                logger.trace ( "Requesting: {}", request.get ( 0 ).getOid () );
                responseEvent = this.node.getConnection ().send ( target, request );
                currentOID = processResponse ( responseEvent );
                endOfList = currentOID == null;
            }
            catch ( final IOException e )
            {
                endOfList = true;
            }

        }
    }
}
