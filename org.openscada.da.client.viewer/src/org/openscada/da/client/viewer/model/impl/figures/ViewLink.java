package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.openscada.da.client.viewer.Activator;

public class ViewLink extends Button
{
    private String _viewId = "";
    
    public ViewLink ( String id )
    {
        super ( id );
    }

    /**
     * Set up the button to open a view link
     * @param button the button to set up
     */
    @Override
    protected void setupButton ( org.eclipse.draw2d.Button button )
    {
        button.addActionListener ( new ActionListener () {

            public void actionPerformed ( ActionEvent event )
            {
                openLink ();
            }} );
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

}
