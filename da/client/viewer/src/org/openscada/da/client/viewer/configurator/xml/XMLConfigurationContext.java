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
