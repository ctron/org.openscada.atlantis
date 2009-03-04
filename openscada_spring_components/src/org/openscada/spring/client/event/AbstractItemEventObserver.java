/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
