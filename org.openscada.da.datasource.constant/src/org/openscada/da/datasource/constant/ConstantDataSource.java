/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.datasource.constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.VariantEditor;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.datasource.base.AbstractInputDataSource;

public class ConstantDataSource extends AbstractInputDataSource
{

    private final Executor executor;

    public ConstantDataSource ( final Executor executor )
    {
        this.executor = executor;
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        final Builder builder = new DataItemValue.Builder ();

        try
        {
            final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

            // set main value
            builder.setValue ( VariantEditor.toVariant ( cfg.getStringChecked ( "value", "'value' must be provided" ) ) );

            builder.setTimestamp ( makeTimestamp ( cfg ) );

            // set attributes
            for ( final Map.Entry<String, String> entry : cfg.getPrefixed ( "attributes." ).entrySet () )
            {
                builder.setAttribute ( entry.getKey (), VariantEditor.toVariant ( entry.getValue () ) );
            }

            // set as last step
            builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        }
        finally
        {
            updateData ( builder.build () );
        }
    }

    private Calendar makeTimestamp ( final ConfigurationDataHelper cfg ) throws ParseException
    {
        final Calendar c = Calendar.getInstance ();

        final String timeString = cfg.getString ( "timeString" );
        if ( timeString != null && !timeString.isEmpty () )
        {
            c.setTime ( new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.SSS" ).parse ( timeString ) );
        }

        final long timeDiff = cfg.getLong ( "timeDiff.value", 0 );
        final TimeUnit timeDiffUnit = TimeUnit.valueOf ( cfg.getString ( "timeDiff.unit", TimeUnit.MILLISECONDS.toString () ) );

        c.add ( Calendar.MILLISECOND, (int)TimeUnit.MILLISECONDS.convert ( timeDiff, timeDiffUnit ) );

        return c;
    }
}
