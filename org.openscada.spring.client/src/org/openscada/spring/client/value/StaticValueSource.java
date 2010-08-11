package org.openscada.spring.client.value;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class StaticValueSource extends AbstractBaseValueSource implements ValueSource, InitializingBean, DisposableBean
{
    private Map<String, Variant> attributes = new HashMap<String, Variant> ();

    private Variant value = new Variant ();

    private SubscriptionState subscriptionState = SubscriptionState.DISCONNECTED;

    public void afterPropertiesSet () throws Exception
    {
        this.subscriptionState = SubscriptionState.CONNECTED;
    }

    public void destroy () throws Exception
    {
        this.subscriptionState = SubscriptionState.DISCONNECTED;
    }

    public void setAttributes ( final Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }

    public DataItemValue getValue ()
    {
        final DataItemValue.Builder builder = new Builder ();
        builder.setValue ( this.value );
        builder.setAttributes ( this.attributes );
        builder.setSubscriptionState ( this.subscriptionState );
        return builder.build ();
    }

    public void setValue ( final Variant value )
    {
        this.value = value;
    }

    public void setSubscriptionState ( final SubscriptionState subscriptionState )
    {
        this.subscriptionState = subscriptionState;
    }

}
