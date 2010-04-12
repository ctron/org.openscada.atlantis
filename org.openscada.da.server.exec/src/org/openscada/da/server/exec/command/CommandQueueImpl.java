/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
