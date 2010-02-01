package org.openscada.da.server.common.osgi.factory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleObjectExporter<T>
{

    private final static Logger logger = LoggerFactory.getLogger ( SimpleObjectExporter.class );

    private final Class<? extends T> objectClass;

    private final DataItemFactory factory;

    private final String prefix;

    private final Map<String, DataItemInputChained> itemMap = new HashMap<String, DataItemInputChained> ();

    public SimpleObjectExporter ( final Class<? extends T> objectClass, final DataItemFactory factory, final String prefix )
    {
        this.objectClass = objectClass;
        this.factory = factory;
        this.prefix = prefix;

        createFields ();
        setValue ( null );
    }

    public void setValue ( final T value )
    {
        final long timestamp = System.currentTimeMillis ();

        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( this.objectClass );
            for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
            {
                final DataItemInputChained item = this.itemMap.get ( pd.getName () );

                if ( value != null )
                {
                    try
                    {
                        final Object data = pd.getReadMethod ().invoke ( value );
                        setItemValue ( pd, item, data, timestamp );
                    }
                    catch ( final Exception e )
                    {
                        setItemError ( pd, item, e );
                    }
                }
                else
                {
                    setItemError ( pd, item, null );
                }

            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to set value" );
            for ( final DataItemInputChained item : this.itemMap.values () )
            {
                setItemError ( null, item, e );
            }
        }
    }

    private void setItemError ( final PropertyDescriptor pd, final DataItemInputChained item, final Exception e )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        if ( pd != null )
        {
            fillAttributes ( pd, attributes );
        }

        if ( e != null )
        {
            attributes.put ( "invocation.error", Variant.TRUE );
            attributes.put ( "invocation.error.message", new Variant ( e.getMessage () ) );
        }
        else
        {
            attributes.put ( "null.error", Variant.TRUE );
        }

        item.updateData ( Variant.NULL, attributes, AttributeMode.SET );
    }

    private void fillAttributes ( final PropertyDescriptor pd, final Map<String, Variant> attributes )
    {
        attributes.put ( "property.name", new Variant ( pd.getName () ) );
        attributes.put ( "property.type", new Variant ( pd.getPropertyType ().getName () ) );
    }

    private void setItemValue ( final PropertyDescriptor pd, final DataItemInputChained item, final Object data, final long timestamp )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        fillAttributes ( pd, attributes );
        attributes.put ( "timestamp", new Variant ( timestamp ) );

        item.updateData ( new Variant ( data ), attributes, AttributeMode.SET );
    }

    private void createFields ()
    {
        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( this.objectClass );
            for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
            {
                if ( pd.getReadMethod () != null )
                {
                    createDataItem ( pd );
                }
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create fields", e );
        }
    }

    private void createDataItem ( final PropertyDescriptor pd )
    {
        final String name = pd.getName ();

        final Map<String, Variant> properties = new HashMap<String, Variant> ();
        if ( pd.getShortDescription () != null )
        {
            properties.put ( "description", new Variant ( pd.getShortDescription () ) );
        }
        else
        {
            properties.put ( "description", new Variant ( "Field: " + pd.getName () ) );
        }

        final DataItemInputChained item = this.factory.createInput ( this.prefix + "." + name, properties );

        ChainCreator.applyDefaultInputChain ( item, null );

        this.itemMap.put ( name, item );
    }
}
