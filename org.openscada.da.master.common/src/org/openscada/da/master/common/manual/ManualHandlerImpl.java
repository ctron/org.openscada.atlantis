package org.openscada.da.master.common.manual;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ManualHandlerImpl extends AbstractCommonHandlerImpl
{
    private Variant value = Variant.NULL;

    private String user;

    private String reason;

    private Date timestamp;

    public ManualHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ManualHandlerFactoryImpl.FACTORY_ID, ManualHandlerFactoryImpl.FACTORY_ID );
    }

    @Override
    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );

        if ( this.value.isNull () )
        {
            return builder.build ();
        }

        // apply manual value : manual value is active

        final String user = this.user;
        final String reason = this.reason;
        final Date timestamp = this.timestamp;

        final Variant originalError = builder.getAttributes ().remove ( "error" );
        builder.setAttribute ( getPrefixed ( "error.original" ), originalError );
        builder.setAttribute ( "error", Variant.FALSE );
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setSubscriptionError ( null );

        final Variant originalErrorCount = builder.getAttributes ().remove ( "error.count" );
        if ( originalErrorCount != null )
        {
            builder.setAttribute ( "error.count", new Variant ( 0 ) );
            builder.setAttribute ( getPrefixed ( "error.count.original" ), originalErrorCount );
        }

        final Variant originalErrorItems = builder.getAttributes ().remove ( "error.items" );
        if ( originalErrorItems != null )
        {
            builder.setAttribute ( "error.items", new Variant ( "" ) );
            builder.setAttribute ( getPrefixed ( "error.items.original" ), originalErrorItems );
        }

        builder.setAttribute ( getPrefixed ( "value.original" ), value.getValue () );
        builder.setAttribute ( getPrefixed ( "active" ), Variant.TRUE );

        builder.setValue ( this.value );

        if ( user != null )
        {
            builder.setAttribute ( getPrefixed ( "user" ), new Variant ( user ) );
        }
        if ( reason != null )
        {
            builder.setAttribute ( getPrefixed ( "reason" ), new Variant ( reason ) );
        }
        if ( timestamp != null )
        {
            final Variant originalTimestamp = builder.getAttributes ().get ( "timestamp" );
            builder.setAttribute ( "timestamp", new Variant ( timestamp.getTime () ) );
            if ( originalTimestamp != null )
            {
                builder.setAttribute ( getPrefixed ( "timestamp.original" ), originalTimestamp );
            }
        }

        return builder.build ();
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final VariantEditor ve = new VariantEditor ();

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String str = cfg.getString ( "value" );
        if ( str != null )
        {
            ve.setAsText ( str );
            this.value = (Variant)ve.getValue ();
            if ( this.value == null )
            {
                this.value = Variant.NULL;
            }
        }
        else
        {
            this.value = Variant.NULL;
        }
        this.user = cfg.getString ( "user" );
        this.reason = cfg.getString ( "reason" );
        this.timestamp = new Date ( cfg.getLong ( "timestamp", System.currentTimeMillis () ) );

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( "active" ), this.value.isNull () ? Variant.FALSE : Variant.TRUE );
        builder.setAttribute ( getPrefixed ( "value" ), this.value );
        builder.setAttribute ( getPrefixed ( "reason" ), new Variant ( this.reason ) );
        builder.setAttribute ( getPrefixed ( "user" ), new Variant ( this.user ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant value = attributes.get ( "value" );

        if ( value != null )
        {
            data.put ( "value", value.toString () );
        }

        if ( writeInformation.getUserInformation () != null )
        {
            data.put ( "user", writeInformation.getUserInformation ().getName () );
        }
        else
        {
            data.put ( "user", "" );
        }

        if ( value != null )
        {
            // clear user, reason and timestamp if we have a value

            data.put ( "reason", "" );
            data.put ( "timestamp", "" + System.currentTimeMillis () );
        }

        final Variant reason = attributes.get ( "reason" );
        if ( reason != null && !reason.isNull () )
        {
            data.put ( "reason", reason.toString () );
        }

        final Variant timestamp = attributes.get ( "timestamp" );
        if ( timestamp != null && !timestamp.isNull () )
        {
            data.put ( "timestamp", "" + timestamp.asLong ( System.currentTimeMillis () ) );
        }

        return updateConfiguration ( data, false );
    }

}
