package org.openscada.hd.exporter.http;

import java.util.Date;
import java.util.List;

public interface HttpExporter
{
    List<String> getItems () throws Exception;

    List<String> getSeries ( String itemId ) throws Exception;

    List<DataPoint> getData ( String item, String type, Date from, Date to, Integer number ) throws Exception;
}
