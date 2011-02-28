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
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDHibernateType implements UserType
{
    private static final boolean IS_VALUE_TRACING_ENABLED = LoggerFactory.getLogger ( "org.hibernate.type" ).isTraceEnabled ();

    private transient Logger log;

    private Logger log ()
    {
        if ( this.log == null )
        {
            this.log = LoggerFactory.getLogger ( getClass () );
        }
        return this.log;
    }

    private static final int[] SQL_TYPES = new int[] { Types.CHAR };

    public UUIDHibernateType ()
    {
    }

    @Override
    public int[] sqlTypes ()
    {
        return SQL_TYPES;
    }

    @Override
    public Class<?> returnedClass ()
    {
        return UUID.class;
    }

    @Override
    public boolean equals ( final Object x, final Object y ) throws HibernateException
    {
        return x == y || x != null && y != null && x.equals ( y );
    }

    @Override
    public int hashCode ( final Object x ) throws HibernateException
    {
        return x.hashCode ();
    }

    @Override
    public Object nullSafeGet ( final ResultSet rs, final String[] names, final Object arg2 ) throws HibernateException, SQLException
    {
        try
        {
            final String id = rs.getString ( names[0] );
            if ( id == null || rs.wasNull () )
            {
                if ( IS_VALUE_TRACING_ENABLED )
                {
                    log ().trace ( "returning null as column: {}" + names[0] );
                }
                return null;
            }
            else
            {
                if ( IS_VALUE_TRACING_ENABLED )
                {
                    log ().trace ( "returning '{}' as column: {}", id, names[0] );
                }
                return UUID.fromString ( id );
            }
        }
        catch ( final RuntimeException re )
        {
            log ().info ( "could not read column value from result set: {}; {}", names[0], re.getMessage () );
            throw re;
        }
        catch ( final SQLException se )
        {
            log ().info ( "could not read column value from result set: {}; {}", names[0], se.getMessage () );
            throw se;
        }
    }

    @Override
    public void nullSafeSet ( final PreparedStatement st, final Object value, final int index ) throws HibernateException, SQLException
    {
        try
        {
            if ( value == null )
            {
                if ( IS_VALUE_TRACING_ENABLED )
                {
                    log ().trace ( "binding null to parameter: {}", index );
                }

                st.setNull ( index, Types.CHAR );
            }
            else
            {
                if ( IS_VALUE_TRACING_ENABLED )
                {
                    log ().trace ( "binding '{}' to parameter: {}", ( (UUID)value ).toString (), index );
                }

                st.setString ( index, ( (UUID)value ).toString () );
            }
        }
        catch ( final RuntimeException re )
        {
            log ().info ( "could not bind value '{}' to parameter: {}; {}", new Object[] { value, index, re.getMessage () } );
            throw re;
        }
        catch ( final SQLException se )
        {
            log ().info ( "could not bind value '{}' to parameter: {}; {}", new Object[] { value, index, se.getMessage () } );
            throw se;
        }
    }

    @Override
    public Object deepCopy ( final Object value ) throws HibernateException
    {
        return value;
    }

    @Override
    public boolean isMutable ()
    {
        return false;
    }

    @Override
    public Serializable disassemble ( final Object value ) throws HibernateException
    {
        return (Serializable)value;
    }

    @Override
    public Object assemble ( final Serializable cached, final Object owner ) throws HibernateException
    {
        return cached;
    }

    @Override
    public Object replace ( final Object original, final Object target, final Object owner ) throws HibernateException
    {
        return original;
    }

}
