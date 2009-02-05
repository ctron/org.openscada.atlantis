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

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.OutputDefinition;

public class BaseDynamicObject implements DynamicObject
{
    private String _id = null;
    
    private Map<String, InputDefinition> _inputs = new HashMap<String, InputDefinition> ();
    private Map<String, OutputDefinition> _outputs = new HashMap<String, OutputDefinition> ();
    
    public BaseDynamicObject ( String id )
    {
        super ();
        _id = id;
    }
    
    public String getId ()
    {
        return _id;
    }
    
    public InputDefinition getInputByName ( String name )
    {
        return _inputs.get ( name );
    }

    public InputDefinition[] getInputs ()
    {
       return _inputs.values ().toArray ( new InputDefinition [0] );
    }

    public OutputDefinition getOutputByName ( String name )
    {
        return _outputs.get ( name );
    }

    public OutputDefinition[] getOutputs ()
    {
        return _outputs.values ().toArray ( new OutputDefinition [0] );
    }
    
    protected void addInput ( InputDefinition input )
    {
        _inputs.put ( input.getName (), input );
    }
    
    protected void removeInput ( String name )
    {
        _inputs.remove ( name );
    }
    
    protected void addOutput ( OutputDefinition output )
    {
        _outputs.put ( output.getName (), output );
    }
    
    protected void removeOutput ( String name )
    {
        _outputs.remove ( name );
    }

    public void dispose ()
    {
    }
}
