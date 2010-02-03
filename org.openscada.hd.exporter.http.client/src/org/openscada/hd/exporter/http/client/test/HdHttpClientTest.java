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
        List<DataPoint> d = new HdHttpClient ( "http://localhost:8080/org.openscada.hd/items", 10, TimeUnit.SECONDS ).getData ( "FOO", "AVG", from, to, 300 );
        for ( DataPoint dataPoint : d )
        {
            System.out.println ( dataPoint );
        }
    }
}
