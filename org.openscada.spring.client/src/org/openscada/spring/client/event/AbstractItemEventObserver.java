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

package org.openscada.spring.client.event;

import org.openscada.da.client.ItemUpdateListener;
import org.openscada.spring.client.Connection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A generic item observer which generates different specific events from the
 * different item events of OpenSCADA
 * @author Jens Reimann
 *
 */
public abstract class AbstractItemEventObserver implements InitializingBean, DisposableBean, ItemUpdateListener
{
    /**
     * The item name
     */
    protected String itemName;

    /**
     * The connection to the server
     */
    protected Connection connection;

    protected boolean running;

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.connection, "'connection' must not be null" );
        Assert.notNull ( this.itemName, "'itemName' must not be null" );
        start ();
    }

    protected void start ()
    {
        this.running = true;
        this.connection.getItemManager ().addItemUpdateListener ( this.itemName, this );
    }

    public void destroy () throws Exception
    {
        stop ();
    }

    protected void stop ()
    {
        this.connection.getItemManager ().removeItemUpdateListener ( this.itemName, this );
        this.running = false;
    }

    public void setItemName ( final String itemName )
    {
        this.itemName = itemName;
    }
}
