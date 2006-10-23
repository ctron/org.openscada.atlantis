package org.openscada.da.client.viewer.configurator.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.client.viewer.model.DynamicObject;

public class XMLContainerContext
{
    
    private XMLConfigurationContext _configurationContext = null;
    private Map<String, DynamicObject> _objects = new HashMap<String, DynamicObject> ();

    public XMLContainerContext ( XMLConfigurationContext configurationContext)
    {
        _configurationContext = configurationContext;
    }
    
    public Map<String, DynamicObject> getObjects ()
    {
        return _objects;
    }

    public void setObjects ( Map<String, DynamicObject> objects )
    {
        _objects = objects;
    }

    public XMLConfigurationContext getConfigurationContext ()
    {
        return _configurationContext;
    }

}
