package org.openscada.da.client.ice;

import org.apache.log4j.Logger;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;

import OpenSCADA.DA.WriteAttributesResultEntry;

public class ConnectionHelper
{
    private static Logger _log = Logger.getLogger ( ConnectionHelper.class );

    public static WriteAttributeResults fromIce ( final WriteAttributesResultEntry[] entries )
    {
        final WriteAttributeResults ret = new WriteAttributeResults ();

        for ( final WriteAttributesResultEntry entry : entries )
        {
            _log.debug ( String.format ( "Attribute result '%s': '%s'", entry.item, entry.result ) );

            if ( entry.result != null && entry.result.length () > 0 )
            {
                ret.put ( entry.item, new WriteAttributeResult ( new Exception ( entry.result ) ) );
            }
            else
            {
                ret.put ( entry.item, WriteAttributeResult.OK );
            }
        }
        return ret;
    }
}
