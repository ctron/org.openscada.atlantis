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

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.NotConnectedException;
import org.openscada.da.client.viewer.model.Type;

public class InputExport implements InputDefinition
{
    private InputDefinition _input = null;
    private String _alias = null;
    
    public InputExport ( InputDefinition input, String alias )
    {
        _input = input;
        _alias = alias;
    }

    public void connect ( Connector connector ) throws AlreadyConnectedException
    {
        _input.connect ( connector );
    }

    public void disconnect ( Connector connector ) throws NotConnectedException
    {
        _input.disconnect ( connector );
    }

    public String getName ()
    {
        return _alias;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return _input.getSupportedTypes ();
    }

    public void update ( Type type, Object value )
    {
        _input.update ( type, value );
    }

}
