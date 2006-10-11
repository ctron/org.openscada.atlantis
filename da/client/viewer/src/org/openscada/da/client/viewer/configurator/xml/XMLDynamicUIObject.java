package org.openscada.da.client.viewer.configurator.xml;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.View;

public class XMLDynamicUIObject extends XMLDynamicObject implements DynamicUIObject
{
    private static Logger _log = Logger.getLogger ( XMLDynamicUIObject.class );
    
    public XMLDynamicUIObject ( View view )
    {
        super ( view );
    }

    public IFigure createFigure ()
    {
        Figure figure = new Figure ();
        figure.setLayoutManager ( new XYLayout () );
        figure.setBounds ( new Rectangle ( 0, 0, 1000, 1000 ) );
        figure.setBackgroundColor ( ColorConstants.red );
        
        _log.debug ( String.format ( "%d sub-items", getView ().getObjects ().size () ) );
        
        for ( DynamicObject object : getView ().getObjects () )
        {
            if ( object instanceof DynamicUIObject )
            {
                _log.debug ( "UI object " );
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
