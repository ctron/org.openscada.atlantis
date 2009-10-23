package org.openscada.ae.ui.connection.data;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.core.Variant;
import org.openscada.utils.beans.AbstractPropertyChange;

public class ConditionStatusBean extends AbstractPropertyChange
{
    public static final String PROP_STATUS = "status";

    public static final String PROP_STATUS_TIMESTAMP = "statusTimestamp";

    public static final String PROP_VALUE = "value";

    public static final String PROP_LAST_AKN_USER = "lastAknUser";

    public static final String PROP_LAST_AKN_TIMESTAMP = "lastAknTimestamp";

    private final ConnectionService connection;

    private final String id;

    private ConditionStatus status;

    private Date statusTimestamp;

    private Variant value;

    private String lastAknUser;

    private Date lastAknTimestamp;

    public ConditionStatusBean ( final ConnectionService connection, final String id )
    {
        this.connection = connection;
        this.id = id;
    }

    public ConditionStatusBean ( final ConnectionService connection, final ConditionStatusInformation information )
    {
        this ( connection, information.getId () );
        this.status = information.getStatus ();
        this.statusTimestamp = information.getStatusTimestamp ();
        this.value = information.getValue ();
        this.lastAknUser = information.getLastAknUser ();
        this.lastAknTimestamp = information.getLastAknTimestamp ();
    }

    public String getId ()
    {
        return this.id;
    }

    public ConnectionService getConnection ()
    {
        return this.connection;
    }

    public ConditionStatus getStatus ()
    {
        return this.status;
    }

    public void setStatus ( final ConditionStatus status )
    {
        final ConditionStatus oldStatus = this.status;
        this.status = status;
        firePropertyChange ( PROP_STATUS, oldStatus, status );
    }

    public void update ( final ConditionStatusInformation info )
    {
        setStatus ( info.getStatus () );
        setStatusTimestamp ( info.getStatusTimestamp () );
        setValue ( info.getValue () );
        setLastAknTimestamp ( info.getLastAknTimestamp () );
        setLastAknUser ( info.getLastAknUser () );
    }

    public Date getStatusTimestamp ()
    {
        return this.statusTimestamp;
    }

    public void setStatusTimestamp ( final Date statusTimestamp )
    {
        final Date oldStatusTimestamp = this.statusTimestamp;
        this.statusTimestamp = statusTimestamp;
        firePropertyChange ( PROP_STATUS_TIMESTAMP, oldStatusTimestamp, statusTimestamp );
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public void setValue ( final Variant value )
    {
        final Variant oldValue = this.value;
        this.value = value;
        firePropertyChange ( PROP_VALUE, oldValue, value );
    }

    public String getLastAknUser ()
    {
        return this.lastAknUser;
    }

    public void setLastAknUser ( final String lastAknUser )
    {
        final String oldLastAknUser = this.lastAknUser;
        this.lastAknUser = lastAknUser;
        firePropertyChange ( PROP_LAST_AKN_USER, oldLastAknUser, lastAknUser );
    }

    public Date getLastAknTimestamp ()
    {
        return this.lastAknTimestamp;
    }

    public void setLastAknTimestamp ( final Date lastAknTimestamp )
    {
        final Date oldLastDateAknTimestamp = this.lastAknTimestamp;
        this.lastAknTimestamp = lastAknTimestamp;
        firePropertyChange ( PROP_LAST_AKN_TIMESTAMP, oldLastDateAknTimestamp, lastAknTimestamp );
    }

    public void akn ()
    {
        this.connection.getConnection ().acknowledge ( this.id, new Date () );
    }
}
