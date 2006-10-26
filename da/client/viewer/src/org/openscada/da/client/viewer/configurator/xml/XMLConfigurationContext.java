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

package org.openscada.da.client.viewer.configurator.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.client.viewer.model.ConnectorFactory;
import org.openscada.da.client.viewer.model.ContainerFactory;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.openscada.da.viewer.RootDocument;

public class XMLConfigurationContext
{
    private RootDocument _document = null;
    
    private Map<String, ContainerFactory> _containerFactories = new HashMap<String, ContainerFactory> ();
    private Map<String, ConnectorFactory> _connectorFactories = new HashMap<String, ConnectorFactory> ();
    private Map<String, ObjectFactory> _objectFactories = new HashMap<String, ObjectFactory> ();
   
    public Map<String, ConnectorFactory> getConnectorFactories ()
    {
        return _connectorFactories;
    }

    public void setConnectorFactories ( Map<String, ConnectorFactory> connectorFactories )
    {
        _connectorFactories = connectorFactories;
    }

    public RootDocument getDocument ()
    {
        return _document;
    }

    public void setDocument ( RootDocument document )
    {
        _document = document;
    }

    public Map<String, ObjectFactory> getObjectFactories ()
    {
        return _objectFactories;
    }

    public void setObjectFactories ( Map<String, ObjectFactory> objectFactories )
    {
        _objectFactories = objectFactories;
    }

    public Map<String, ContainerFactory> getContainerFactories ()
    {
        return _containerFactories;
    }

    public void setContainerFactories ( Map<String, ContainerFactory> containerFactories )
    {
        _containerFactories = containerFactories;
    }
    
}
