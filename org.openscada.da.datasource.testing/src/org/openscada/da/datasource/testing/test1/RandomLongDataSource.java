/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class RandomLongDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private final Random r;

    private long variance;

    private long lastSwitch = System.currentTimeMillis ();

    @SuppressWarnings ( "unused" )
    private long offset;

    private long switchDelay = 10 * 1000;

    public RandomLongDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
        this.r = new Random ();
    }

    @Override
    protected void tick ()
    {

        final long now = System.currentTimeMillis ();

        if ( now - this.lastSwitch > this.switchDelay )
        {
            this.lastSwitch = now;
            this.offset += this.r.nextLong () % this.variance;
        }

        setValue ( Variant.valueOf ( this.r.nextLong () % this.variance ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.variance = getLong ( properties, "variance", 100 );
        this.switchDelay = getLong ( properties, "switchDelay", 10 * 1000 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "timestamp", Variant.valueOf ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
