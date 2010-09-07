/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.datasource.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;

public class WriterController
{
    private final ObjectPoolTracker tracker;

    private volatile Map<String, SingleObjectPoolServiceTracker> trackers = Collections.emptyMap ();

    public WriterController ( final ObjectPoolTracker tracker )
    {
        this.tracker = tracker;
    }

    public void setWriteItems ( final Set<String> datasources )
    {
        // create new tracker map
        final Map<String, SingleObjectPoolServiceTracker> newTrackers = new HashMap<String, SingleObjectPoolServiceTracker> ( 1 );
        for ( final String dataSourceId : datasources )
        {
            final SingleObjectPoolServiceTracker objectTracker = new SingleObjectPoolServiceTracker ( this.tracker, dataSourceId, null );
            objectTracker.open ();
            newTrackers.put ( dataSourceId, objectTracker );
        }

        // swap
        final Map<String, SingleObjectPoolServiceTracker> oldTrackers = this.trackers;
        this.trackers = newTrackers;

        // close old stuff
        for ( final SingleObjectPoolServiceTracker tracker : oldTrackers.values () )
        {
            tracker.close ();
        }
    }

    public void write ( final String dataSourceId, final Object value ) throws Exception
    {
        final SingleObjectPoolServiceTracker objectTracker = this.trackers.get ( dataSourceId );
        if ( objectTracker == null )
        {
            throw new IllegalArgumentException ( String.format ( "Data source '%s' is not configured", dataSourceId ) );
        }

        final Object o = objectTracker.getCurrentService ();
        if ( o == null )
        {
            throw new IllegalStateException ( String.format ( "Data source '%s' was not found", dataSourceId ) );
        }
        if ( ! ( o instanceof DataSource ) )
        {
            throw new IllegalStateException ( String.format ( "Data source '%s' is not a data source", dataSourceId ) );
        }

        final WriteInformation wi = new WriteInformation ( null );
        ( (DataSource)o ).startWriteValue ( wi, Variant.valueOf ( value ) );
    }

    public void writeAsText ( final String itemId, final String value ) throws Exception
    {
        final VariantEditor ve = new VariantEditor ();
        ve.setAsText ( value );
        write ( itemId, ve.getValue () );
    }
}
