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

package org.openscada.hd.exporter.http.server.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.openscada.utils.concurrent.AbstractFuture;

public class LocalHttpExporter implements HttpExporter
{
    private class QueryFuture extends AbstractFuture<List<DataPoint>> implements QueryListener
    {
        final List<DataPoint> result = new ArrayList<DataPoint> ();

        final String type;

        public QueryFuture ( final String type )
        {
            this.type = type;
        }

        public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
        {
            int i = 0;
            for ( final ValueInformation vi : valueInformation )
            {
                final DataPoint dp = new DataPoint ();
                dp.setQuality ( vi.getQuality () );
                dp.setManual ( vi.getManualPercentage () );
                dp.setTimestamp ( vi.getStartTimestamp ().getTime () );
                dp.setValue ( values.get ( this.type )[i].toDouble () );
                this.result.add ( dp );
                i++;
            }
        }

        public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
        {
        }

        public void updateState ( final QueryState state )
        {
            if ( state == QueryState.COMPLETE || state == QueryState.DISCONNECTED )
            {
                setResult ( this.result );
            }
        }
    }

    private final Service hdService;

    private final Session session;

    public LocalHttpExporter ( final Service hdService ) throws Exception
    {
        this.hdService = hdService;
        this.session = (Session)this.hdService.createSession ( new Properties () );
    }

    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
        final Calendar calFrom = new GregorianCalendar ();
        calFrom.setTime ( from );
        final Calendar calTo = new GregorianCalendar ();
        calTo.setTime ( to );

        final QueryParameters parameters = new QueryParameters ( calFrom, calTo, number );
        QueryFuture queryFuture = new QueryFuture ( type );
        Query q = null;
        try
        {
            q = this.hdService.createQuery ( this.session, item, parameters, queryFuture, false );
            final List<DataPoint> result = queryFuture.get ( 30, TimeUnit.SECONDS );
            q.close ();
            queryFuture = null;
            q = null;
            return result;
        }
        catch ( final Exception e )
        {
            queryFuture = null;
            if ( q != null )
            {
                q.close ();
                q = null;
            }
        }
        return null;
    }

    public List<String> getItems ()
    {
        return new ArrayList<String> ();
    }

    public List<String> getSeries ( final String itemId )
    {
        return new ArrayList<String> ();
    }
}
