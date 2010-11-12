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

package org.openscada.hd.exporter.http.client;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;
import org.openscada.hd.exporter.http.Utils;

public class HdHttpClient implements HttpExporter
{
    private final String baseUrl;

    private final long timeout;

    public HdHttpClient ( final String url, final long timeout, final TimeUnit timeUnit )
    {
        this.baseUrl = url;
        this.timeout = TimeUnit.MILLISECONDS.convert ( timeout, timeUnit );
    }

    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number ) throws Exception
    {
        final HttpClient client = new HttpClient ();
        final HttpMethod method = new GetMethod ( this.baseUrl + "/" + URLEncoder.encode ( item, "UTF-8" ) + "/" + URLEncoder.encode ( type, "UTF-8" ) + "?from=" + URLEncoder.encode ( Utils.isoDateFormat.format ( from ), "UTF-8" ) + "&to=" + URLEncoder.encode ( Utils.isoDateFormat.format ( to ), "UTF-8" ) + "&no=" + number );
        client.getParams ().setSoTimeout ( (int)this.timeout );
        try
        {
            final int status = client.executeMethod ( method );
            if ( status != HttpStatus.SC_OK )
            {
                throw new RuntimeException ( "Method failed with error " + status + " " + method.getStatusLine () );
            }
            return Utils.fromJson ( method.getResponseBodyAsString () );
        }
        finally
        {
            method.releaseConnection ();
        }
    }

    public List<String> getItems () throws Exception
    {
        throw new IllegalArgumentException ( "not implemented" );
    }

    public List<String> getSeries ( final String itemId ) throws Exception
    {
        throw new IllegalArgumentException ( "not implemented" );
    }
}
