package org.openscada.da.server.exec2.command;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.exec2.Hive;

public class CommandQueueImpl implements CommandQueue
{

    private static class Entry
    {
        private SingleCommand command;

        private int period;

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

        public void setCommand ( final SingleCommand command )
        {
            this.command = command;
        }

        public int getPeriod ()
        {
            return this.period;
        }

        public void setPeriod ( final int period )
        {
            this.period = period;
        }

        public long getLastTimestamp ()
        {
            return this.lastTimestamp;
        }

        public void setLastTimestamp ( final long lastTimestamp )
        {
            this.lastTimestamp = lastTimestamp;
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
        for ( Iterator<Entry> i = this.commands.iterator (); i.hasNext (); )
        {
            Entry entry = i.next ();
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
