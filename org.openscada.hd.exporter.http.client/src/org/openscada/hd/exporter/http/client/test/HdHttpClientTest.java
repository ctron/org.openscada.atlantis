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

package org.openscada.hd.exporter.http.client.test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.Utils;
import org.openscada.hd.exporter.http.client.HdHttpClient;

public class HdHttpClientTest
{
    @Test
    public void testClient () throws Exception
    {
        final Date from = Utils.isoDateFormat.parse ( "2005-01-01 13:00:00.000" );
        final Date to = Utils.isoDateFormat.parse ( "2005-08-01 13:00:00.000" );
        final List<DataPoint> d = new HdHttpClient ( "http://localhost:8080/org.openscada.hd/items", 10, TimeUnit.SECONDS ).getData ( "FOO", "AVG", from, to, 300 );
        for ( final DataPoint dataPoint : d )
        {
            System.out.println ( dataPoint );
        }
    }
}
