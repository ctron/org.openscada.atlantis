/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.storage.syslog.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.storage.syslog.DataStore;

public abstract class FileProviderBase implements Runnable
{
    private static Logger _log = Logger.getLogger ( FileProviderBase.class );

    private DataStore _storage = null;

    private File _file = null;

    private final Thread _thread = new Thread ( this );

    public FileProviderBase ( final DataStore storage, final File file )
    {
        super ();
        this._storage = storage;
        this._file = file;

        this._thread.setDaemon ( true );
        this._thread.start ();
    }

    public void run ()
    {
        while ( true )
        {
            try
            {
                runOnce ();
            }
            catch ( final Exception e )
            {
                _log.debug ( "read failed", e );
            }
            try
            {
                Thread.sleep ( 1 * 1000 );
            }
            catch ( final InterruptedException e )
            {
                _log.debug ( "sleep failed", e );
            }
        }
    }

    public void runOnce () throws Exception
    {
        FileReader fileReader;

        fileReader = new FileReader ( this._file );
        final BufferedReader bufferedReader = new BufferedReader ( fileReader );

        while ( true )
        {
            String line;
            while ( ( line = bufferedReader.readLine () ) != null )
            {
                handleLine ( line );
            }
            Thread.sleep ( 100 );
        }
    }

    protected abstract void handleLine ( String line );

    protected void submitEvent ( final Event event )
    {
        if ( this._storage != null )
        {
            this._storage.submitEvent ( event );
        }
    }
}
