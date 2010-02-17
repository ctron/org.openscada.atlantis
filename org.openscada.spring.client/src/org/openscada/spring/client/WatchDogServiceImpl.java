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

package org.openscada.spring.client;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.spring.client.command.ValueCommand;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Implements the watchdog interface using OPC and the ABB system 
 * @author jens reimann
 *
 */
public class WatchDogServiceImpl implements WatchDogService, InitializingBean
{
    private static Logger logger = Logger.getLogger ( WatchDogServiceImpl.class );

    private Collection<ValueCommand> commands;

    private final long startTimestamp = System.currentTimeMillis ();

    public void tick ()
    {
        for ( final ValueCommand command : this.commands )
        {
            try
            {
                command.command ( generateCurrentValue () );
            }
            catch ( final Throwable e )
            {
                // ignore error
                logger.info ( "Unable to write watchdog", e );
            }
        }
    }

    /**
     * Generate the current watchdog value
     * @return the current watchdog value
     */
    protected Variant generateCurrentValue ()
    {
        long ts = System.currentTimeMillis () - this.startTimestamp;
        ts = ts / 1000;
        return new Variant ( (int)ts );
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.commands, "'commands' must be set" );
    }

    public void setCommands ( final Collection<ValueCommand> commands )
    {
        this.commands = commands;
    }

    public void setCommand ( final ValueCommand command )
    {
        this.commands = new ArrayList<ValueCommand> ();
        this.commands.add ( command );
    }

}
