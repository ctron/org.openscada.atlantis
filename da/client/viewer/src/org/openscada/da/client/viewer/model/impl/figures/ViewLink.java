package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.IFigure;
import org.openscada.da.client.viewer.Activator;

public class ViewLink extends BaseFigure
{
    private IFigure _parent = null;
    private Button _button = null;
    
    private String _viewId = "";
    private String _text = "";
    
    public ViewLink ( String id )
    {
        super ( id );
    }
    
    @Override
    protected void update ()
    {
        if ( _button != null )
        {
            updateFigure ( _button );
        }
    }

    public void createFigure ( IFigure parent )
    {
        if ( _button == null )
        {
            _button = new Button ( _text );
            _button.addActionListener ( new ActionListener () {

                public void actionPerformed ( ActionEvent event )
                {
                    openLink ();
                }} );
            _parent = parent;
            _parent.add ( _button );
            update ();
        }
    }
    
    @Override
    public void dispose ()
    {
        if ( _button != null )
        {
            _parent.remove ( _button );
            _button = null;
        }
        super.dispose ();
    }
    
    protected void openLink ()
    {
        if ( _viewId.length () <= 0 )
        {
            return;
        }
        Activator.getDefault ().openProcessView ( _viewId );
    }

    public String getViewId ()
    {
        return _viewId;
    }

    public void setViewId ( String viewId )
    {
        _viewId = viewId;
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
