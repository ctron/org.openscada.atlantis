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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.exporter.ConfigurationDocument;
import org.openscada.da.server.exporter.ConfigurationType;
import org.openscada.da.server.exporter.HiveType;
import org.w3c.dom.Node;

public class Controller
{
    private static Logger _log = Logger.getLogger ( Controller.class );
    private List<HiveExport> _hives = new LinkedList<HiveExport> ();
    
    public Controller ( ConfigurationDocument configurationDocument )
    {
        super ();
        configure ( configurationDocument );
    }
    
    public Controller ( String file ) throws XmlException, IOException
    {
        this ( new File ( file ) );
    }
    
    public Controller ( File file ) throws XmlException, IOException
    {
        this ( ConfigurationDocument.Factory.parse ( file ) );
    }
    
    public void configure ( ConfigurationDocument configurationDocument )
    {
        ConfigurationType configuration = configurationDocument.getConfiguration ();
        
        for ( HiveType hive : configuration.getHiveList () )
        {
            HiveExport hiveExport = null;
            try
            {
                HiveConfigurationType hc = hive.getConfiguration ();
                Node subNode = null;
                if ( hc != null )
                {
                    for ( int i = 0; i < hc.getDomNode ().getChildNodes ().getLength (); i++ )
                    {
                        Node node = hc.getDomNode ().getChildNodes ().item ( i ); 
                        if ( node.getNodeType () == Node.ELEMENT_NODE )
                        {
                            subNode = node;
                        }
                    }
                }
                hiveExport = new HiveExport ( hive.getClass1 (), subNode );
            }
            catch ( ClassNotFoundException e )
            {
                _log.error ( String.format ( "Unable to find hive class: %s", hive.getClass1 () ), e );
            }
            catch ( InstantiationException e )
            {
                _log.error ( String.format ( "Unable to create hive instance" ), e );
            }
            catch ( IllegalAccessException e )
            {
                _log.error ( String.format ( "Unable to create hive instance" ), e );
            }
            catch ( IllegalArgumentException e )
            {
                _log.error ( String.format ( "Unable to create hive instance" ), e );
            }
            catch ( InvocationTargetException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if ( hiveExport != null )
            {
                for ( ExportType export : hive.getExportList () )
                {
                    try
                    {
                        _log.debug ( String.format ( "Adding export: %s", export.getUri () ) );
                        
                        hiveExport.addExport ( export );
                    }
                    catch ( ConfigurationError e )
                    {
                        _log.error ( String.format ( "Unable to configure export (%s) for hive (%s)", hive.getClass1 (), export.getUri () ) );
                    }
                }
                _hives.add ( hiveExport );
            }
        }
    }
    
    public synchronized void start ()
    {
        for ( HiveExport hive : _hives )
        {
            hive.start ();
        }
    }
    
    public synchronized void stop ()
    {
        for ( HiveExport hive : _hives )
        {
            hive.stop ();
        }
    }
}
