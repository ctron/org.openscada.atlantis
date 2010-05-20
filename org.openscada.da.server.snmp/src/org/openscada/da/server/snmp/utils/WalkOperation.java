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

package org.openscada.da.server.snmp.utils;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.openscada.da.server.snmp.SNMPNode;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public abstract class WalkOperation
{
    private static Logger _log = Logger.getLogger ( WalkOperation.class );

    private SNMPNode _node = null;

    private OID _oid = null;

    private boolean _useBulkGet = false;

    public WalkOperation ( final SNMPNode node, final OID oid, final boolean useBulkGet )
    {
        this._node = node;
        this._oid = oid;
        this._useBulkGet = useBulkGet;
    }

    protected abstract void handleOID ( OID oid );

    @SuppressWarnings ( "unchecked" )
    private OID processResponse ( final ResponseEvent responseEvent )
    {
        if ( responseEvent == null )
        {
            return null;
        }

        if ( responseEvent.getError () != null )
        {
            _log.warn ( "Listing failed", responseEvent.getError () );
            return null;
        }

        if ( responseEvent.getResponse () == null )
        {
            return null;
        }

        final PDU response = responseEvent.getResponse ();
        if ( response.getErrorStatus () != 0 )
        {
            _log.warn ( String.format ( "Error in reply: %1$d", response.getErrorStatus () ) );
            return null;
        }

        if ( response.getType () == PDU.REPORT )
        {
            return null;
        }

        final Vector<VariableBinding> vbs = response.getVariableBindings ();

        OID nextOID = null;
        for ( final VariableBinding vb : vbs )
        {
            if ( vb.isException () )
            {
                _log.info ( "Binding Exception: " + vb.toString () );
                return null;
            }
            if ( vb.getVariable ().isException () )
            {
                _log.info ( "Variable Exception: " + vb.getVariable ().toString () );
                return null;
            }

            final OID oid = vb.getOid ();
            if ( !oid.startsWith ( this._oid ) )
            {
                _log.info ( "OID has not same root" );
                return null;
            }

            handleOID ( oid );

            nextOID = oid;
        }

        return nextOID;
    }

    public void run ()
    {
        _log.debug ( "Starting walk operation for subtree: " + this._oid );

        final Target target = this._node.getConnection ().createTarget ();
        final PDU request = this._node.getConnection ().createPDU ( target, this._useBulkGet ? PDU.GETBULK : PDU.GETNEXT );

        if ( this._useBulkGet )
        {
            request.setNonRepeaters ( 0 );
            request.setMaxRepetitions ( 10 );
        }

        OID currentOID = this._oid;
        boolean endOfList = false;

        // add dummy entry .. will be set later
        request.add ( new VariableBinding ( new OID ( this._oid ) ) );

        while ( !endOfList )
        {
            request.set ( 0, new VariableBinding ( currentOID ) );
            request.setRequestID ( new Integer32 ( 0 ) );
            ResponseEvent responseEvent;
            try
            {
                //_log.info ( "Requesting: " + request.get ( 0 ).getOid () );
                responseEvent = this._node.getConnection ().send ( target, request );
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
