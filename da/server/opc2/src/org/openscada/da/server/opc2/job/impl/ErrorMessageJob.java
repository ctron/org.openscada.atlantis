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
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.JobResult;
import org.openscada.da.server.opc2.job.ThreadJob;

/**
 * This job resolves an error string
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class ErrorMessageJob extends ThreadJob implements JobResult<String>
{
    public static final long DEFAULT_TIMEOUT = 1000L;

    private static Logger log = Logger.getLogger ( ErrorMessageJob.class );

    private OPCModel model;

    private String result;

    private int errorCode;

    public ErrorMessageJob ( long timeout, OPCModel model, int errorCode )
    {
        super ( timeout );
        this.model = model;
        this.errorCode = errorCode;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request error message" );
        result = this.model.getCommon ().getErrorString ( errorCode, 0 );
    }

    public String getResult ()
    {
        return result;
    }
}
