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
