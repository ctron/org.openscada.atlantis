package org.openscada.ae.monitor.common;

import java.util.Date;

import org.openscada.core.Variant;

public class StateInformation
{
    private Boolean requireAck;

    private Boolean active;

    private Boolean ok;

    private Variant value;

    private Date timestamp;

    private String lastAckUser;

    private Date lastAckTimestamp;

    private Date lastFailTimestamp;

    public StateInformation ()
    {
    }

    public StateInformation ( final StateInformation information )
    {
        this.requireAck = information.requireAck;
        this.active = information.active;
        this.ok = information.ok;
        this.value = information.value;
        if ( information.timestamp != null )
        {
            this.timestamp = (Date)information.timestamp.clone ();
        }
        this.lastAckUser = information.lastAckUser;
        if ( information.lastAckTimestamp != null )
        {
            this.lastAckTimestamp = (Date)information.lastAckTimestamp.clone ();
        }
        if ( information.lastFailTimestamp != null )
        {
            this.lastFailTimestamp = (Date)information.lastFailTimestamp.clone ();
        }
    }

    public StateInformation apply ( final StateInformation information )
    {
        final StateInformation newInformation = new StateInformation ( this );
        if ( information.active != null )
        {
            newInformation.active = information.active;
        }
        if ( information.requireAck != null )
        {
            newInformation.requireAck = information.requireAck;
        }
        if ( information.ok != null )
        {
            newInformation.ok = information.ok;
        }
        if ( information.value != null )
        {
            newInformation.value = information.value;
        }
        if ( information.timestamp != null )
        {
            newInformation.timestamp = (Date)information.timestamp.clone ();
        }
        if ( information.lastAckUser != null )
        {
            newInformation.lastAckUser = information.lastAckUser;
        }
        if ( information.lastAckTimestamp != null )
        {
            newInformation.lastAckTimestamp = information.lastAckTimestamp;
        }
        if ( information.lastFailTimestamp != null )
        {
            newInformation.lastFailTimestamp = information.lastFailTimestamp;
        }

        return newInformation;
    }

    public Date getLastFailTimestamp ()
    {
        return this.lastFailTimestamp;
    }

    public void setLastFailTimestamp ( final Date lastFailTimestamp )
    {
        this.lastFailTimestamp = lastFailTimestamp;
    }

    public Boolean getRequireAck ()
    {
        return this.requireAck;
    }

    public void setRequireAck ( final Boolean requireAck )
    {
        this.requireAck = requireAck;
    }

    public Boolean getActive ()
    {
        return this.active;
    }

    public void setActive ( final Boolean active )
    {
        this.active = active;
    }

    public Boolean getOk ()
    {
        return this.ok;
    }

    public void setOk ( final Boolean ok )
    {
        this.ok = ok;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public void setValue ( final Variant value )
    {
        this.value = value;
    }

    public Date getTimestamp ()
    {
        return this.timestamp;
    }

    public void setTimestamp ( final Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getLastAckUser ()
    {
        return this.lastAckUser;
    }

    public void setLastAckUser ( final String lastAckUser )
    {
        this.lastAckUser = lastAckUser;
    }

    public Date getLastAckTimestamp ()
    {
        return this.lastAckTimestamp;
    }

    public void setLastAckTimestamp ( final Date lackAckTimestamp )
    {
        this.lastAckTimestamp = lackAckTimestamp;
    }
}
