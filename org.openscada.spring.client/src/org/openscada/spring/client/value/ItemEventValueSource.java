/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.spring.client.value;

import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.spring.client.Connection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A value source based on OpenSCADA DA items
 * @author Jens Reimann
 *
 */
public class ItemEventValueSource extends AbstractBaseValueSource implements ValueSource, DisposableBean, InitializingBean
{
    private Connection connection;

    private String itemName;

    private DataItem dataItem;

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.connection, "'connection' must not be null" );
        Assert.hasText ( this.itemName, "'itemName' must be set" );

        this.dataItem = new DataItem ( this.itemName, this.connection.getItemManager () );
    }

    @Override
    public void destroy () throws Exception
    {
        this.dataItem.unregister ();
    }

    @Override
    public DataItemValue getValue ()
    {
        return this.dataItem.getSnapshotValue ();
    }

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    public void setItemName ( final String itemName )
    {
        this.itemName = itemName;
    }

}
