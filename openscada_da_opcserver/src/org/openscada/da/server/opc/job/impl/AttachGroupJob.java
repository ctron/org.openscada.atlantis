/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.EventHandler;
import org.openscada.opc.dcom.da.IOPCDataCallback;
import org.openscada.opc.dcom.da.impl.OPCGroupStateMgt;

/**
 * Attach to the connection point of the group
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class AttachGroupJob extends ThreadJob implements JobResult<EventHandler>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( AttachGroupJob.class );

    private final OPCModel model;

    private final IOPCDataCallback dataCallback;

    private EventHandler result;

    public AttachGroupJob ( final long timeout, final OPCModel model, final IOPCDataCallback dataCallback )
    {
        super ( timeout );
        this.model = model;
        this.dataCallback = dataCallback;
    }

    @Override
    protected void perform () throws Exception
    {
        log.info ( "Perform group attach" );

        final OPCGroupStateMgt group = this.model.getGroup ();
        if ( group != null )
        {
            this.result = group.attach ( this.dataCallback );
        }
    }

    public EventHandler getResult ()
    {
        return this.result;
    }

}
