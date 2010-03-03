package org.openscada.da.master.common.sum;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.str.StringHelper;

public class CommonSumHandler extends AbstractMasterHandlerImpl
{

    private final Pattern pattern;

    private final String tag;

    private String prefix = "osgi.source";

    private boolean debug = false;

    public CommonSumHandler ( final ObjectPoolTracker poolTracker, final String tag, final int priority )
    {
        super ( poolTracker, priority );
        this.tag = tag;
        this.pattern = Pattern.compile ( ".*\\." + this.tag + "$" );
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.debug = cfg.getBoolean ( "debug", false );
        this.prefix = cfg.getString ( "prefix", "osgi.source" );

        reprocess ();
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        final Builder builder = new DataItemValue.Builder ( value );

        // convert source errors
        convertSource ( builder );

        final Set<Object> contextSet = getContextSet ( context, this.tag );

        if ( this.debug )
        {
            builder.setAttribute ( this.prefix + ".before", new Variant ( StringHelper.join ( contextSet, "," ) ) );
        }

        // sum up
        final Set<String> matches = new HashSet<String> ();
        for ( final Map.Entry<String, Variant> entry : builder.getAttributes ().entrySet () )
        {
            final Variant pValue = entry.getValue ();
            final String name = entry.getKey ();
            if ( this.pattern.matcher ( name ).matches () && pValue != null && pValue.asBoolean () )
            {
                if ( !contextSet.contains ( name ) )
                {
                    matches.add ( name );
                    contextSet.add ( name );
                }
            }
        }

        if ( this.debug )
        {
            builder.setAttribute ( this.prefix + ".after", new Variant ( StringHelper.join ( contextSet, "," ) ) );
        }

        builder.setAttribute ( this.tag, Variant.valueOf ( !matches.isEmpty () ) );
        if ( this.debug )
        {
            builder.setAttribute ( this.tag + ".count", new Variant ( matches.size () ) );
            builder.setAttribute ( this.tag + ".items", new Variant ( StringHelper.join ( matches, ", " ) ) );
        }

        return builder.build ();
    }

    @SuppressWarnings ( "unchecked" )
    private static Set<Object> getContextSet ( final Map<String, Object> context, final String tag )
    {
        final Object o = context.get ( tag + ".set" );
        if ( o instanceof Set<?> )
        {
            return (Set<Object>)o;
        }
        else
        {
            final Set<Object> set = new HashSet<Object> ();
            context.put ( tag + ".set", set );
            return set;
        }
    }

    private void convertSource ( final Builder builder )
    {
        Variant sourceValue;

        sourceValue = builder.getAttributes ().remove ( this.tag );
        if ( sourceValue != null )
        {
            builder.setAttribute ( String.format ( "%s.%s", this.prefix, this.tag ), sourceValue );
        }

        sourceValue = builder.getAttributes ().remove ( this.tag + ".count" );
        if ( sourceValue != null && this.debug )
        {
            builder.setAttribute ( String.format ( "%s.%s.count", this.prefix, this.tag ), sourceValue );
        }

        sourceValue = builder.getAttributes ().remove ( this.tag + ".items" );
        if ( sourceValue != null && this.debug )
        {
            builder.setAttribute ( String.format ( "%s.%s.items", this.prefix, this.tag ), sourceValue );
        }
    }

}