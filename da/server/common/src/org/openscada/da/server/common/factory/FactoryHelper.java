package org.openscada.da.server.common.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.ChainItem;
import org.openscada.da.server.common.chain.ChainProcessEntry;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class FactoryHelper
{
    private static Logger log = Logger.getLogger ( FactoryHelper.class );

    static public ChainItem createChainItem ( HiveServiceRegistry serviceRegistry, Class<?> clazz ) throws ConfigurationError
    {
        Object whatObject;
        try
        {
            Constructor<?> ctor = null;
            try
            {
                ctor = clazz.getConstructor ( HiveServiceRegistry.class );
            }
            catch ( Throwable e )
            {
                log.info ( "Failed to load ctor for HiveServiceRegistry" );
            }

            if ( ctor != null )
            {
                whatObject = ctor.newInstance ( serviceRegistry );
            }
            else
            {
                whatObject = clazz.newInstance ();
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationError ( "Unable to instatiate chain item", e );
        }

        if ( ! ( whatObject instanceof ChainItem ) )
        {
            throw new ConfigurationError ( String.format ( "Chain item %s does not implement interface ChainItem",
                    clazz ) );
        }

        return (ChainItem)whatObject;
    }

    public static List<ChainProcessEntry> instantiateChainList ( HiveServiceRegistry serviceRegistry, List<ChainEntry> chainEntries ) throws ConfigurationError
    {
        List<ChainProcessEntry> list = new ArrayList<ChainProcessEntry> ();

        for ( ChainEntry entry : chainEntries )
        {
            ChainProcessEntry processEntry = new ChainProcessEntry ();

            processEntry.setWhen ( entry.getWhen () );
            processEntry.setWhat ( createChainItem ( serviceRegistry, entry.getWhat () ) );

            list.add ( processEntry );
        }

        return list;
    }

}
