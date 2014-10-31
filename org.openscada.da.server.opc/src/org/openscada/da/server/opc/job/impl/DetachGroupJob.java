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

import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detach from the connection point of a group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class DetachGroupJob extends ThreadJob
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( DetachGroupJob.class );

    private final EventHandler eventHandler;

    public DetachGroupJob ( final long timeout, final EventHandler eventHandler )
    {
        super ( timeout );
        this.eventHandler = eventHandler;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.info ( "Perform group detach" );
        this.eventHandler.detach ();
    }

}
