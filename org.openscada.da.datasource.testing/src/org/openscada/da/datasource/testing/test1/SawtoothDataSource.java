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

import org.eclipse.scada.core.Variant;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class SawtoothDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private Long startIndex;

    private double factor;

    private int cap;

    private int precision;

    private Variant lastValue;

    public SawtoothDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        if ( this.startIndex == null )
        {
            this.startIndex = System.currentTimeMillis ();
        }

        final long now = System.currentTimeMillis ();
        final long ms = ( now - this.startIndex ) / this.precision % this.cap;

        final double value = ms * this.factor;

        setValue ( Variant.valueOf ( value ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.factor = getDouble ( properties, "factor", 1 );
        this.cap = getInteger ( properties, "cap", 1000 );
        this.precision = getInteger ( properties, "precision", 1000 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        if ( this.lastValue == null || !this.lastValue.equals ( variant ) )
        {
            this.lastValue = variant;
            final Map<String, Variant> attributes = new HashMap<String, Variant> ( 1 );
            attributes.put ( "timestamp", Variant.valueOf ( System.currentTimeMillis () + this.timeDiff ) );
            updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
        }
    }

}
