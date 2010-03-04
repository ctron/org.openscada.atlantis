package org.openscada.ae.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class ConditionMessageHelper
{

    public static ConditionStatusInformation[] fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }

        final List<ConditionStatusInformation> result = new ArrayList<ConditionStatusInformation> ();

        final ListValue value = (ListValue)baseValue;

        for ( final Value entryValue : value.getValues () )
        {
            final ConditionStatusInformation entry = fromValueEntry ( entryValue );
            if ( entry != null )
            {
                result.add ( entry );
            }
        }

        if ( result.isEmpty () )
        {
            return null;
        }

        return result.toArray ( new ConditionStatusInformation[result.size ()] );
    }

    private static ConditionStatusInformation fromValueEntry ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        final MapValue value = (MapValue)entryValue;
        try
        {

            final String id = ( (StringValue)value.get ( "id" ) ).getValue ();
            final Variant currentValue = MessageHelper.valueToVariant ( value.get ( "value" ), null );

            Date lastAknTimestamp = null;
            final LongValue lastAknTimestampValue = (LongValue)value.get ( "lastAknTimestamp" );
            if ( lastAknTimestampValue != null )
            {
                lastAknTimestamp = new Date ( lastAknTimestampValue.getValue () );
            }

            String lastAknUser = null;
            final StringValue lastAknUserValue = (StringValue)value.get ( "lastAknUser" );
            if ( lastAknUserValue != null )
            {
                lastAknUser = lastAknUserValue.getValue ();
            }

            Date statusTimestamp = null;
            statusTimestamp = new Date ( ( (LongValue)value.get ( "statusTimestamp" ) ).getValue () );
            // get status
            final ConditionStatus status = ConditionStatus.valueOf ( ( (StringValue)value.get ( "status" ) ).getValue () );
            if ( status == null )
            {
                return null;
            }

            return new ConditionStatusInformation ( id, status, statusTimestamp, currentValue, lastAknTimestamp, lastAknUser );
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

    public static Value toValue ( final ConditionStatusInformation[] added )
    {
        final ListValue result = new ListValue ();

        if ( added != null )
        {
            for ( final ConditionStatusInformation condition : added )
            {
                result.add ( toValue ( condition ) );
            }
        }

        return result;
    }

    private static Value toValue ( final ConditionStatusInformation condition )
    {
        final MapValue value = new MapValue ();

        value.put ( "id", new StringValue ( condition.getId () ) );
        value.put ( "status", new StringValue ( condition.getStatus ().toString () ) );
        final Value currentValue = MessageHelper.variantToValue ( condition.getValue () );
        if ( currentValue != null )
        {
            value.put ( "value", currentValue );
        }
        value.put ( "lastAknUser", new StringValue ( condition.getLastAknUser () ) );
        if ( condition.getStatusTimestamp () != null )
        {
            value.put ( "statusTimestamp", new LongValue ( condition.getStatusTimestamp ().getTime () ) );
        }
        if ( condition.getLastAknTimestamp () != null )
        {
            value.put ( "lastAknTimestamp", new LongValue ( condition.getLastAknTimestamp ().getTime () ) );
        }

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

    public static String[] fromValueRemoved ( final Value value )
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
