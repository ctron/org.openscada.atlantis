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

package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.impl.AnySetterOutput;
import org.openscada.da.client.viewer.model.impl.AnyValue;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class If extends BaseDynamicObject
{
    public AnyValue _trueValue = new AnyValue ();
    public AnyValue _falseValue = new AnyValue ();
    public AnyValue _errorValue = new AnyValue ();
    public Boolean _conditionValue = null;
    
    public AnySetterOutput _output = new AnySetterOutput ( "value" );
    
    public If ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "trueValue" ) );
        addInput ( new PropertyInput ( this, "falseValue" ) );
        addInput ( new PropertyInput ( this, "errorValue" ) );
        addInput ( new PropertyInput ( this, "conditionValue" ) );
        
        addOutput ( _output );
    }

    protected void update ()
    {
        if ( _conditionValue == null )
        {
            setOutput ( _errorValue );
        }
        else
        {
            setOutput ( _conditionValue ? _trueValue : _falseValue );  
        }
    }
    
    protected void setOutput ( AnyValue value )
    {
        _output.setValue ( value );
    }

    public AnyValue getFalseValue ()
    {
        return _falseValue;
    }

    public void setFalseValue ( AnyValue falseValue )
    {
        _falseValue = falseValue;
        update ();
    }

    public AnyValue getTrueValue ()
    {
        return _trueValue;
    }

    public void setTrueValue ( AnyValue trueValue )
    {
        _trueValue = trueValue;
        update ();
    }

    public AnyValue getErrorValue ()
    {
        return _errorValue;
    }

    public void setErrorValue ( AnyValue errorValue )
    {
        _errorValue = errorValue;
        update ();
    }

    public Boolean getConditionValue ()
    {
        return _conditionValue;
    }

    public void setConditionValue ( Boolean conditionValue )
    {
        _conditionValue = conditionValue;
        update ();
    }

}
