package org.openscada.da.server.dave.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.NotifyFuture;

public class DaveDataitem extends DataItemInputOutputChained
{

    private final ScalarVariable variable;

    public DaveDataitem ( final String itemId, final Executor executor, final ScalarVariable variable )
    {
        super ( itemId, executor );
        this.variable = variable;
    }

    @Override
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults initialResults, final Map<String, Variant> attributes )
    {
        // check for null
        WriteAttributeResults writeAttributeResults = initialResults;
        if ( writeAttributeResults == null )
        {
            writeAttributeResults = new WriteAttributeResults ();
        }

        // gather the list of open requests
        final Map<String, Variant> requests = new HashMap<String, Variant> ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( !writeAttributeResults.containsKey ( entry.getKey () ) )
            {
                requests.put ( entry.getKey (), entry.getValue () );
            }
        }

        // hand over to the variable instance
        final Set<String> accepted = this.variable.handleAttributes ( requests );

        // OK for all accepted
        for ( final String attr : accepted )
        {
            writeAttributeResults.put ( attr, WriteAttributeResult.OK );
        }

        // default for the rest
        return super.handleUnhandledAttributes ( writeAttributeResults, attributes );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        return this.variable.handleWrite ( value );
    }

}
