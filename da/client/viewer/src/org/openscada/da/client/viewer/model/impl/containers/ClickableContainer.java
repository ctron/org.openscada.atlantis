package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class ClickableContainer extends FigureContainer
{

    private IFigure _figure = null;

    protected abstract void clicked ( ActionEvent event );

    private org.eclipse.draw2d.Clickable _clickable = null;

    public ClickableContainer ( String id )
    {
        super ( id );
    }

    @Override
    protected void update ()
    {
        if ( _clickable != null )
        {
            _clickable.getParent ().setConstraint ( _clickable, new Rectangle ( 0, 0, -1, -1 ) );
        }
        this.update ();
    }

    @Override
    public void createFigure ( IFigure parent )
    {
        if ( _figure == null )
        {
            _clickable = new org.eclipse.draw2d.Clickable ();
            _clickable.setCursor ( Cursors.CROSS );
            _clickable.setRolloverEnabled ( true );
            _clickable.setSelected ( false );
            _clickable.setLayoutManager ( new XYLayout () );
            _clickable.getModel ().addActionListener ( new ActionListener () {
    
                public void actionPerformed ( ActionEvent event )
                {
                    clicked ( event );
                }} );
            
            parent.add ( _clickable );
            this.createFigure ( _clickable );
        }
    }

}