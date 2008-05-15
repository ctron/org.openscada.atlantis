package org.openscada.da.server.common.chain.test;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.common.HiveService;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.impl.HiveCommon;

/**
 * A hive service registry for testing.
 * <p>
 * Other than the normal {@link HiveCommon} implementation this service registry does not
 * initialize or dispose the services.
 * @author jens
 *
 */
public class TestHiveServiceRegistry implements HiveServiceRegistry
{
    private Map<String, HiveService> services = new HashMap<String, HiveService> ();
    
    public HiveService getService ( String serviceName )
    {
        return services.get ( serviceName );
    }

    public HiveService registerService ( String serviceName, HiveService service )
    {
        return services.put ( serviceName, service );
    }

    public HiveService unregisterService ( String serviceName )
    {
        return services.remove ( serviceName );
    }

}
