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

package org.openscada.ae.client.test.impl;

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

public class StorageRepository extends Observable
{
    private List<StorageConnection> _connections = new ArrayList<StorageConnection>();
    
    public StorageRepository ()
    {
    }
    
    synchronized public void load ( IPath path )
    {
        _connections.clear();
        
        File file = path.toFile();
        XMLDecoder decoder = null;
        try
        {
            decoder = new XMLDecoder(new FileInputStream(file));
            while ( true )
            {
                try
                {
                    Object o = decoder.readObject();
                    if ( !(o instanceof StorageConnectionInformation) )
                        continue;
                    _connections.add( new StorageConnection((StorageConnectionInformation)o) );
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
            e.printStackTrace();
        }
        finally
        {
            if ( decoder != null )
                decoder.close();
        }
    }
    
    synchronized public void save ( IPath path )
    {
        File file = path.toFile();
        XMLEncoder encoder = null;
        
        try
        {
            encoder = new XMLEncoder(new FileOutputStream(file));
            for ( StorageConnection connection : _connections )
            {
                encoder.writeObject(connection.getConnectionInformation());
            }
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if ( encoder != null )
                encoder.close();
        }
    }

    public void addConnection ( StorageConnection connection )
    {
        _connections.add ( connection );
        
        setChanged();
        notifyObservers();
    }
    
    public List<StorageConnection> getConnections ()
    {
        return _connections;
    }
}
