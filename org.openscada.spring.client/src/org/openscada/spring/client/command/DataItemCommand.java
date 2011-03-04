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

package org.openscada.spring.client.command;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.spring.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A value command implementation based on OpenSCADA
 * @author jens reimann
 *
 */
public class DataItemCommand implements ValueCommand, InitializingBean
{

    private final static Logger logger = LoggerFactory.getLogger ( DataItemCommand.class );

    /**
     * The connection on which to write
     */
    private Connection connection;

    /**
     * The data item name on which to write
     */
    private String itemName;

    @Override
    public void command ( final Variant value ) throws NoConnectionException, OperationException
    {
        logger.debug ( "Writing {} to connection", value.asString ( "<null>" ) );
        this.connection.writeItem ( this.itemName, value );
    }

    @Override
    public String toString ()
    {
        return "DataItemCommand#" + this.itemName;
    }

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.connection, "'connection' must not be null" );
        Assert.hasText ( this.itemName, "'itemName' must be set" );
    }

    public void setItemName ( final String itemName )
    {
        this.itemName = itemName;
    }

}
