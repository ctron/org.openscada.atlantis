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
        HttpClient client = new HttpClient ();
        HttpMethod method = new GetMethod ( this.baseUrl + "/" + URLEncoder.encode ( item, "UTF-8" ) + "/" + URLEncoder.encode ( type, "UTF-8" ) + "?from=" + URLEncoder.encode ( Utils.isoDateFormat.format ( from ), "UTF-8" ) + "&to=" + URLEncoder.encode ( Utils.isoDateFormat.format ( to ), "UTF-8" ) + "&no=" + number );
        client.getParams ().setSoTimeout ( (int)this.timeout );
        try
        {
            int status = client.executeMethod ( method );
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
