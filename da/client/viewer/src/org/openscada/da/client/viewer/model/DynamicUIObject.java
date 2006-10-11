package org.openscada.da.client.viewer.model;

import org.eclipse.draw2d.IFigure;

public interface DynamicUIObject extends DynamicObject
{
    public abstract IFigure getFigure ();
    public abstract void dispose ();
}
