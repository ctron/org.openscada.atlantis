package org.openscada.da.server.proxy;

import java.util.Map;
import java.util.regex.Pattern;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyItem extends DataItemInputOutputChained implements ItemUpdateListener
{
    private final Connection connection;

    private final String prefix;

    /**
     * @param connection
     * @param id
     */
    public ProxyItem ( final Connection connection, final String id, final String prefix )
    {
        super ( id );
        this.connection = connection;
        this.prefix = prefix;
    }

    @Override
    protected void writeCalculatedValue ( final Variant value ) throws NotConvertableException, InvalidOperationException
    {
        try
        {
        	if (this.connection.getClass() == RedundantConnection.class) {
        		this.connection.write ( this.getInformation ().getName (), value );
        	} else {
        		this.connection.write ( prepareItemName (), value );
        	}
        }
        catch ( final NoConnectionException e )
        {
            throw new InvalidOperationException ();
        }
        catch ( final OperationException e )
        {
            throw new InvalidOperationException ();
        }
    }

    private String prepareItemName ()
    {
        return this.getInformation ().getName ().replaceFirst ( Pattern.quote ( this.prefix ), "" );
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        updateData ( value, attributes, AttributeMode.UPDATE );
    }

    public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
    }
}
