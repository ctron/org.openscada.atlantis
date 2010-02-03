package org.openscada.hd.exporter.http;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Utils
{
    public static final String isoDatePatterrn = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final DateFormat isoDateFormat = new SimpleDateFormat ( isoDatePatterrn );

    public static final Gson gson = new GsonBuilder ().setDateFormat ( DateFormat.FULL ).setDateFormat ( isoDatePatterrn ).create ();

    public static String toJson ( final List<DataPoint> items )
    {
        return gson.toJson ( items );
    }

    public static List<DataPoint> fromJson ( final String json )
    {
        Type dataPointListType = new TypeToken<List<DataPoint>> () {}.getType ();
        return gson.fromJson ( json, dataPointListType );
    }
}
