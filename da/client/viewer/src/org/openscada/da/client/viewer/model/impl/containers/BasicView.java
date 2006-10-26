package org.openscada.da.client.viewer.model.impl.containers;

import java.util.LinkedList;
import java.util.List;

import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.View;

public class BasicView extends FigureContainer implements View
{
    private List<Connector> _connectors = new LinkedList<Connector> ();
    
    public BasicView ( String id )
    {
        super ( id );
    }
}
