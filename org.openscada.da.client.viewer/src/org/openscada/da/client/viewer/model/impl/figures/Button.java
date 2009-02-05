package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Canvas;

public abstract class Button extends BoundsFigure
{
    private org.eclipse.draw2d.Button _button = null;
    private String _text = "";

    public Button ( String id )
    {
        super ( id );
    }

    @Override
    public void dispose ()
    {
        if ( _button != null )
        {
            _button.getParent ().remove ( _button );
            _button = null;
        }
        super.dispose ();
    }
    
    @Override
    protected void update ()
    {
        if ( _button != null )
        {
            updateFigure ( _button );
        }
    }
    
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _button == null )
        {
            _button = new org.eclipse.draw2d.Button ( _text );
            parent.add ( _button );
            setupButton ( _button );
            update ();
        }
    }
    
    /**
     * Must be implemented by dereived classes to setup up the draw2d button
     * @param button the button to set up
     */
    protected abstract void setupButton ( org.eclipse.draw2d.Button button );

    public String getText ()
    {
        return _text;
    }

    public void setText ( String text )
    {
        _text = text;
    }

}