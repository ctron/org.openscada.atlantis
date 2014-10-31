/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.snmp.utils;

import org.openscada.da.server.snmp.SNMPNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

public class DumpOIDWalker extends WalkOperation
{

    private final static Logger logger = LoggerFactory.getLogger ( DumpOIDWalker.class );

    public DumpOIDWalker ( final SNMPNode node, final OID oid, final boolean useBulkGet )
    {
        super ( node, oid, useBulkGet );
    }

    @Override
    protected void handleOID ( final OID oid )
    {
        logger.info ( "OID found: {}", oid );
    }

}
