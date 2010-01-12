package org.openscada.da.server.stock.domain;

import java.util.Calendar;

public class StockQuote
{
    private String _symbol = null;
    private Double _value = null;
    private Calendar _timestamp = null;
    private String _error = null;
    
    public String getSymbol ()
    {
        return _symbol;
    }
    public void setSymbol ( String symbol )
    {
        _symbol = symbol;
    }
    public Calendar getTimestamp ()
    {
        return _timestamp;
    }
    public void setTimestamp ( Calendar timestamp )
    {
        _timestamp = timestamp;
    }
    public Double getValue ()
    {
        return _value;
    }
    public void setValue ( Double value )
    {
        _value = value;
    }
    public String getError ()
    {
        return _error;
    }
    public void setError ( String error )
    {
        _error = error;
    }
}
