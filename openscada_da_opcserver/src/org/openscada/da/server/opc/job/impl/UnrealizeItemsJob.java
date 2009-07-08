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
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.openscada.utils.str.StringHelper;

/**
 * This job removes items from an opc group
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class UnrealizeItemsJob extends ThreadJob implements JobResult<ResultSet<Integer>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( UnrealizeItemsJob.class );

    private final OPCItemMgt itemMgt;

    private final Integer[] serverHandles;

    private ResultSet<Integer> result;

    public UnrealizeItemsJob ( final long timeout, final OPCItemMgt itemMgt, final Integer[] serverHandles )
    {
        super ( timeout );
        this.itemMgt = itemMgt;
        this.serverHandles = serverHandles;
    }

    @Override
    protected void perform () throws Exception
    {

        if ( log.isInfoEnabled () )
        {
            log.info ( String.format ( "UnRealizing items: %s", StringHelper.join ( this.serverHandles, ", " ) ) );
        }
        this.result = this.itemMgt.remove ( this.serverHandles );
    }

    public ResultSet<Integer> getResult ()
    {
        return this.result;
    }
}
