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

package org.openscada.spring.client.command;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.spring.client.Connection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A value command implementation based on OpenSCADA
 * @author jens reimann
 *
 */
public class DataItemCommand implements ValueCommand, InitializingBean
{
    private static Logger logger = Logger.getLogger ( DataItemCommand.class );

    /**
     * The connection on which to write
     */
    private Connection connection;

    /**
     * The data item name on which to write
     */
    private String itemName;

    public void command ( final Variant value ) throws NoConnectionException, OperationException
    {
        logger.debug ( String.format ( "Writing %s to connection", value.asString ( "<null>" ) ) );
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
