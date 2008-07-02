/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.execServer.commands.CommandQueueType;
import org.openscada.da.execServer.commands.CommandType;
import org.openscada.da.execServer.commands.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.base.Command;
import org.openscada.da.server.exec.base.CommandQueue;
import org.openscada.da.server.exec.factory.CommandFactory;
import org.openscada.da.server.exec.factory.CommandQueueFactory;
import org.openscada.da.server.exec.factory.CommandResultParserFactory;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static final int DEFAULT_QUEUE_PERIOD = 250;

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
    private final List<Timer> threads = new ArrayList<Timer> ();

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

        // Init root folder
        this.document = document;
        this.setRootFolder ( this.rootFolder );

        // Setup and start the queues
        this.startQueues ();
    }

    /**
     * Initializes all configured command queues and executes them in threads
     */
    protected void startQueues ()
    {
        // Iterate through all configured command queues and initialize them
        for ( CommandQueueType commandQueueConfig : this.document.getRoot ().getCommandQueueList () )
        {
            // Create the queue
            CommandQueue commandQueue = null;
            try
            {
                commandQueue = CommandQueueFactory.createCommandQueue ( commandQueueConfig.getCommandQueueClass (), commandQueueConfig.getCommandQueueName () );
                logger.info ( "Created command queue " + commandQueueConfig.getCommandQueueName () );
            }
            catch ( Exception e )
            {
                logger.error ( "Error creating command queue from class name: " + commandQueueConfig.getCommandQueueClass () + ". Reason: " + e.getMessage () );
                break;
            }

            // Iterate through all commands of the command queue and initialize them
            for ( CommandType commandConfig : commandQueueConfig.getCommandList () )
            {
                Command command = null;
                try
                {
                    command = CommandFactory.createCommand ( commandConfig.getCommandClass (), this, commandConfig.getCommandName (), commandQueue );
                    logger.info ( "Created command " + commandConfig.getCommandName () );

                    // Set command properties
                    command.setCommandLine ( commandConfig.getCommandLine () );
                    command.setMinDelay ( commandConfig.getMinPeriod () );
                }
                catch ( Exception e )
                {
                    logger.error ( "Error creating command from class name: " + commandConfig.getCommandClass () + ". Reason: " + e.getMessage () );
                    break;
                }

                // Create a parser for the command
                try
                {
                    // let the parser class be optional for now since the actually don't need it
                    if ( commandConfig.isSetParserClass () )
                    {
                        command.setParser ( CommandResultParserFactory.createParser ( commandConfig.getParserClass (), this, command ) );
                    }
                }
                catch ( Exception e )
                {
                    logger.error ( "Error creating parser from class name: " + commandConfig.getParserClass () + ". Reason: " + e.getMessage () );
                    break;
                }

                // Add the command to the queue
                commandQueue.addCommand ( command );
            }

            // Create the queue
            Timer executor = new Timer ( commandQueue.getQueueName () + "-timer", true );

            // get the queue period time .. use a default it none specified
            int period = commandQueueConfig.getDelay ();
            if ( period <= 0 )
            {
                period = DEFAULT_QUEUE_PERIOD;
            }

            // Start the queue
            final CommandQueue commandQueueRef = commandQueue;
            executor.scheduleAtFixedRate ( new TimerTask () {

                @Override
                public void run ()
                {
                    commandQueueRef.run ();
                }
            }, commandQueueConfig.getInitialDelay (), period );
            this.threads.add ( executor );
        }
    }

    /**
     * Stops all running command queues
     */
    @Override
    public void dispose ()
    {
        // Send stop request
        for ( Timer executor : this.threads )
        {
            executor.cancel ();
        }

        // Remove all threads from the list
        this.threads.clear ();
    }
}
