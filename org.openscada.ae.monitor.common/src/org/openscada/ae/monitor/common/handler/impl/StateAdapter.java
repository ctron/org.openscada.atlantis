package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.monitor.common.AbstractConditionService;
import org.openscada.ae.monitor.common.handler.StateHandler;
import org.openscada.core.Variant;

public class StateAdapter implements StateHandler
{

    protected final AbstractConditionService service;

    private final ConditionStatus state;

    protected final Context currentContext;

    protected static class Context
    {
        private boolean requireAkn;

        private Variant value;

        private Date timestamp;

        private String lastAknUser;

        private Date lastAknTimestamp;

        public Context ()
        {
        }

        public Context ( final Context context )
        {
            this.requireAkn = context.requireAkn;
            this.value = context.value;
            this.timestamp = context.timestamp;
            this.lastAknUser = context.lastAknUser;
            this.lastAknTimestamp = context.lastAknTimestamp;
        }

        public Variant getValue ()
        {
            return this.value;
        }

        public Date getTimestamp ()
        {
            return this.timestamp;
        }

        public void setValue ( final Variant value, final Date timestamp )
        {
            this.value = value;
            this.timestamp = timestamp;
        }

        public boolean isRequireAkn ()
        {
            return this.requireAkn;
        }

        public void setRequireAkn ( final boolean requireAkn )
        {
            this.requireAkn = requireAkn;
        }

        public Date getLastAknTimestamp ()
        {
            return this.lastAknTimestamp;
        }

        public String getLastAknUser ()
        {
            return this.lastAknUser;
        }

        public void setAknInformation ( final String user, final Date timestamp )
        {
            this.lastAknUser = user;
            this.lastAknTimestamp = timestamp;
        }
    }

    public StateAdapter ( final AbstractConditionService service, final Context context, final ConditionStatus state )
    {
        this.service = service;
        this.state = state;
        this.currentContext = context;
    }

    public StateAdapter ( final StateAdapter adapter, final ConditionStatus state )
    {
        this.service = adapter.service;
        this.state = state;
        this.currentContext = new Context ( adapter.currentContext );
    }

    public ConditionStatusInformation getState ()
    {
        return new ConditionStatusInformation ( this.service.getId (), this.state, new Date (), this.currentContext.getValue (), this.currentContext.getLastAknTimestamp (), this.currentContext.getLastAknUser () );
    }

    protected void switchHandler ( final StateHandler handler )
    {
        this.service.setHandler ( handler );
    }

    public void akn ( final String aknUser, final Date aknTimestamp )
    {
    }

    public void disable ()
    {
    }

    public void enable ()
    {
    }

    public void fail ( final Variant value, final Date timestamp )
    {
    }

    protected void publishFailEvent ( final Variant value )
    {
        this.service.publishEvent ( EventHelper.newFailEvent ( this.service.getId (), "", value, this.currentContext.getTimestamp () ) );
    }

    protected void publishUnsafeEvent ()
    {
        this.service.publishEvent ( EventHelper.newUnsafeEvent ( this.service.getId (), "", this.currentContext.getTimestamp () ) );
    }

    protected void publishAknEvent ()
    {
        this.service.publishEvent ( EventHelper.newAknEvent ( this.service.getId (), "", this.currentContext.getTimestamp () ) );
    }

    public void ok ( final Variant value, final Date timestamp )
    {
    }

    protected void publishOkEvent ( final Variant value )
    {
        this.service.publishEvent ( EventHelper.newOkEvent ( this.service.getId (), "", value, this.currentContext.getTimestamp () ) );
    }

    public void unsafe ()
    {
    }

    public void ignoreAkn ()
    {
        this.service.publishEvent ( EventHelper.newConfigurationEvent ( this.service.getId (), "Changed require akn", new Variant ( false ), new Date () ) );
        this.currentContext.setRequireAkn ( false );
    }

    public void requireAkn ()
    {
        this.service.publishEvent ( EventHelper.newConfigurationEvent ( this.service.getId (), "Changed require akn", new Variant ( true ), new Date () ) );
        this.currentContext.setRequireAkn ( true );
    }

    protected void setValue ( final Variant value, final Date timestamp )
    {
        this.currentContext.setValue ( value, timestamp );
    }

    public void activate ()
    {
    }

    public void deactivate ()
    {
    }

    protected void setAknInformation ( final String aknUser, final Date aknTimestamp )
    {
        this.currentContext.setAknInformation ( aknUser, aknTimestamp );
    }
}
