package org.openscada.ae.monitor.dataitem.monitor.internal.remote;

import java.io.Serializable;
import java.util.Date;

import org.openscada.ae.ConditionStatus;

public class PersistentState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String lastAckUser;

    private ConditionStatus state;

    private Date timestamp;

    private Date ackTimestamp;

    public PersistentState ()
    {
        this.state = ConditionStatus.INIT;
    }

    public String getLastAckUser ()
    {
        return this.lastAckUser;
    }

    public void setLastAckUser ( final String lastAckUser )
    {
        this.lastAckUser = lastAckUser;
    }

    public ConditionStatus getState ()
    {
        return this.state;
    }

    public void setState ( final ConditionStatus state )
    {
        this.state = state;
    }

    public Date getAckTimestamp ()
    {
        return this.ackTimestamp;
    }

    public Date getTimestamp ()
    {
        return this.timestamp;
    }

    public void setAckTimestamp ( final Date ackTimestamp )
    {
        this.ackTimestamp = ackTimestamp;
    }

    public void setTimestamp ( final Date timestamp )
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[PersistentState - State: %s, Timestamp: %s, LastAckUser: %s, LastAckTimestamp: %s", this.state, this.timestamp, this.lastAckUser, this.ackTimestamp );
    }
}
