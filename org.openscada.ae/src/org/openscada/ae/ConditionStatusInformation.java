package org.openscada.ae;

import java.util.Date;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class ConditionStatusInformation
{
    private final String id;

    private final ConditionStatus status;

    private final Date statusTimestamp;

    private final Variant value;

    private final String lastAknUser;

    private final Date lastAknTimestamp;

    public ConditionStatusInformation ( final String id, final ConditionStatus status, final Date statusTimestamp, final Variant value, final Date lastAknTimestamp, final String lastAknUser )
    {
        super ();
        this.id = id;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
        this.value = value;
        this.lastAknTimestamp = lastAknTimestamp;
        this.lastAknUser = lastAknUser;
    }

    public String getId ()
    {
        return this.id;
    }

    public ConditionStatus getStatus ()
    {
        return this.status;
    }

    public Date getStatusTimestamp ()
    {
        return this.statusTimestamp;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public String getLastAknUser ()
    {
        return this.lastAknUser;
    }

    public Date getLastAknTimestamp ()
    {
        return this.lastAknTimestamp;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.id == null ) ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( ! ( obj instanceof ConditionStatusInformation ) )
        {
            return false;
        }
        ConditionStatusInformation other = (ConditionStatusInformation)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder ();

        sb.append ( this.id + "(" );
        sb.append ( "status=" + this.status );
        sb.append ( ")" );

        return sb.toString ();
    }

}
