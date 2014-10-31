/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.browser;

import java.util.LinkedList;

import org.openscada.da.server.opc.connection.OPCController;
import org.openscada.da.server.opc.connection.OPCStateListener;

public class OPCRootTreeFolder extends OPCTreeFolder implements OPCStateListener
{

    private final OPCController controller;

    public OPCRootTreeFolder ( final OPCController controller )
    {
        super ( controller, new LinkedList<String> () );
        this.controller = controller;
    }

    @Override
    public void added ()
    {
        super.added ();
        this.controller.addStateListener ( this );
    }

    @Override
    public void removed ()
    {
        this.controller.removeStateListener ( this );
        super.removed ();
    }

    public void connectionEstablished ()
    {
        checkRefresh ();
    }

    public void connectionLost ()
    {
        this.folderImpl.clear ();
        this.refreshed = false;
    }

}
