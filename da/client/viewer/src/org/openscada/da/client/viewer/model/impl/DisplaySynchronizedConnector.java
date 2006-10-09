package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.viewer.model.Type;

public class DisplaySynchronizedConnector extends PassThroughConnector
{
    private static Logger _log = Logger.getLogger ( DisplaySynchronizedConnector.class );
    
    private Display _display = null;
    
    public DisplaySynchronizedConnector ()
    {
        this ( Display.getDefault () );
    }
    
    public DisplaySynchronizedConnector ( Display display )
    {
        super ();
        _display = display;
    }
    
    @Override
    public void update ( final Type type, final Object value )
    {
        if ( !_display.isDisposed () )
        {
            _display.asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !_display.isDisposed () )
                        performUpdate ( type, value );        
                }} );
        }
        
    }
    
    protected void performUpdate ( Type type, Object value )
    {
        try
        {
            super.update ( type, value );    
        }
        catch ( Exception e ) 
        {
            _log.warn ( "Unable to perform update", e );
        }
    }
}
