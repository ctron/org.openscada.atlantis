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

package org.openscada.da.server.snmp.utils;

import org.apache.log4j.Logger;
import org.openscada.da.server.snmp.SNMPNode;
import org.snmp4j.smi.OID;

public class DumpOIDWalker extends WalkOperation
{
    private static Logger _log = Logger.getLogger ( DumpOIDWalker.class );

    public DumpOIDWalker ( final SNMPNode node, final OID oid, final boolean useBulkGet )
    {
        super ( node, oid, useBulkGet );
    }

    @Override
    protected void handleOID ( final OID oid )
    {
        _log.info ( "OID found: " + oid.toString () );
    }

}
