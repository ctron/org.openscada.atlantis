/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.exec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.execServer.commands.CommandQueueType;
import org.openscada.da.execServer.commands.CommandType;
import org.openscada.da.execServer.commands.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger ( Hive.class );

    /**
     * Root folder of the Hive
     */
    private final FolderCommon rootFolder = new FolderCommon ();

    /**
     * Configuration
     */
    private final RootDocument document;

    /**
     * List with all running command queues
     */
    private final List<ScheduledThreadPoolExecutor> threads = new ArrayList<ScheduledThreadPoolExecutor> ();

    /**
     * Maximum timeout, e.g. by waiting for the threads to end
     */
    private final long MAX_TIMEOUT = 10 * 1000;

    /**
     * Default Constructor
     * @throws XmlException
     * @throws IOException
     */
    public Hive () throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( "execServerConfiguration.xml" ) ) );
    }

    /**
     * Constructor
     * @param node
     * @throws XmlException
     */
    public Hive ( final Node node ) throws XmlException
    {
        this ( RootDocument.Factory.parse ( node ) );
    }

    /**
     * Set up the hive and start the command queues
     * @param document Configuration
     */
    public Hive ( final RootDocument document )
    {
        super ();

        this.document = document;
        this.setRootFolder ( this.rootFolder );

        this.startQueues ();
    }

    /**
     * Initializes all configured command queues and executes them in threads
     */
    protected void startQueues ()
    {
        // Iterate through all configured command queues and initialize them
        for ( CommandQueueType commandQueueConfig : this.document.getRoot ().getCommandQueuesList () )
        {
            CommandQueue commandQueue = null;
            try
            {
                commandQueue = CommandQueueFactory.createCommandQueue ( commandQueueConfig.getCommandQueueClass () );
                logger.error ( "Created command queue " + commandQueueConfig.getCommandQueueName () );
                commandQueue.setQueueName ( commandQueueConfig.getCommandQueueName () );
            }
            catch ( Exception e )
            {
                logger.equals ( "Error creating command queue from class name: " + commandQueueConfig.getCommandQueueClass () + ". Reason: " + e.getMessage () );
                break;
            }

            // Iterate through all commands of the command queue and initialize them
            for ( CommandType commandConfig : commandQueueConfig.getCommandList () )
            {
                Command command = null;
                try
                {
                    logger.error ( commandConfig.getCommandline () );
                    logger.error ( commandConfig.getCommandClass () );
                    command = CommandFactory.createCommand ( commandConfig.getCommandClass () );
                    logger.info ( "Created command " + commandConfig.getCommandline () );
                    command.setCommandline ( commandConfig.getCommandline () );
                }
                catch ( Exception e )
                {
                    logger.equals ( "Error creating command from class name: " + commandConfig.getCommandClass () + ". Reason: " + e.getMessage () );
                    break;
                }
            }

            // Start the queue
            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor ( 1 );
            executor.scheduleWithFixedDelay ( commandQueue, 0, 1, TimeUnit.SECONDS );
            this.threads.add ( executor );
        }
    }

    /**
     * Stops all running command queues
     */
    protected void stopQueues ()
    {
        // Send stop request
        for ( ScheduledThreadPoolExecutor executor : this.threads )
        {
            executor.shutdown ();
        }

        // Remove all threads from the list
        this.threads.clear ();
    }
}
