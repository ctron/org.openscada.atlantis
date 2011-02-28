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

package org.openscada.ae.server.storage.jdbc.internal;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.VariantType;

public class VariantHibernateType implements CompositeUserType, Serializable
{

    private static final long serialVersionUID = -1245809821282686169L;

    private static final int[] TYPES = { Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.DOUBLE };

    private static final String[] PROPERTY_NAMES = { "type", "string", "integer", "double" };

    private static final Type[] PROPERTY_TYPES = { Hibernate.STRING, Hibernate.STRING, Hibernate.INTEGER, Hibernate.DOUBLE };

    public VariantHibernateType ()
    {
    }

    @Override
    public Object assemble ( final Serializable cached, final SessionImplementor session, final Object owner ) throws HibernateException
    {
        return cached;
    }

    @Override
    public Object deepCopy ( final Object value ) throws HibernateException
    {
        return new Variant ( (Variant)value );
    }

    @Override
    public Serializable disassemble ( final Object value, final SessionImplementor session ) throws HibernateException
    {
        return (Serializable)value;
    }

    @Override
    public boolean equals ( final Object x, final Object y ) throws HibernateException
    {
        if ( x == y )
        {
            return true;
        }
        if ( x == null || y == null )
        {
            return false;
        }
        return x.equals ( y );
    }

    @Override
    public String[] getPropertyNames ()
    {
        return PROPERTY_NAMES;
    }

    @Override
    public Type[] getPropertyTypes ()
    {
        return PROPERTY_TYPES;
    }

    @Override
    public Object getPropertyValue ( final Object component, final int property ) throws HibernateException
    {
        try
        {
            switch ( property )
            {
            case 0:
                return ( (Variant)component ).getType ();
            case 1:
                return ( (Variant)component ).asString ();
            case 2:
                return ( (Variant)component ).asInteger ();
            case 3:
                return ( (Variant)component ).asDouble ();
            default:
                break;
            }
        }
        catch ( final NullValueException e )
        {
            return null;
        }
        catch ( final NotConvertableException e )
        {
            return null;
        }
        return null;
    }

    @Override
    public int hashCode ( final Object x ) throws HibernateException
    {
        if ( x == null )
        {
            return 0;
        }
        return x.hashCode ();
    }

    @Override
    public boolean isMutable ()
    {
        return false;
    }

    @Override
    public Object nullSafeGet ( final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner ) throws HibernateException, SQLException
    {
        final String type = rs.getString ( names[0] );
        final String str = rs.getString ( names[1] );
        final Long i = rs.getLong ( names[2] );
        final Double d = rs.getDouble ( names[3] );
        if ( type == null )
        {
            return null;
        }
        if ( VariantType.NULL.equals ( type ) )
        {
            return new Variant ( (Object)null );
        }
        if ( VariantType.UNKNOWN.equals ( type ) )
        {
            return new Variant ( new Object () );
        }
        if ( VariantType.BOOLEAN.equals ( type ) )
        {
            if ( i == null )
            {
                return new Variant ( (Boolean)null );
            }
            else
            {
                return new Variant ( !i.equals ( 0L ) );
            }
        }
        if ( VariantType.INT32.equals ( type ) )
        {
            if ( i == null )
            {
                return new Variant ( (Integer)null );
            }
            else
            {
                return new Variant ( i.intValue () );
            }
        }
        if ( VariantType.INT64.equals ( type ) )
        {
            if ( i == null )
            {
                return new Variant ( (Long)null );
            }
            else
            {
                return new Variant ( i.longValue () );
            }
        }
        if ( VariantType.DOUBLE.equals ( type ) )
        {
            if ( i == null )
            {
                return new Variant ( (Double)null );
            }
            else
            {
                return new Variant ( d );
            }
        }
        return new Variant ( str );
    }

    @Override
    public void nullSafeSet ( final PreparedStatement st, final Object value, final int index, final SessionImplementor session ) throws HibernateException, SQLException
    {
        st.setNull ( index + 0, TYPES[0] );
        st.setNull ( index + 1, TYPES[1] );
        st.setNull ( index + 2, TYPES[2] );
        st.setNull ( index + 3, TYPES[3] );
        final Variant v = (Variant)value;
        if ( v != null )
        {
            try
            {
                st.setString ( index + 0, v.getType ().toString () );
                st.setString ( index + 1, v.asString () );
                st.setLong ( index + 2, v.asLong () );
                st.setDouble ( index + 3, v.asDouble () );
            }
            catch ( final NullValueException e )
            {
                // leave at null
            }
            catch ( final NotConvertableException e )
            {
                // leave at null
            }
        }
    }

    @Override
    public Object replace ( final Object original, final Object target, final SessionImplementor session, final Object owner ) throws HibernateException
    {
        return original;
    }

    @Override
    @SuppressWarnings ( "unchecked" )
    public Class returnedClass ()
    {
        return Variant.class;
    }

    @Override
    public void setPropertyValue ( final Object component, final int property, final Object value ) throws HibernateException
    {
        throw new UnsupportedOperationException ( "Variant is immutable" );
    }

}
