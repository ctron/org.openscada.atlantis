package org.openscada.hd.net;

import java.util.HashSet;
import java.util.Set;

import org.openscada.core.net.MessageHelper;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class ListItemHelper
{

    public static Set<HistoricalItemInformation> fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }

        final Set<HistoricalItemInformation> result = new HashSet<HistoricalItemInformation> ();

        final ListValue value = (ListValue)baseValue;

        for ( final Value entryValue : value.getValues () )
        {
            final HistoricalItemInformation entry = fromValueEntry ( entryValue );
            if ( entry != null )
            {
                result.add ( entry );
            }
        }
        return result;
    }

    private static HistoricalItemInformation fromValueEntry ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        final MapValue value = (MapValue)entryValue;
        try
        {

            final String id = ( (StringValue)value.get ( "id" ) ).getValue ();

            final Value attributes = value.get ( "attributes" );
            if ( ! ( attributes instanceof MapValue ) )
            {
                return null;
            }

            return new HistoricalItemInformation ( id, MessageHelper.mapToAttributes ( (MapValue)attributes ) );
        }
        catch ( final ClassCastException e )
        {
            return null;
        }
        catch ( final NullPointerException e )
        {
            return null;
        }
    }

    public static Value toValue ( final HistoricalItemInformation[] added )
    {
        final ListValue result = new ListValue ();

        if ( added != null )
        {
            for ( final HistoricalItemInformation entry : added )
            {
                result.add ( toValue ( entry ) );
            }
        }

        return result;
    }

    private static Value toValue ( final HistoricalItemInformation entry )
    {
        final MapValue value = new MapValue ();

        value.put ( "id", new StringValue ( entry.getId () ) );
        value.put ( "attributes", MessageHelper.attributesToMap ( entry.getAttributes () ) );

        return value;
    }

    public static Value toValue ( final String[] removed )
    {
        if ( removed == null )
        {
            return new VoidValue ();
        }

        final ListValue result = new ListValue ();

        for ( final String entry : removed )
        {
            result.add ( new StringValue ( entry ) );
        }

        return result;
    }

    public static Set<String> fromValueRemoved ( final Value value )
    {
        if ( ! ( value instanceof ListValue ) )
        {
            return null;
        }

        final Set<String> removed = new HashSet<String> ();
        for ( final Value entryValue : ( (ListValue)value ).getValues () )
        {
            if ( entryValue instanceof StringValue )
            {
                removed.add ( ( (StringValue)entryValue ).getValue () );
            }
        }

        return removed;
    }

    public static Message createRequestList ( final boolean flag )
    {
        final Message message = new Message ( flag ? Messages.CC_HD_START_LIST : Messages.CC_HD_STOP_LIST );
        return message;
    }
}
