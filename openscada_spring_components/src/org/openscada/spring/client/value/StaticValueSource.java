package org.openscada.spring.client.value;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class StaticValueSource implements ValueSource, InitializingBean, DisposableBean
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
        final DataItemValue value = new DataItemValue ();
        value.setValue ( this.value );
        value.setAttributes ( this.attributes );
        value.setSubscriptionState ( this.subscriptionState );
        return value;
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
