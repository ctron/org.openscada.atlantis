/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.connection;

import org.eclipse.scada.da.server.common.factory.DataItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data item factory which creates items bases on the
 * assigned OPC connection
 * @author Jens Reimann
 *
 */
public class OPCConnectionDataItemFactory implements DataItemFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( OPCConnectionDataItemFactory.class );

    private final OPCConnection connection;

    public OPCConnectionDataItemFactory ( final OPCConnection connection )
    {
        this.connection = connection;
    }

    @Override
    public boolean canCreate ( final String itemId )
    {
        return itemId.startsWith ( this.connection.getItemPrefix () + "." );
    }

    @Override
    public void create ( final String itemId )
    {
        logger.info ( "Requested: {}", itemId );
        final String opcItemId = itemId.substring ( this.connection.getItemPrefix ().length () + 1 );

        this.connection.addUnrealizedItem ( opcItemId );
    }

}
