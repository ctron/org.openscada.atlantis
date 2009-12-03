package org.openscada.da.master.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.utils.str.StringHelper;
import org.osgi.framework.BundleContext;

public class CommonSumHandler extends AbstractHandlerImpl
{

    private final Pattern pattern;

    private final String tag;

    private final String prefix = "osgi.source";

    public CommonSumHandler ( final BundleContext context, final String tag, final int priority )
    {
        super ( context, priority );
        this.tag = tag;
        this.pattern = Pattern.compile ( ".*\\." + this.tag + "$" );
    }

    @Override
    public DataItemValue dataUpdate ( final DataItemValue value )
    {
        final Builder builder = new DataItemValue.Builder ( value );

        // convert source errors
        convertSource ( builder );

        // sum up
        final Set<String> matches = new HashSet<String> ();
        for ( final Map.Entry<String, Variant> entry : builder.getAttributes ().entrySet () )
        {
            final Variant pValue = entry.getValue ();
            if ( this.pattern.matcher ( entry.getKey () ).matches () && pValue != null && pValue.asBoolean () )
            {
                matches.add ( entry.getKey () );
            }
        }

        builder.setAttribute ( this.tag, new Variant ( !matches.isEmpty () ) );
        builder.setAttribute ( this.tag + ".count", new Variant ( matches.size () ) );
        builder.setAttribute ( this.tag + ".items", new Variant ( StringHelper.join ( matches, ", " ) ) );

        return builder.build ();
    }

    private void convertSource ( final Builder builder )
    {
        Variant sourceValue;

        sourceValue = builder.getAttributes ().get ( this.tag );
        if ( sourceValue != null )
        {
            builder.setAttribute ( String.format ( "%s.%s", this.prefix, this.tag ), sourceValue );
        }

        sourceValue = builder.getAttributes ().get ( this.tag + ".count" );
        if ( sourceValue != null )
        {
            builder.setAttribute ( String.format ( "%s.%s.count", this.prefix, this.tag ), sourceValue );
        }

        sourceValue = builder.getAttributes ().get ( this.tag + ".items" );
        if ( sourceValue != null )
        {
            builder.setAttribute ( String.format ( "%s.%s.items", this.prefix, this.tag ), sourceValue );
        }
    }

}