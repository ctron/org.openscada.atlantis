package org.openscada.da.server.common.factory;

import java.util.ArrayList;
import java.util.List;

import org.openscada.da.server.common.chain.ChainItem;
import org.openscada.da.server.common.chain.ChainProcessEntry;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class FactoryHelper
{

    static public List<ChainProcessEntry> instantiateChainList ( List<ChainEntry> chainEntries ) throws ConfigurationError
    {
        List<ChainProcessEntry> list = new ArrayList<ChainProcessEntry> ();
        
        for ( ChainEntry entry : chainEntries )
        {
            ChainProcessEntry processEntry = new ChainProcessEntry ();
            
            processEntry.setWhen ( entry.getWhen () );
            
            Object whatObject;
            try
            {
                whatObject = entry.getWhat ().newInstance ();
            }
            catch ( Exception e )
            {
                throw new ConfigurationError ( "Unable to instatiate chain item", e );
            }
            if ( ! ( whatObject instanceof ChainItem ) ) 
            {
                throw new ConfigurationError ( String.format ( "Chain item %s does not implement interface ChainItem", entry.getWhat () ) );
            }
            
            processEntry.setWhat ( (ChainItem) whatObject );
            
            list.add ( processEntry );
        }
        
        return list;
    }

}
