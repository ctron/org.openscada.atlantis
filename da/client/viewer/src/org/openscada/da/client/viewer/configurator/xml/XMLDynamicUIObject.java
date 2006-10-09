package org.openscada.da.client.viewer.configurator.xml;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.View;

public class XMLDynamicUIObject extends XMLDynamicObject implements DynamicUIObject
{
    public XMLDynamicUIObject ( View view )
    {
        super ( view );
    }

    public IFigure createFigure ()
    {
        Figure figure = new Figure ();
        
        for ( DynamicObject object : getView ().getObjects () )
        {
            if ( object instanceof DynamicUIObject )
            {
                figure.add ( ((DynamicUIObject)object).createFigure () );
            }
        }
        
        return figure;
    }

    public void dispose ()
    {
        for ( DynamicObject object : getView ().getObjects () )
        {
            if ( object instanceof DynamicUIObject )
            {
                ((DynamicUIObject)object).dispose ();
            }
        }
    }
}
