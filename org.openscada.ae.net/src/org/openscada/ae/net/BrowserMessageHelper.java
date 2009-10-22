package org.openscada.ae.net;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserType;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class BrowserMessageHelper
{

    public static BrowserEntry[] fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }

        List<BrowserEntry> result = new ArrayList<BrowserEntry> ();

        ListValue value = (ListValue)baseValue;

        for ( Value entryValue : value.getValues () )
        {
            BrowserEntry entry = fromValueEntry ( entryValue );
            if ( entry != null )
            {
                result.add ( entry );
            }
        }

        if ( result.isEmpty () )
        {
            return null;
        }

        return result.toArray ( new BrowserEntry[result.size ()] );
    }

    private static Set<BrowserType> getTypes ( final Value value )
    {
        EnumSet<BrowserType> result = EnumSet.noneOf ( BrowserType.class );

        if ( value instanceof ListValue )
        {
            for ( Value entry : ( (ListValue)value ).getValues () )
            {
                if ( entry instanceof StringValue )
                {
                    BrowserType type = BrowserType.valueOf ( ( (StringValue)entry ).getValue () );
                    if ( type != null )
                    {
                        result.add ( type );
                    }
                }
            }
        }
        if ( result.isEmpty () )
        {
            return null;
        }
        return result;
    }

    private static BrowserEntry fromValueEntry ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        MapValue value = (MapValue)entryValue;
        try
        {

            String id = ( ( (StringValue)value.get ( "id" ) ).getValue () );

            Set<BrowserType> types = getTypes ( value.get ( "types" ) );
            if ( types == null )
            {
                return null;
            }

            Value attributes = value.get ( "attributes" );

            if ( ! ( attributes instanceof MapValue ) )
            {
                return null;
            }

            return new BrowserEntry ( id, types, MessageHelper.mapToAttributes ( (MapValue)attributes ) );
        }
        catch ( ClassCastException e )
        {
            return null;
        }
        catch ( NullPointerException e )
        {
            return null;
        }
    }

    public static Value toValue ( final BrowserEntry[] added )
    {
        ListValue result = new ListValue ();

        if ( added != null )
        {
            for ( BrowserEntry entry : added )
            {
                result.add ( toValue ( entry ) );
            }
        }

        return result;
    }

    private static Value toValue ( final BrowserEntry entry )
    {
        MapValue value = new MapValue ();

        value.put ( "id", new StringValue ( entry.getId () ) );
        ListValue types = new ListValue ();
        for ( BrowserType type : entry.getTypes () )
        {
            types.add ( new StringValue ( type.toString () ) );
        }
        value.put ( "types", types );
        value.put ( "attributes", MessageHelper.attributesToMap ( entry.getAttributes () ) );

        return value;
    }

    public static Value toValue ( final String[] removed )
    {
        if ( removed == null )
        {
            return new VoidValue ();
        }

        ListValue result = new ListValue ();

        for ( String entry : removed )
        {
            result.add ( new StringValue ( entry ) );
        }

        return result;
    }

    public static String[] fromValueRemoved ( final Value value )
    {
        if ( ! ( value instanceof ListValue ) )
        {
            return null;
        }

        Set<String> removed = new HashSet<String> ();
        for ( Value entryValue : ( (ListValue)value ).getValues () )
        {
            if ( entryValue instanceof StringValue )
            {
                removed.add ( ( (StringValue)entryValue ).getValue () );
            }
        }

        if ( removed.isEmpty () )
        {
            return null;
        }
        else
        {
            return removed.toArray ( new String[0] );
        }
    }
}
