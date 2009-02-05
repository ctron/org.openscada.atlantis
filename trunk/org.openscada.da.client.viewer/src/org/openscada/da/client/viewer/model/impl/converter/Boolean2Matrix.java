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

public class Boolean2Matrix extends BaseDynamicObject
{
    public AnyValue _defaultValue = new AnyValue ();
    public AnyValue _value00 = null;
    public AnyValue _value01 = null;
    public AnyValue _value10 = null;
    public AnyValue _value11 = null;
    
    public Boolean _valueA = null;
    public Boolean _valueB = null;
    
    public AnySetterOutput _output = new AnySetterOutput ( "value" );
    
    public Boolean2Matrix ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "defaultValue" ) );
        addInput ( new PropertyInput ( this, "value00" ) );
        addInput ( new PropertyInput ( this, "value01" ) );
        addInput ( new PropertyInput ( this, "value10" ) );
        addInput ( new PropertyInput ( this, "value11" ) );
        
        addInput ( new PropertyInput ( this, "valueA" ) );
        addInput ( new PropertyInput ( this, "valueB" ) );
        
        addOutput ( _output );
    }

    public AnyValue getDefaultValue ()
    {
        return _defaultValue;
    }

    public void setDefaultValue ( AnyValue defaultValue )
    {
        _defaultValue = defaultValue;
        update ();
    }

    public AnyValue getValue00 ()
    {
        return _value00;
    }

    public void setValue00 ( AnyValue value00 )
    {
        _value00 = value00;
        update ();
    }

    public AnyValue getValue01 ()
    {
        return _value01;
    }

    public void setValue01 ( AnyValue value01 )
    {
        _value01 = value01;
        update ();
    }

    public AnyValue getValue10 ()
    {
        return _value10;
    }

    public void setValue10 ( AnyValue value10 )
    {
        _value10 = value10;
        update ();
    }

    public AnyValue getValue11 ()
    {
        return _value11;
    }

    public void setValue11 ( AnyValue value11 )
    {
        _value11 = value11;
        update ();
    }

    public Boolean getValueA ()
    {
        return _valueA;
    }

    public void setValueA ( Boolean valueA )
    {
        _valueA = valueA;
        update ();
    }

    public Boolean getValueB ()
    {
        return _valueB;
    }

    public void setValueB ( Boolean valueB )
    {
        _valueB = valueB;
        update ();
    }
    
    protected void update ()
    {
        if ( _valueA == null || _valueB == null )
        {
            setOutput ( _defaultValue );
            return;
        }
        if ( _value00 == null || _value01 == null || _value10 == null || _value11 == null )
        {
            setOutput ( _defaultValue );
            return;
        }
        
        if ( (!_valueA.booleanValue ()) && (!_valueB.booleanValue ()) )
        {
            setOutput ( _value00 );
        }
        else if ( (_valueA.booleanValue ()) && (!_valueB.booleanValue ()) )
        {
            setOutput ( _value01 );
        }
        else if ( (!_valueA.booleanValue ()) && (_valueB.booleanValue ()) )
        {
            setOutput ( _value10 );
        }
        else
        {
            setOutput ( _value11 );
        }
    }
    
    protected void setOutput ( AnyValue value )
    {
        _output.setValue ( value );
    }

}
