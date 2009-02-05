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

    private File _file;

    private String _severity;

    public SyslogFileProvider ( DataStore storage, File file, String severity )
    {
        super ( storage, file );
        _storage = storage;
        _file = file;
        _severity = severity;
    }

    @Override
    protected void handleLine ( String line )
    {
        if ( _parser != null )
            _parser.handleLine ( line );
        else
        {
            synchronized ( this )
            {
                if ( _parser == null )
                    _parser = new SyslogParser ( _storage, _file.getAbsolutePath (), _severity );
                _parser.handleLine ( line );
            }
        }
    }
    
   
}
