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
import org.jinterop.dcom.core.IJIBindingSelector;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.openscada.da.server.opc2.job.ThreadJob;
import org.openscada.opc.dcom.common.impl.OPCCommon;
import org.openscada.opc.dcom.da.impl.OPCGroupStateMgt;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.openscada.opc.dcom.da.impl.OPCServer;
import org.openscada.opc.dcom.da.impl.OPCSyncIO;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.OPC;
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

    private ConnectionInformation connectionInformation;

    private long globalSocketTimeout;

    private JISession session = null;
    private JIComServer comServer = null;
    private OPCServer server = null;
    private ErrorMessageResolver errorMessageResolver = null;

    private OPCGroupStateMgt group;

    private OPCItemMgt itemMgt;

    private OPCSyncIO syncIo;

    private OPCCommon common;

    private int updateRate;

    public ConnectJob ( long timeout, ConnectionInformation connectionInformation, long globalSocketTimeout, int updateRate )
    {
        super ( timeout );
        this.connectionInformation = connectionInformation;
        this.globalSocketTimeout = globalSocketTimeout;
        this.updateRate = updateRate;
    }

    @Override
    protected void perform () throws Exception
    {

        IJIBindingSelector selector = OPC.createBindingSelector ( connectionInformation.getPreferredHosts () );

        log.info ( String.format ( "Socket timeout: %s ", globalSocketTimeout ) );

        if ( connectionInformation.getClsid () != null )
        {
            session = JISession.createSession ( connectionInformation.getDomain (), connectionInformation.getUser (),
                    connectionInformation.getPassword () );
            session.setGlobalSocketTimeout ( (int)globalSocketTimeout );
            session.setBindingSelector ( selector );
            comServer = new JIComServer ( JIClsid.valueOf ( connectionInformation.getClsid () ),
                    connectionInformation.getHost (), session );
        }
        else if ( connectionInformation.getProgId () != null )
        {
            session = JISession.createSession ( connectionInformation.getDomain (), connectionInformation.getUser (),
                    connectionInformation.getPassword () );
            session.setGlobalSocketTimeout ( (int)globalSocketTimeout );
            session.setBindingSelector ( selector );
            comServer = new JIComServer ( JIProgId.valueOf ( session, connectionInformation.getClsid () ),
                    connectionInformation.getHost (), session );
        }
        else
        {
            throw new IllegalArgumentException ( "Neither clsid nor progid is valid!" );
        }

        server = new OPCServer ( comServer.createInstance () );
        this.common = server.getCommon ();

        group = server.addGroup ( null, true, updateRate, 0, null, null, 0 );
        this.itemMgt = group.getItemManagement ();
        this.syncIo = group.getSyncIO ();

    }

    public JISession getSession ()
    {
        return session;
    }

    public JIComServer getComServer ()
    {
        return comServer;
    }

    public OPCServer getServer ()
    {
        return server;
    }

    public ErrorMessageResolver getErrorMessageResolver ()
    {
        return errorMessageResolver;
    }

    public OPCGroupStateMgt getGroup ()
    {
        return group;
    }

    public OPCItemMgt getItemMgt ()
    {
        return itemMgt;
    }

    public OPCSyncIO getSyncIo ()
    {
        return syncIo;
    }

    public OPCCommon getCommon ()
    {
        return common;
    }
}
