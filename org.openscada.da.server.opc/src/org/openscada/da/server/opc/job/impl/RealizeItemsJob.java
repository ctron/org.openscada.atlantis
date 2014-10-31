/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job.impl;

import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job adds an item to an opc group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class RealizeItemsJob extends ThreadJob implements JobResult<KeyedResultSet<OPCITEMDEF, OPCITEMRESULT>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( RealizeItemsJob.class );

    private final OPCItemMgt itemMgt;

    private final OPCITEMDEF[] itemDefs;

    private KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> result;

    public RealizeItemsJob ( final long timeout, final OPCItemMgt itemMgt, final OPCITEMDEF[] itemDefs )
    {
        super ( timeout );
        this.itemMgt = itemMgt;
        this.itemDefs = itemDefs;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.debug ( "Realizing items" );
        this.result = this.itemMgt.add ( this.itemDefs );
    }

    @Override
    public KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> getResult ()
    {
        return this.result;
    }
}
