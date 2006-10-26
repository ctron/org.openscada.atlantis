package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;

public class Label extends BaseFigure
{
    private org.eclipse.draw2d.Label _label = null;
    private String _text = null;
    
    public Label ( String id )
    {
        super ( id );
    }

    @Override
    protected void update ()
    {
        if ( _label == null )
            return;
        
        updateFigure ( _label );
    }

    protected void updateFigure ( org.eclipse.draw2d.Label label )
    {
        if ( _text == null )
        {
            label.setVisible ( false );
            label.setText ( "" );
        }
        else
        {
            super.updateFigure ( label );
            label.setVisible ( true );
            label.setText ( _text );
        }
    }

    public void createFigure ( IFigure parent )
    {
        if ( _label == null )
        {
            _label = new org.eclipse.draw2d.Label ();
            parent.add ( _label );
            update ();
        }
    }

    public String getText ()
    {
        return _text;
    }

    public void setText ( String text )
    {
        _text = text;
    }

}
