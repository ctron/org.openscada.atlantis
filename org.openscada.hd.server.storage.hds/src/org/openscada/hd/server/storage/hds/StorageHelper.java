package org.openscada.hd.server.storage.hds;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openscada.hds.DataFilePool;
import org.openscada.hds.DataStoreAccesor;

public class StorageHelper
{

    public static void create ( final String id, final File file, final StorageConfiguration configuration, final DataFilePool pool ) throws Exception
    {
        file.mkdir ();
    
        final Properties p = new Properties ();
        p.put ( "id", id );
        p.storeToXML ( new FileOutputStream ( new File ( file, "settings.xml" ) ), "openSCADA HD HDS Storage Settings" );
    
        DataStoreAccesor.create ( new File ( file, "native" ), configuration.getTimeSlice (), TimeUnit.MILLISECONDS, configuration.getCount (), pool );
    }

}
