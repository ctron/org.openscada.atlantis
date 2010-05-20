/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class QualityDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private int timespan;

    private double value;

    public QualityDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        final long time = System.currentTimeMillis ();

        final Variant value = new Variant ( this.value );

        final boolean good = time / this.timespan % 2 == 0;

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "error", new Variant ( good ) );

        setValue ( new Variant ( value ), attributes );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.timespan = getInteger ( properties, "timestamp", 10 * 1000 );
        this.value = getDouble ( properties, "value", 0 );
        super.update ( properties );
    }

    protected void setValue ( final Variant variant, final Map<String, Variant> origAttributes )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( origAttributes );
        attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
