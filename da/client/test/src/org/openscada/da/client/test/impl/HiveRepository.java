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
import org.openscada.da.client.test.config.HiveConnectionInformation;

public class HiveRepository extends Observable
{
    private List<HiveConnection> _connections = new ArrayList<HiveConnection>();
    
    public HiveRepository ()
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
                    if ( !(o instanceof HiveConnectionInformation) )
                        continue;
                    _connections.add( new HiveConnection((HiveConnectionInformation)o) );
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
            for ( HiveConnection connection : _connections )
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

    public void addConnection ( HiveConnection connection )
    {
        _connections.add ( connection );
        
        setChanged();
        notifyObservers();
    }
    
    public List<HiveConnection> getConnections ()
    {
        return _connections;
    }
}
