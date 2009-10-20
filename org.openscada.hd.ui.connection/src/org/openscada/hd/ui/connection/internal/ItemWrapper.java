package org.openscada.hd.ui.connection.internal;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openscada.hd.HistoricalItemInformation;

public class ItemWrapper extends PlatformObject implements IAdaptable
{
    enum Properties
    {
        CONNECTION_URI,
        ITEM_ID
    }

    private final ConnectionWrapper connection;

    private final HistoricalItemInformation itemInformation;

    public ItemWrapper ( final ConnectionWrapper connection, final HistoricalItemInformation itemInformation )
    {
        this.connection = connection;
        this.itemInformation = itemInformation;
    }

    public HistoricalItemInformation getItemInformation ()
    {
        return this.itemInformation;
    }

    public ConnectionWrapper getConnection ()
    {
        return this.connection;
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        if ( adapter == IPropertySource.class )
        {
            return new ItemPropertySource ( this );
        }
        return super.getAdapter ( adapter );
    }
}
