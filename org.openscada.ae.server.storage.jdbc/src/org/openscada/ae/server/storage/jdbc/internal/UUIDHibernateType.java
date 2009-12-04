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
        if ( log == null )
        {
            log = LoggerFactory.getLogger ( getClass () );
        }
        return log;
    }

    private static final int[] SQL_TYPES = new int[] { Types.CHAR };

    public UUIDHibernateType ()
    {
    }

    public int[] sqlTypes ()
    {
        return SQL_TYPES;
    }

    public Class<?> returnedClass ()
    {
        return UUID.class;
    }

    public boolean equals ( Object x, Object y ) throws HibernateException
    {
        return ( x == y ) || ( x != null && y != null && x.equals ( y ) );
    }

    public int hashCode ( Object x ) throws HibernateException
    {
        return x.hashCode ();
    }

    public Object nullSafeGet ( ResultSet rs, String[] names, Object arg2 ) throws HibernateException, SQLException
    {
        try
        {
            String id = rs.getString ( names[0] );
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
        catch ( RuntimeException re )
        {
            log ().info ( "could not read column value from result set: {}; {}", names[0], re.getMessage () );
            throw re;
        }
        catch ( SQLException se )
        {
            log ().info ( "could not read column value from result set: {}; {}", names[0], se.getMessage () );
            throw se;
        }
    }

    public void nullSafeSet ( PreparedStatement st, Object value, int index ) throws HibernateException, SQLException
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
        catch ( RuntimeException re )
        {
            log ().info ( "could not bind value '{}' to parameter: {}; {}", new Object[] { value, index, re.getMessage () } );
            throw re;
        }
        catch ( SQLException se )
        {
            log ().info ( "could not bind value '{}' to parameter: {}; {}", new Object[] { value, index, se.getMessage () } );
            throw se;
        }
    }

    public Object deepCopy ( Object value ) throws HibernateException
    {
        return value;
    }

    public boolean isMutable ()
    {
        return false;
    }

    public Serializable disassemble ( Object value ) throws HibernateException
    {
        return (Serializable)value;
    }

    public Object assemble ( Serializable cached, Object owner ) throws HibernateException
    {
        return cached;
    }

    public Object replace ( Object original, Object target, Object owner ) throws HibernateException
    {
        return original;
    }

}
