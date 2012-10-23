package org.openscada.hd.server.storage.slave.hds;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.storage.hds.AbstractStorageImpl;
import org.openscada.hds.DataFilePool;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class StorageImpl extends AbstractStorageImpl
{

    private final ServiceRegistration<HistoricalItem> handle;

    public StorageImpl ( final BundleContext context, final File file, final DataFilePool pool, final ScheduledExecutorService queryExecutor ) throws Exception
    {
        super ( file, pool, queryExecutor );

        // register with OSGi
        final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 2 );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_PID, this.id );
        this.handle = context.registerService ( HistoricalItem.class, this, properties );
    }

    @Override
    public void dispose ()
    {
        this.handle.unregister ();
        super.dispose ();
    }

}
