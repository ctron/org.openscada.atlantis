/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.job.impl;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.openscada.utils.str.StringHelper;

/**
 * This job removes items from an opc group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
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
