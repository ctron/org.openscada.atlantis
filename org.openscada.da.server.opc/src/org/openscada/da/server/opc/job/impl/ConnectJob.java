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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job performs the connect operation
 * 
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 */
public class ConnectJob extends ThreadJob
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( ConnectJob.class );

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

        logger.info ( "Socket timeout: {}", this.globalSocketTimeout );

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
            this.comServer = new JIComServer ( JIProgId.valueOf ( this.connectionInformation.getProgId () ), this.connectionInformation.getHost (), this.session );
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
