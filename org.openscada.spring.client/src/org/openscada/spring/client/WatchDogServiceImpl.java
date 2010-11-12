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
