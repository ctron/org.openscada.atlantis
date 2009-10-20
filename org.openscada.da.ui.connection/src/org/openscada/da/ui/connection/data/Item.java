package org.openscada.da.ui.connection.data;

import java.io.Serializable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;

/**
 * A data item information object
 * @author Jens Reimann
 *
 */
public class Item implements Serializable
{
    private static final long serialVersionUID = 7384306434115724744L;

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

    public static Item loadFrom ( final IMemento memento )
    {
        if ( memento == null )
        {
            return null;
        }

        final String itemId = memento.getString ( "item.id" );
        final String connectionUri = memento.getString ( "connection.uri" );

        if ( itemId == null || connectionUri == null )
        {
            return null;
        }
        else
        {
            return new Item ( connectionUri, itemId );
        }
    }

    public void saveTo ( final IMemento memento )
    {
        if ( memento == null )
        {
            return;
        }

        memento.putString ( "item.id", this.id );
        memento.putString ( "connection.uri", this.connectionString );
    }
}
