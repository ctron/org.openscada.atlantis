/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exporter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.w3c.dom.Node;

public class HiveExport
{
    private static Logger _log = Logger.getLogger ( HiveExport.class );
    
    private Hive _hive = null;
    private List<Export> _exports = new LinkedList<Export> ();
    
    public HiveExport ( String hiveClass, Node node ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        super ();
        _hive = createInstance ( hiveClass, node );
    }
    
    protected static Hive createInstance ( String hiveClassName, Node node ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class hiveClass = Class.forName ( hiveClassName );
       
        Constructor ctor = null;
        
        if ( node != null )
        {
            try
            {
                ctor = hiveClass.getConstructor ( new Class[]{ Node.class } );
            }
            catch ( Exception e )
            {
            }
        }

        if ( ctor != null)
        {
            _log.debug ( "Using XML-Node constructor" );
            return (Hive)ctor.newInstance ( new Object [] { node } );
        }
        
        return (Hive)hiveClass.newInstance ();
    }
    
    public synchronized void start ()
    {
        _log.info ( String.format ( "Starting hive: %s", _hive ) );
        
        for ( Export export : _exports )
        {
            try
            {
                export.start ();
            }
            catch ( Exception e )
            {
                _log.error ( "Failed to start export", e );
            }
        }
    }
    
    public synchronized void stop ()
    {
        _log.info ( String.format ( "Stopping hive: %s", _hive ) );
        
        for ( Export export : _exports )
        {
            try
            {
                export.stop ();
            }
            catch ( Exception e )
            {
                _log.error ( "Failed to stop export", e );
            }
        }
    }

    public Export addExport ( ExportType exportType ) throws ConfigurationError
    {
        ConnectionInformation ci = ConnectionInformation.fromURI ( exportType.getUri () );
        Export export = findExport ( ci );
        
        if ( export != null )
        {
            _exports.add ( export );
        }
        
        return export;
    }
    
    protected Export findExport ( ConnectionInformation ci ) throws ConfigurationError
    {
        if ( !ci.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            throw new ConfigurationError ( String.format ( "Interface must be 'da' but is '%s'", ci.getInterface () ) );
        }
        
        if ( ci.getDriver ().equalsIgnoreCase ( "net" ) || ci.getDriver ().equalsIgnoreCase ( "gmpp" ) )
        {
            return new NetExport ( _hive, ci );
        }
        else if ( ci.getDriver ().equalsIgnoreCase ( "ice" ) )
        {
            return new IceExport ( _hive, ci );
        }
        else
        {
            throw new ConfigurationError ( String.format ( "Driver '%s' is unknown", ci.getDriver () ) );
        }
    }
}
