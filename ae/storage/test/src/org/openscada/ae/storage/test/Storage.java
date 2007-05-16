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

package org.openscada.ae.storage.test;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.impl.StorageCommon;
import org.openscada.ae.storage.common.memory.MemoryQuery;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class Storage extends StorageCommon implements Runnable
{
    private static Logger _log = Logger.getLogger ( Storage.class );
    
    private MemoryQuery _allQuery = new MemoryQuery ();
    
    private Thread _generator = new Thread ( this );
    
    public Storage ()
    {
        super ();
        addQuery ( new QueryDescription ( "all", new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A query containing all items" ) )
                .getMap () ), _allQuery );
        
        _generator.setDaemon ( true );
        _generator.start ();
    }

    public void generateEvent ()
    {
        long ts = System.currentTimeMillis ();
        
        Event event = new Event ( toString () + "." + ts );
        event.getAttributes ().put ( "test", new Variant ( "Just a test" ) );
        event.getAttributes ().put ( "ts", new Variant ( ts ) );
        
        try
        {
            submitEvent ( new Properties (), event );
        }
        catch ( Exception e )
        {
            _log.warn ( "Unable to generate event", e );
        }
    }
    
    public void run ()
    {
        while ( true )
        {
            try
            {
                Thread.sleep ( 2 * 1000 );
                generateEvent ();
            }
            catch ( InterruptedException e )
            {
                _log.warn ( "Interrupted", e );
                return;
            }
        }
    }
}
