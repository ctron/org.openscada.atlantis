/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;

/**
 * This job resolves an error string
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class ErrorMessageJob extends ThreadJob implements JobResult<String>
{
    public static final long DEFAULT_TIMEOUT = 1000L;

    private static Logger log = Logger.getLogger ( ErrorMessageJob.class );

    private final OPCModel model;

    private String result;

    private final int errorCode;

    public ErrorMessageJob ( final long timeout, final OPCModel model, final int errorCode )
    {
        super ( timeout );
        this.model = model;
        this.errorCode = errorCode;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request error message" );
        this.result = this.model.getCommon ().getErrorString ( this.errorCode, 0 );
    }

    public String getResult ()
    {
        return this.result;
    }
}
