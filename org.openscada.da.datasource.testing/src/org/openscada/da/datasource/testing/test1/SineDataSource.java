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

package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class SineDataSource extends ScheduledDataSource implements DefaultDataSource
{

    private double factor1;

    private double factor2;

    private double amp1;

    private double amp2;

    public SineDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        final double time = System.currentTimeMillis () / 1000.0;

        final double value = this.amp1 * Math.sin ( time / this.factor1 ) + this.amp2 * Math.sin ( time / this.factor2 );
        setValue ( Variant.valueOf ( value ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.timeDiff = getInteger ( properties, "time.diff", 0 );
        this.factor1 = getDouble ( properties, "factor1", 2000.0 );
        this.factor2 = getDouble ( properties, "factor2", 20.0 );

        this.amp1 = getDouble ( properties, "amp1", 100 );
        this.amp2 = getDouble ( properties, "amp2", 5 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 1 );
        attributes.put ( "timestamp", Variant.valueOf ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
