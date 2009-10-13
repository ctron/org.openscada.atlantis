package org.openscada.hd.ui.connection.data;

import java.io.Serializable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * A data item information object
 * @author Jens Reimann
 *
 */
public class Item implements Serializable
{
    private static final long serialVersionUID = -3086299983454760614L;

    private String connectionString;

    private String id;

    public String getConnectionString ()
    {
        return this.connectionString;
    }

    public void setConnectionString ( final String connectionString )
    {
        this.connectionString = connectionString;
    }

    public String getId ()
    {
        return this.id;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public Item ()
    {
    }

    public Item ( final String connectionString, final String id )
    {
        this.connectionString = connectionString;
        this.id = id;
    }

    public Item ( final Item item )
    {
        this.connectionString = item.connectionString;
        this.id = item.id;
    }

    public static Item adaptTo ( final Object o )
    {
        if ( o instanceof Item )
        {
            return (Item)o;
        }
        else if ( o instanceof IAdaptable )
        {
            return (Item) ( (IAdaptable)o ).getAdapter ( Item.class );
        }
        else
        {
            return (Item)Platform.getAdapterManager ().getAdapter ( o, Item.class );
        }
    }
}
