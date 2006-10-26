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

package org.openscada.da.client.viewer.model.impl.containers;

import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;

class CommandInformation
{
    private String _name = null;
    private String _label = null;
    private BooleanSetterOutput _output = null;

    public String getLabel ()
    {
        return _label;
    }

    public void setLabel ( String label )
    {
        _label = label;
    }

    public String getName ()
    {
        return _name;
    }

    public void setName ( String name )
    {
        _name = name;
    }

    public BooleanSetterOutput getOutput ()
    {
        return _output;
    }

    public void setOutput ( BooleanSetterOutput output )
    {
        _output = output;
    }
}