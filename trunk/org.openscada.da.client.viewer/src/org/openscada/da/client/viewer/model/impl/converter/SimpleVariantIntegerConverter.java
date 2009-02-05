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

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class SimpleVariantIntegerConverter extends BaseDynamicObject
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( SimpleVariantIntegerConverter.class );

    private final IntegerSetterOutput _output = new IntegerSetterOutput ( "value" );

    private final BooleanSetterOutput _errorOutput = new BooleanSetterOutput ( "error" );

    private Variant _value = null;

    private long _defaultValue = 0;

    public SimpleVariantIntegerConverter ( final String id )
    {
        super ( id );

        addOutput ( this._output );
        addOutput ( this._errorOutput );
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "defaultValue" ) );
    }

    public void setValue ( final Variant value )
    {
        this._value = value;
        update ();
    }

    public void setDefaultValue ( final Long defaultValue )
    {
        if ( defaultValue != null )
        {
            this._defaultValue = defaultValue.longValue ();
            update ();
        }
    }

    public void update ()
    {
        try
        {
            this._output.setValue ( this._value.asLong () );
            this._errorOutput.setValue ( false );
        }
        catch ( final Exception e )
        {
            //_log.info ( String.format ( "Failed to convert value %s to integer", _value ), e );
            this._output.setValue ( this._defaultValue );
            this._errorOutput.setValue ( true );
        }
    }
}
