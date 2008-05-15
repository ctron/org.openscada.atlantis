package org.openscada.da.server.common.chain.storage;

import java.io.File;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.impl.HiveCommon;

public class ChainStorageServiceHelper
{
    private static Logger log = Logger.getLogger ( ChainStorageServiceHelper.class );

    public static void registerService ( HiveCommon hive, ChainStorageService service )
    {
        hive.registerService ( ChainStorageService.SERVICE_ID, service );
    }

    public static void registerDefaultPropertyService ( HiveCommon hive )
    {
        // add property file chain item storage
        String propName = ChainStorageService.SERVICE_ID + ".path";
        String dirName = System.getProperty ( propName, null );

        if ( dirName == null )
        {
            log.info ( String.format ( "Property file chain item storage service is not set (%s)", propName ) );
            return;
        }

        File dir = new File ( dirName );
        if ( dir.exists () && dir.isDirectory () )
        {
            hive.registerService ( ChainStorageService.SERVICE_ID, new PropertyFileChainStorageService ( new File (
                    dirName ) ) );
        }
    }
}
