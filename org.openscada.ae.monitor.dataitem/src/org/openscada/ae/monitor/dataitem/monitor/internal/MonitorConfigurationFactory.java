package org.openscada.ae.monitor.dataitem.monitor.internal;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.ConditionService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class MonitorConfigurationFactory implements ConfigurationFactory, AknHandler
{

    private final BundleContext context;

    private final EventProcessor eventProcessor;

    private final Map<String, DataItemMonitor> monitors = new HashMap<String, DataItemMonitor> ();

    private final Map<String, ServiceRegistration> monitorRegs = new HashMap<String, ServiceRegistration> ();

    public MonitorConfigurationFactory ( final BundleContext context, final EventProcessor eventProcessor )
    {
        this.context = context;
        this.eventProcessor = eventProcessor;
    }

    public void delete ( final String configurationId )
    {
        deleteMonitor ( configurationId );
    }

    public void purge ()
    {
        removeAllMonitors ();
    }

    public synchronized void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        String masterId = properties.get ( "masterId" );
        if ( masterId == null )
        {
            throw new IllegalArgumentException ( "'masterId' must not be null" );
        }

        DataItemMonitor monitor = this.monitors.get ( configurationId );

        if ( monitor == null )
        {
            createMonitor ( configurationId, masterId, properties );
        }
        else
        {
            monitor.configure ( properties );
        }
    }

    protected synchronized void createMonitor ( final String id, final String masterId, final Map<String, String> properties ) throws Exception
    {
        String type = properties.get ( "type" );
        if ( type == null )
        {
            throw new IllegalArgumentException ( "'type' must be set" );
        }

        // create
        DataItemMonitor monitor;
        if ( "high".equals ( type ) )
        {
            monitor = new LevelMonitorHigh ( this.context, this.eventProcessor, id, masterId, properties );
        }
        else if ( "low".equals ( type ) )
        {
            monitor = new LevelMonitorLow ( this.context, this.eventProcessor, id, masterId, properties );
        }
        else
        {
            throw new IllegalArgumentException ( String.format ( "Type '%s' is unknown.", type ) );
        }

        // initialize
        monitor.initialize ();

        this.monitors.put ( id, monitor );

        Dictionary<Object, Object> regProperties = new Hashtable<Object, Object> ();
        ServiceRegistration reg = this.context.registerService ( new String[] { ConditionService.class.getName () }, monitor, regProperties );
        this.monitorRegs.put ( id, reg );
    }

    protected void deleteMonitor ( final String id )
    {
        DataItemMonitor monitor;
        ServiceRegistration reg;

        synchronized ( this )
        {
            monitor = this.monitors.remove ( id );
            reg = this.monitorRegs.remove ( id );
        }

        if ( reg != null )
        {
            reg.unregister ();
        }
        if ( monitor != null )
        {
            monitor.dispose ();
        }
    }

    public void dispose ()
    {
        removeAllMonitors ();
    }

    private synchronized void removeAllMonitors ()
    {
        for ( ServiceRegistration reg : this.monitorRegs.values () )
        {
            reg.unregister ();
        }
        for ( DataItemMonitor monitor : this.monitors.values () )
        {
            monitor.dispose ();
        }
    }

    public boolean acknowledge ( final String conditionId, final String aknUser, final Date aknTimestamp )
    {
        DataItemMonitor monitor;
        synchronized ( this )
        {
            monitor = this.monitors.get ( conditionId );
        }
        if ( monitor != null )
        {
            monitor.akn ( aknUser, aknTimestamp );
            return true;
        }
        else
        {
            return false;
        }
    }

}
