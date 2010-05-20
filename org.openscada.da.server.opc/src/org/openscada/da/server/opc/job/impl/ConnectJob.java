/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.impl.OPCCommon;
import org.openscada.opc.dcom.da.impl.OPCAsyncIO2;
import org.openscada.opc.dcom.da.impl.OPCGroupStateMgt;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.openscada.opc.dcom.da.impl.OPCServer;
import org.openscada.opc.dcom.da.impl.OPCSyncIO;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.ErrorMessageResolver;

/**
 * This job performs the connect operation
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class ConnectJob extends ThreadJob
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( ConnectJob.class );

    private final ConnectionInformation connectionInformation;

    private final long globalSocketTimeout;

    private JISession session = null;

    private JIComServer comServer = null;

    private OPCServer server = null;

    private final ErrorMessageResolver errorMessageResolver = null;

    private OPCGroupStateMgt group;

    private OPCItemMgt itemMgt;

    private OPCSyncIO syncIo;

    private OPCAsyncIO2 asyncIo2;

    private OPCCommon common;

    private final int updateRate;

    public ConnectJob ( final long timeout, final ConnectionInformation connectionInformation, final long globalSocketTimeout, final int updateRate )
    {
        super ( timeout );
        this.connectionInformation = connectionInformation;
        this.globalSocketTimeout = globalSocketTimeout;
        this.updateRate = updateRate;
    }

    @Override
    protected void perform () throws Exception
    {

        /*
        IJIBindingSelector selector = OPC.createBindingSelector ( connectionInformation.getPreferredHosts () );
         */

        log.info ( String.format ( "Socket timeout: %s ", this.globalSocketTimeout ) );

        if ( this.connectionInformation.getClsid () != null )
        {
            this.session = JISession.createSession ( this.connectionInformation.getDomain (), this.connectionInformation.getUser (), this.connectionInformation.getPassword () );
            this.session.setGlobalSocketTimeout ( (int)this.globalSocketTimeout );
            // session.setBindingSelector ( selector );
            this.comServer = new JIComServer ( JIClsid.valueOf ( this.connectionInformation.getClsid () ), this.connectionInformation.getHost (), this.session );
        }
        else if ( this.connectionInformation.getProgId () != null )
        {
            this.session = JISession.createSession ( this.connectionInformation.getDomain (), this.connectionInformation.getUser (), this.connectionInformation.getPassword () );
            this.session.setGlobalSocketTimeout ( (int)this.globalSocketTimeout );
            // session.setBindingSelector ( selector );
            this.comServer = new JIComServer ( JIProgId.valueOf ( this.connectionInformation.getClsid () ), this.connectionInformation.getHost (), this.session );
        }
        else
        {
            throw new IllegalArgumentException ( "Neither clsid nor progid is valid!" );
        }

        this.server = new OPCServer ( this.comServer.createInstance () );
        this.common = this.server.getCommon ();

        this.group = this.server.addGroup ( null, true, this.updateRate, 0, null, null, 0 );
        this.itemMgt = this.group.getItemManagement ();
        this.syncIo = this.group.getSyncIO ();
        this.asyncIo2 = this.group.getAsyncIO2 ();
    }

    public JISession getSession ()
    {
        return this.session;
    }

    public JIComServer getComServer ()
    {
        return this.comServer;
    }

    public OPCServer getServer ()
    {
        return this.server;
    }

    public ErrorMessageResolver getErrorMessageResolver ()
    {
        return this.errorMessageResolver;
    }

    public OPCGroupStateMgt getGroup ()
    {
        return this.group;
    }

    public OPCItemMgt getItemMgt ()
    {
        return this.itemMgt;
    }

    public OPCSyncIO getSyncIo ()
    {
        return this.syncIo;
    }

    public OPCCommon getCommon ()
    {
        return this.common;
    }

    public OPCAsyncIO2 getAsyncIo2 ()
    {
        return this.asyncIo2;
    }
}
