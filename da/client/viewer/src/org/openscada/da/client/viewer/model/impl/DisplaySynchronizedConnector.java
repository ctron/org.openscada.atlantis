/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.viewer.model.Type;

/**
 * A passthrough connector which will set the input side from inside the GUI loop
 * @author Jens Reimann
 *
 */
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
            _log.debug ( "Perform synchronized update" );
            super.update ( type, value );    
        }
        catch ( Exception e ) 
        {
            _log.warn ( "Unable to perform update", e );
        }
    }
}
