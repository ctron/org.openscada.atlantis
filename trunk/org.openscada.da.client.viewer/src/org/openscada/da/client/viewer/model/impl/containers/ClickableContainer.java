package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.widgets.Canvas;

public abstract class ClickableContainer extends FigureContainer
{
    /**
     * Called by the {@link ClickableContainer} when a click event occurs.
     * @param event The action event
     */
    protected abstract void clicked ( ActionEvent event );

    private org.eclipse.draw2d.Clickable _clickable = null;

    public ClickableContainer ( String id )
    {
        super ( id );
    }

    @Override
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _clickable == null )
        {
            _canvas = canvas;
            _clickable = new org.eclipse.draw2d.Clickable ();
            _clickable.setCursor ( Cursors.CROSS );
            _clickable.setRolloverEnabled ( true );
            _clickable.setSelected ( false );
            _clickable.setLayoutManager ( new XYLayout () );
            _clickable.setRequestFocusEnabled ( false );
            _clickable.getModel ().addActionListener ( new ActionListener () {
    
                public void actionPerformed ( ActionEvent event )
                {
                    clicked ( event );
                }} );
            
            parent.add ( _clickable );
            _figure = _clickable;
            createChildren ( _figure );
            update ();
        }
    }

}