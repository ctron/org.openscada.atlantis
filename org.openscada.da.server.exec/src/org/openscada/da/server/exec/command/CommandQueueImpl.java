/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exec.command;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.exec.Hive;

public class CommandQueueImpl implements CommandQueue
{

    private static class Entry
    {
        private final SingleCommand command;

        private final int period;

        private long lastTimestamp;

        public Entry ( final SingleCommand command, final int period )
        {
            this.command = command;
            this.period = period;
        }

        public SingleCommand getCommand ()
        {
            return this.command;
        }

        public boolean canExecute ()
        {
            return System.currentTimeMillis () - this.lastTimestamp >= this.period;
        }

        public void execute ()
        {
            this.lastTimestamp = System.currentTimeMillis ();
            this.command.execute ();
        }
    }

    @SuppressWarnings ( "unused" )
    private final String id;

    @SuppressWarnings ( "unused" )
    private final Hive hive;

    private final Collection<Entry> commands = new CopyOnWriteArrayList<Entry> ();

    private Timer timer;

    private final int loopDelay;

    public CommandQueueImpl ( final Hive hive, final String id, final int loopDelay )
    {
        this.hive = hive;
        this.id = id;
        this.loopDelay = loopDelay;
    }

    public void addCommand ( final SingleCommand command, final int period )
    {
        this.commands.add ( new Entry ( command, period ) );
    }

    public void removeCommand ( final SingleCommand command )
    {
        for ( final Iterator<Entry> i = this.commands.iterator (); i.hasNext (); )
        {
            final Entry entry = i.next ();
            if ( entry.getCommand () == command )
            {
                i.remove ();
                return;
            }
        }
    }

    public void start ( final Hive hive, final FolderCommon baseFolder )
    {
        for ( final Entry entry : this.commands )
        {
            entry.getCommand ().register ( hive, baseFolder );
        }

        this.timer = new Timer ( true );
        this.timer.scheduleAtFixedRate ( new TimerTask () {

            @Override
            public void run ()
            {
                runOnce ();
            }
        }, new Date (), this.loopDelay );

    }

    public void stop ()
    {
        this.timer.cancel ();
        this.timer = null;

        for ( final Entry entry : this.commands )
        {
            entry.getCommand ().unregister ();
        }
    }

    protected void runOnce ()
    {
        for ( final Entry entry : this.commands )
        {
            if ( entry.canExecute () )
            {
                entry.execute ();
            }
        }
    }

}
