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

package org.openscada.da.server.opc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.opc.configuration.ConfigurationType;
import org.openscada.da.opc.configuration.InitialItemType;
import org.openscada.da.opc.configuration.InitialItemsType;
import org.openscada.da.opc.configuration.ItemsDocument;
import org.openscada.da.opc.configuration.RootDocument;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.w3c.dom.Node;

public class XMLConfigurator
{
    
    private RootDocument _rootDocument = null;

    public XMLConfigurator ( RootDocument rootDocument )
    {
        _rootDocument  = rootDocument;
    }
    
    public XMLConfigurator ( Node node ) throws XmlException
    {
        this ( RootDocument.Factory.parse ( node ) );
    }
    
    public XMLConfigurator ( String filename ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( filename ) ) );
    }
    
    public void configure ( Hive hive ) throws ConfigurationError
    {
        // first configure the base hive
        new org.openscada.da.server.common.configuration.xml.XMLConfigurator ( null, _rootDocument.getRoot ().getItemTemplates (), null, null ).configure ( hive );
        
        // now configure the opc hive
        for ( ConfigurationType configuration : _rootDocument.getRoot ().getConnections ().getConfigurationList () )
        {
            if ( !configuration.getEnabled () )
            {
                continue;
            }
            
            ConnectionInformation ci = new ConnectionInformation ();
            ci.setUser ( configuration.getUser () );
            ci.setPassword ( configuration.getPassword () );
            ci.setDomain ( configuration.getDomain () );
            ci.setHost ( configuration.getHost () );
            ci.setClsid ( configuration.getClsid () );
            ci.setProgId ( configuration.getProgid () );
            
            ConnectionSetup setup = new ConnectionSetup ( ci );
            
            String access = configuration.getAccess ();
            if ( access.equalsIgnoreCase ( "sync" ) )
            {
                setup.setAccessMethod ( AccessMethod.SYNC );
            }
            else if ( access.equalsIgnoreCase ( "async" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }
            else if ( access.equalsIgnoreCase ( "async20" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }
            
            setup.setFlatBrowser ( configuration.getFlatBrowser () );
            setup.setTreeBrowser ( configuration.getTreeBrowser () );
            
            setup.setRefreshTimeout ( configuration.getRefresh () );
            setup.setInitialConnect ( configuration.getInitialRefresh () );
            

            Set<String> initialItemSet = new HashSet<String> ();
            
            // load the item source file if it is set
            if ( configuration.isSetInitialItemResource () )
            {
                initialItemSet.addAll ( loadInitialItemResource ( configuration.getInitialItemResource () ) );
            }
            
            // add all initial items anyway
            initialItemSet.addAll ( configuration.getInitialItemList () );
            
            hive.addConnection ( setup, configuration.getAlias (), configuration.getConnected (), initialItemSet );
        }
    }

    /**
     * Emulate the xml item source
     * @param initialItemResource the item source file name
     * @return the list of initial items
     */
    private Collection<String> loadInitialItemResource ( String initialItemResource )
    {
        Set<String> result = new HashSet<String> ();
        
        try
        {
            File file = new File ( initialItemResource );
            InitialItemsType items = ItemsDocument.Factory.parse ( file ).getItems ();
            for ( InitialItemType item : items.getItemList () )
            {
                result.add ( item.getId () );
            }
        }
        catch ( Throwable e )
        {
        }
        
        return result;
    }
}
