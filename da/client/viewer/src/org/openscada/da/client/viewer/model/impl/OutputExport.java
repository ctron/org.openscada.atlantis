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

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;

public class OutputExport implements OutputDefinition
{
    private OutputDefinition _output = null;
    private String _alias = null;
    
    public OutputExport ( OutputDefinition output, String alias )
    {
        _output = output;
        _alias = alias;
    }

    public void addListener ( OutputListener listener )
    {
        _output.addListener ( listener );
    }

    public void removeListener ( OutputListener listener )
    {
        _output.removeListener ( listener );
    }

    public String getName ()
    {
        return _alias;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return _output.getSupportedTypes ();
    }


}
