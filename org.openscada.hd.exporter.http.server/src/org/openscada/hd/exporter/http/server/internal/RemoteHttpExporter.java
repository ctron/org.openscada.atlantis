package org.openscada.hd.exporter.http.server.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;

public class RemoteHttpExporter implements HttpExporter
{
    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
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
