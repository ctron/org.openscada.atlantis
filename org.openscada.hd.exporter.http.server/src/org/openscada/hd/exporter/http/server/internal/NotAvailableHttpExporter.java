package org.openscada.hd.exporter.http.server.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;

/**
 * just a placeholder for the case that no exporter is available
 * @author jrose
 */
public class NotAvailableHttpExporter implements HttpExporter
{
    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
        return new ArrayList<DataPoint> ();
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
