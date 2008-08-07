/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.opc2.job.impl;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc2.job.JobResult;
import org.openscada.da.server.opc2.job.ThreadJob;
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

    private OPCItemMgt itemMgt;

    private OPCITEMDEF[] itemDefs;

    private KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> result;

    public RealizeItemsJob ( long timeout, OPCItemMgt itemMgt, OPCITEMDEF[] itemDefs )
    {
        super ( timeout );
        this.itemMgt = itemMgt;
        this.itemDefs = itemDefs;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Realizing items" );
        result = itemMgt.add ( itemDefs );
    }

    public KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> getResult ()
    {
        return result;
    }
}
