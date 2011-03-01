/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.spring.client.value;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;

public abstract class AbstractBaseValueSource implements ValueSource
{
    @Override
    public Value<Variant> getVariantValue () throws ValueSourceException
    {
        final DataItemValue value = getValue ();

        if ( value == null )
        {
            throw new ValueSourceException ( "No value from source" );
        }

        if ( !value.isConnected () )
        {
            throw new ValueSourceException ( "Value is not connected" );
        }

        if ( value.isError () )
        {
            throw new ValueSourceException ( "Error flag is set for value" );
        }

        return new Value<Variant> ( value.getValue (), value.isManual (), value.isAlarm (), value.getTimestamp () );
    }

    // boolean

    @Override
    public Value<Boolean> getBooleanValue ( final Boolean defaultValue )
    {
        try
        {
            return getBooleanValue ();
        }
        catch ( final ValueSourceException e )
        {
            return Value.createDefault ( defaultValue );
        }
    }

    @Override
    public Value<Boolean> getBooleanValue () throws ValueSourceException
    {
        final Value<Variant> value = getVariantValue ();

        final Boolean data = value.getValue ().asBoolean ( null );

        if ( data == null )
        {
            throw new ValueSourceException ( "Null value" );
        }

        return new Value<Boolean> ( data, value );
    }

    @Override
    public boolean getBoolean () throws ValueSourceException
    {
        return getBooleanValue ().getValue ();
    }

    @Override
    public Boolean getBoolean ( final Boolean defaultValue )
    {
        return getBooleanValue ( defaultValue ).getValue ();
    }

    // double

    @Override
    public Value<Double> getDoubleValue ( final Double defaultValue )
    {
        try
        {
            return getDoubleValue ();
        }
        catch ( final ValueSourceException e )
        {
            return Value.createDefault ( defaultValue );
        }
    }

    @Override
    public Value<Double> getDoubleValue () throws ValueSourceException
    {
        final Value<Variant> value = getVariantValue ();

        try
        {
            final Double data = value.getValue ().asDouble ();
            return new Value<Double> ( data, value );
        }
        catch ( final Exception e )
        {
            throw new ValueSourceException ( "Failed to convert value", e );
        }
    }

    @Override
    public double getDouble () throws ValueSourceException
    {
        return getDoubleValue ().getValue ();
    }

    @Override
    public Double getDouble ( final Double defaultValue )
    {
        return getDoubleValue ( defaultValue ).getValue ();
    }

    // long

    @Override
    public Value<Long> getLongValue ( final Long defaultValue )
    {
        try
        {
            return getLongValue ();
        }
        catch ( final ValueSourceException e )
        {
            return Value.createDefault ( defaultValue );
        }
    }

    @Override
    public Value<Long> getLongValue () throws ValueSourceException
    {
        final Value<Variant> value = getVariantValue ();

        try
        {
            final Long data = value.getValue ().asLong ();
            return new Value<Long> ( data, value );
        }
        catch ( final Exception e )
        {
            throw new ValueSourceException ( "Failed to convert value", e );
        }
    }

    @Override
    public long getLong () throws ValueSourceException
    {
        return getLongValue ().getValue ();
    }

    @Override
    public Long getLong ( final Long defaultValue )
    {
        return getLongValue ( defaultValue ).getValue ();
    }

    // integer

    @Override
    public Value<Integer> getIntegerValue ( final Integer defaultValue )
    {
        try
        {
            return getIntegerValue ();
        }
        catch ( final ValueSourceException e )
        {
            return Value.createDefault ( defaultValue );
        }
    }

    @Override
    public Value<Integer> getIntegerValue () throws ValueSourceException
    {
        final Value<Variant> value = getVariantValue ();

        try
        {
            final Integer data = value.getValue ().asInteger ();
            return new Value<Integer> ( data, value );
        }
        catch ( final Exception e )
        {
            throw new ValueSourceException ( "Failed to convert value", e );
        }
    }

    @Override
    public int getInteger () throws ValueSourceException
    {
        return getIntegerValue ().getValue ();
    }

    @Override
    public Integer getInteger ( final Integer defaultValue )
    {
        return getIntegerValue ( defaultValue ).getValue ();
    }
}
