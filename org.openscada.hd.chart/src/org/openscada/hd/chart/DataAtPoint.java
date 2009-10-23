package org.openscada.hd.chart;

import java.util.Date;
import java.util.Map;

public interface DataAtPoint
{

    double getQuality ( int currentX );

    double getManual ( int currentX );

    Date getTimestamp ( int currentX );

    long getSourceValues ( int currentX );

    Map<String, Double> getData ( int currentX );

}
