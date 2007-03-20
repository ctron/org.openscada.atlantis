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

package org.openscada.da.client.viewer.model.impl.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Canvas;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Label extends BoundsFigure
{
    private static Logger _log = Logger.getLogger ( Label.class );
    
    private org.eclipse.draw2d.Label _label = null;
    private String _format = null;
    private String _displayText = null;
    private Object _text = null;
    
    public Label ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "object" ) );
        addInput ( new PropertyInput ( this, "text" ) );
    }

    @Override
    protected void update ()
    {
        if ( _label == null )
            return;
        
        updateFigure ( _label );
    }

    protected synchronized void updateDisplayText ()
    {
        if ( _text == null )
            _displayText = null;
        else if ( _format == null )
            _displayText = _text.toString ();
        else
        {
            try
            {
                _displayText = String.format ( _format, _text );
            }
            catch ( Exception e )
            {
                _displayText = e.getMessage ();
            }
        }
        
        update ();
    }
    
    protected synchronized String getDisplayText ()
    {
        return _displayText;
    }
    
    protected void updateFigure ( org.eclipse.draw2d.Label label )
    {
        if ( _displayText == null )
        {
            label.setVisible ( false );
            label.setText ( "" );
        }
        else
        {
            super.updateFigure ( label );
            label.setVisible ( true );
            label.setText ( _displayText );
        }
    }

    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _label == null )
        {
            _label = new org.eclipse.draw2d.Label ();
            parent.add ( _label );
            update ();
        }
    }
    
    @Override
    public void dispose ()
    {
        if ( _label != null )
        {
            _label.getParent ().remove ( _label );
            _label = null;
        }
        super.dispose ();
    }

    public synchronized void setText ( String text )
    {
        setObject ( (Object)text );
    }
    
    public synchronized void setObject ( Object text )
    {
        _log.debug ( "Set Object: " + text );
        _text = text;
        updateDisplayText ();
    }

    public String getFormat ()
    {
        return _format;
    }

    public synchronized void setFormat ( String format )
    {
        _format = format;
        setObject ( _text );
    }

}
