/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.spring.client.value;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class StaticValueSource extends AbstractBaseValueSource implements ValueSource, InitializingBean, DisposableBean
{
    private Map<String, Variant> attributes = new HashMap<String, Variant> ();

    private Variant value = Variant.NULL;

    private SubscriptionState subscriptionState = SubscriptionState.DISCONNECTED;

    @Override
    public void afterPropertiesSet () throws Exception
    {
        this.subscriptionState = SubscriptionState.CONNECTED;
    }

    @Override
    public void destroy () throws Exception
    {
        this.subscriptionState = SubscriptionState.DISCONNECTED;
    }

    public void setAttributes ( final Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }

    @Override
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
