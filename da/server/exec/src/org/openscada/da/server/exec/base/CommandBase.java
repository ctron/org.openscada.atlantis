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

/**
 * 
 */
package org.openscada.da.server.exec.base;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public abstract class CommandBase implements Command
{
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger ( CommandBase.class );

    /**
     * Command line of the call
     */
    private String commandLine;

    /**
     * The factory to create the items of this command
     */
    private final FolderItemFactory commandItemFactory;

    /**
     * A name for this command
     */
    private final String commandName;

    /**
     * The command queue where this command is to be registered in
     */
    private final CommandQueue queue;

    /**
     * The command line name as item
     */
    private final DataItemInputChained commandLineItem;

    /**
     * The class name of the command as item
     */
    private final DataItemInputChained commandTypeItem;

    /**
     * The time of the last execution of this command
     */
    private Calendar lastExecutionTime = null;

    /**
     * the minimum delay between executions
     */
    private int minDelay = 0;

    /** 
     * Item to store whether the command is currently active or not
     */
    private final DataItemInputChained busyItem;

    /**
     * Item to store the last execution time
     */
    private final DataItemInputChained lastExecutionTimeItem;

    /**
     * The period it took to execute the command in ms and as item
     */
    private final DataItemInputChained executionTimeItem;

    /** 
     * the minimum delay between executions as item
     */
    private final DataItemInputChained minPeriodItem;

    /**
     * the parser for the result
     */
    private CommandResultParser parser;

    /**
     * Item to store the queue name in
     */
    private final DataItemInputChained queueItem;

    /**
     * Constructor
     * @param hive
     */
    public CommandBase ( HiveCommon hive, String commandName, CommandQueue queue )
    {
        this.commandName = commandName;
        this.queue = queue;

        // Create the factory to create other items
        //this.commandItemFactory = queue.getFolderItemFactory ().createSubFolderFactory ( commandName );
        this.commandItemFactory = new FolderItemFactory ( hive, (FolderCommon)hive.getRootFolder (), this.commandName, this.commandName );

        // prepare the commandline as item
        this.commandLineItem = this.commandItemFactory.createInput ( "commandLine" );

        // prepare the last execution time as item
        this.lastExecutionTimeItem = this.commandItemFactory.createInput ( "lastExecutionTime" );

        // prepare the execution time as item
        this.executionTimeItem = this.commandItemFactory.createInput ( "executionTime" );

        // show the class name as item
        this.commandTypeItem = this.commandItemFactory.createInput ( "commandType" );
        this.commandTypeItem.updateValue ( new Variant ( this.toString () ) );

        // show whether the command is currently active or not
        this.busyItem = this.commandItemFactory.createInput ( "busy" );
        this.busyItem.updateValue ( new Variant ( false ) );

        // show whether the command is currently active or not
        this.minPeriodItem = this.commandItemFactory.createInput ( "minPeriod" );

        // print the queue this command is in
        this.queueItem = this.commandItemFactory.createInput ( "queueName" );
        this.queueItem.updateValue ( new Variant ( this.queue.getQueueName () ) );

    }

    /**
     * setCommandline
     */
    @Override
    public void setCommandLine ( String commandLine )
    {
        this.commandLine = commandLine;
        this.commandLineItem.updateValue ( new Variant ( commandLine ) );
    }

    /**
     * getCommandline
     * @return the commandline
     */
    public String getCommandLine ()
    {
        return this.commandLine;
    }

    /**
     * getCommandName
     */
    @Override
    public String getCommandName ()
    {
        return this.commandName;
    }

    /**
     * Returns the time of the last execution
     * @return
     */
    @Override
    public Calendar getLastExecutionTime ()
    {
        return this.lastExecutionTime;
    }

    /**
     * @return the parser
     */
    public CommandResultParser getParser ()
    {
        return this.parser;
    }

    /**
     * Sets the minimum time delay (ms) between executions
     * @param delay
     */
    @Override
    public void setMinDelay ( int delay )
    {
        this.minDelay = delay;
        this.minPeriodItem.updateValue ( new Variant ( this.minDelay ) );
    }

    /**
     * @return the commandItemFactory
     */
    public FolderItemFactory getCommandItemFactory ()
    {
        return this.commandItemFactory;
    }

    /**
     * sets a parser to the command
     */
    @Override
    public void setParser ( CommandResultParser parser )
    {
        this.parser = parser;
    }

    /**
     * run the command task
     */
    @Override
    public void tick ()
    {
        Calendar check = Calendar.getInstance ();
        check.add ( Calendar.MILLISECOND, -1 * this.minDelay );

        if ( this.getLastExecutionTime () == null || check.after ( this.getLastExecutionTime () ) )
        {
            // Execute the command
            this.busyItem.updateValue ( new Variant ( true ) );
            Calendar start = Calendar.getInstance ();
            this.execute ();
            Calendar stop = Calendar.getInstance ();
            this.busyItem.updateValue ( new Variant ( false ) );

            // Set the time of the finished execution
            this.lastExecutionTime = Calendar.getInstance ();
            this.lastExecutionTimeItem.updateValue ( new Variant ( this.lastExecutionTime.getTime ().toString () ) );
            this.executionTimeItem.updateValue ( new Variant ( stop.getTimeInMillis () - start.getTimeInMillis () ) );
            logger.debug ( this.getCommandName () + ": tick!" );
        }
    }

    public void dispose ()
    {
        this.commandItemFactory.dispose ();
    }
    
}
