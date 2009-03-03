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

import java.io.File;

import org.apache.log4j.Logger;
import org.openscada.ae.storage.syslog.DataStore;

public class SyslogFileProvider extends FileProviderBase
{
    private static Logger _log = Logger.getLogger ( SyslogFileProvider.class );

    private SyslogParser _parser = null;

    private DataStore _storage = null;

    private final File _file;

    private final String _severity;

    public SyslogFileProvider ( final DataStore storage, final File file, final String severity )
    {
        super ( storage, file );
        this._storage = storage;
        this._file = file;
        this._severity = severity;
    }

    @Override
    protected void handleLine ( final String line )
    {
        if ( this._parser != null )
        {
            this._parser.handleLine ( line );
        }
        else
        {
            synchronized ( this )
            {
                if ( this._parser == null )
                {
                    this._parser = new SyslogParser ( this._storage, this._file.getAbsolutePath (), this._severity );
                }
                this._parser.handleLine ( line );
            }
        }
    }

}
