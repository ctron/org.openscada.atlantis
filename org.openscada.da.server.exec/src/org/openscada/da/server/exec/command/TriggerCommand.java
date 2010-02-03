/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.DataItemCommand.Listener;
import org.openscada.da.server.exec.Hive;
import org.openscada.da.server.exec.StatusCodes;
import org.openscada.da.server.exec.extractor.Extractor;
import org.openscada.utils.statuscodes.CodedRuntimeException;

public class TriggerCommand extends AbstractSingleCommand implements Listener, ProcessListener
{

    /**
     * A string which will be replaced in the arguments with the write request value
     */
    private final String argumentPlaceholder;

    /**
     * If this flag is <code>true</code> and the written value is <code>null</code>
     * then arguments containing the placeholder will be removed completely.
     */
    private final boolean skipIfNull;

    private DataItemCommand startItem;

    private final boolean fork;

    private final AtomicBoolean running = new AtomicBoolean ( false );

    private volatile Process currentProcess;

    private DataItemCommand killItem;

    public TriggerCommand ( final String id, final ProcessConfiguration processConfiguration, final Collection<Extractor> extractors, final String argumentPlaceholder, final boolean skipIfNull, final boolean fork )
    {
        super ( id, processConfiguration, extractors );
        this.argumentPlaceholder = argumentPlaceholder;
        this.skipIfNull = skipIfNull;
        this.fork = fork;
    }

    @Override
    public void register ( final Hive hive, final FolderCommon parentFolder )
    {
        super.register ( hive, parentFolder );
        this.startItem = getItemFactory ().createCommand ( "start" );
        this.startItem.addListener ( this );

        this.killItem = getItemFactory ().createCommand ( "kill" );
        this.killItem.addListener ( new Listener () {

            public void command ( final Variant value ) throws Exception
            {
                TriggerCommand.this.kill ();
            }
        } );

    }

    public void command ( final Variant value ) throws Exception
    {
        if ( !this.running.compareAndSet ( false, true ) )
        {
            throw new CodedRuntimeException ( StatusCodes.TRIGGER_RUNNING, "Operation is already running" );
            // the running flag will be reset by the process listener
        }

        start ( value );
    }

    private void start ( final Variant value )
    {
        final ProcessBuilder pb = mergeValue ( this.processConfiguration.asProcessBuilder (), value );

        if ( this.fork )
        {
            // do the fork
            final Thread thread = new Thread ( new Runnable () {

                public void run ()
                {
                    TriggerCommand.this.execute ( pb, TriggerCommand.this );
                }
            } );
            thread.start ();
        }
        else
        {
            // just call
            execute ( pb, this );
        }
    }

    private ProcessBuilder mergeValue ( final ProcessBuilder pb, final Variant value )
    {
        if ( this.argumentPlaceholder == null )
        {
            return pb;
        }

        final List<String> command = pb.command ();
        final List<String> newCommand = new ArrayList<String> ();

        for ( int i = 0; i < command.size (); i++ )
        {
            String entry = command.get ( i );

            /*
             *  the first entry (0) is special ... it is the command name. we will not
             *  interfere with it
             */
            if ( i != 0 )
            {
                // the entry contains the placeholder string
                if ( entry.contains ( this.argumentPlaceholder ) )
                {
                    if ( value.isNull () && this.skipIfNull )
                    {
                        // nuke that entry
                        entry = null;
                    }
                    else
                    {
                        // replace the placeholder with the actual value
                        final String str = value.asString ( "" );
                        entry = entry.replace ( this.argumentPlaceholder, str );
                    }
                }
            }

            // if we still have the entry append it
            if ( entry != null )
            {
                newCommand.add ( entry );
            }
        }

        // update the command list
        pb.command ( newCommand );
        return pb;
    }

    protected void kill ()
    {
        final Process p = this.currentProcess;
        if ( p == null )
        {
            // nothing to do
            return;
        }
        p.destroy ();
    }

    public void processCompleted ()
    {
        this.running.set ( false );
        this.currentProcess = null;
    }

    public void processCreated ( final Process process )
    {
        this.currentProcess = process;
    }
}
