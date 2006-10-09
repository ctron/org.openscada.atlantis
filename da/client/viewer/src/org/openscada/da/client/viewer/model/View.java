package org.openscada.da.client.viewer.model;

import java.util.List;

public interface View
{
    public abstract List<DynamicObject> getObjects ();
    public abstract List<Connector> getConnectors ();
}
