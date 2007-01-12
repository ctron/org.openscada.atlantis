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

package org.openscada.da.client.test.impl;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.core.runtime.IPath;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.da.client.test.config.HiveConnectionInformation;

public class HiveRepository extends Observable
{
    private List<HiveConnection> _connections = new ArrayList<HiveConnection> ();

    public HiveRepository ()
    {
    }

    synchronized public void load ( IPath path )
    {
        _connections.clear ();

        File file = path.toFile ();
        XMLDecoder decoder = null;
        try
        {
            decoder = new XMLDecoder ( new FileInputStream ( file ) );
            while ( true )
            {
                try
                {
                    Object o = decoder.readObject ();
                    if ( ! ( o instanceof HiveConnectionInformation ) )
                        continue;
                    _connections.add ( new HiveConnection ( (HiveConnectionInformation)o ) );
                }
                catch ( ArrayIndexOutOfBoundsException e )
                {
                    break;
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        finally
        {
            if ( decoder != null )
                decoder.close ();
        }
    }

    synchronized public void save ( IPath path )
    {
        File file = path.toFile ();
        XMLEncoder encoder = null;

        try
        {
            encoder = new XMLEncoder ( new FileOutputStream ( file ) );
            for ( HiveConnection connection : _connections )
            {
                encoder.writeObject ( new HiveConnectionInformation ( connection.getConnectionInformation ().toString () ) );
            }
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        finally
        {
            if ( encoder != null )
                encoder.close ();
        }
    }

    public void addConnection ( HiveConnection connection )
    {
        _connections.add ( connection );

        setChanged ();
        notifyObservers ();
    }

    public List<HiveConnection> getConnections ()
    {
        return _connections;
    }

    public HiveConnection findConnection ( ConnectionInformation connectionInformation )
    {
        for ( HiveConnection connection : _connections )
        {
            if ( connection.getConnectionInformation ().equals ( connectionInformation ) )
                return connection;
        }
        return null;
    }
}
