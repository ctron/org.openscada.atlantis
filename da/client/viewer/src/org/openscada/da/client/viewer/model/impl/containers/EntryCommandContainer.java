package org.openscada.da.client.viewer.model.impl.containers;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.geometry.Point;
import org.openscada.da.client.viewer.model.impl.VariantSetterOutput;

/**
 * A clickable container that open a new popup dialog with a text entry that acts
 * as a output
 * @author Jens Reimann
 *
 */
public class EntryCommandContainer extends ClickableContainer
{
    private static Logger _log = Logger.getLogger ( EntryCommandContainer.class );

    private VariantSetterOutput _output;

    public EntryCommandContainer ( String id )
    {
        super ( id );
        addOutput ( _output = new VariantSetterOutput ( "value" ) );
    }

    @Override
    protected void clicked ( ActionEvent event )
    {
        try
        {
            Point p = getBounds ().getCenter ();
            _figure.translateToAbsolute ( p );

            new EntryMessageDialog ( _canvas.getShell (), _output, _canvas.toDisplay ( p.getSWTPoint () ) ).open ();
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to show dialog", e );
        }
    }

}
