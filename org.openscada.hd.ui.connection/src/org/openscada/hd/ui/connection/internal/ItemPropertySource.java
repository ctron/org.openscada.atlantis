/**
 * 
 */
package org.openscada.hd.ui.connection.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.hd.ui.connection.internal.ItemWrapper.Properties;

final class ItemPropertySource implements IPropertySource
{

    private final ItemWrapper itemWrapper;

    public ItemPropertySource ( final ItemWrapper itemWrapper )
    {
        this.itemWrapper = itemWrapper;
    }

    public void setPropertyValue ( final Object id, final Object value )
    {
    }

    public void resetPropertyValue ( final Object id )
    {
    }

    public boolean isPropertySet ( final Object id )
    {
        return false;
    }

    public Object getPropertyValue ( final Object id )
    {
        if ( id instanceof Properties )
        {
            switch ( (Properties)id )
            {
            case CONNECTION_URI:
                return this.itemWrapper.getConnection ().getService ().getConnection ().getConnectionInformation ().toString ();
            case ITEM_ID:
                return this.itemWrapper.getItemInformation ().getId ();
            }
        }
        else if ( id instanceof String )
        {
            return this.itemWrapper.getItemInformation ().getAttributes ().get ( id ).toLabel ();
        }
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        final Collection<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor> ();

        PropertyDescriptor p;

        p = new PropertyDescriptor ( Properties.CONNECTION_URI, Messages.ItemPropertySource_Connection_URI_Label );
        p.setCategory ( Messages.ItemPropertySource_Connection_Category );
        result.add ( p );

        p = new PropertyDescriptor ( Properties.ITEM_ID, Messages.ItemPropertySource_Item_ID_Label );
        p.setCategory ( Messages.ItemPropertySource_Item_Category );
        result.add ( p );

        for ( final String key : this.itemWrapper.getItemInformation ().getAttributes ().keySet () )
        {
            p = new PropertyDescriptor ( key, key );
            p.setCategory ( Messages.ItemPropertySource_Item_Attributes_Category );
            result.add ( p );
        }

        return result.toArray ( new IPropertyDescriptor[0] );
    }

    public Object getEditableValue ()
    {
        return null;
    }
}