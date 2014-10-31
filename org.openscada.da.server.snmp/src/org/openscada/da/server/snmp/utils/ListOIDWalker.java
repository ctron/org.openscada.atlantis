/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.snmp.utils;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.server.snmp.SNMPNode;
import org.snmp4j.smi.OID;

public class ListOIDWalker extends WalkOperation
{

    private final Set<OID> oidSet = new HashSet<OID> ();

    public ListOIDWalker ( final SNMPNode node, final OID oid, final boolean useBulkGet )
    {
        super ( node, oid, useBulkGet );
    }

    @Override
    protected void handleOID ( final OID oid )
    {
        this.oidSet.add ( oid );
    }

    public Set<OID> getList ()
    {
        return this.oidSet;
    }

}
