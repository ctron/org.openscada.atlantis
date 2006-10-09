package org.openscada.da.client.viewer.model.impl;

import java.util.LinkedList;
import java.util.List;

import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.DynamicObject;

public class View implements org.openscada.da.client.viewer.model.View
{

    private List<Connector> _connectors = new LinkedList<Connector> ();
    private List<DynamicObject> _objects = new LinkedList<DynamicObject> ();
    
    public List<DynamicObject> getObjects ()
    {
        return _objects;
    }

    public void setObjects ( List<DynamicObject> objects )
    {
        _objects = objects;
    }

    public List<Connector> getConnectors ()
    {
        return _connectors;
    }

    public void setConnectors ( List<Connector> connectors )
    {
        _connectors = connectors;
    }

}
