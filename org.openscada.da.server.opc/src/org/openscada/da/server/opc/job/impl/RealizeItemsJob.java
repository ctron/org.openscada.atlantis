/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.job.impl;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;

/**
 * This job adds an item to an opc group
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class RealizeItemsJob extends ThreadJob implements JobResult<KeyedResultSet<OPCITEMDEF, OPCITEMRESULT>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( RealizeItemsJob.class );

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
        log.debug ( "Realizing items" );
        this.result = this.itemMgt.add ( this.itemDefs );
    }

    public KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> getResult ()
    {
        return this.result;
    }
}
