package org.openscada.io.lcl.data;

public class Response
{
    private int _code = 0;
    private String _data = "";
    
    public Response ()
    {
        super ();
    }
    
    public Response ( int code, String data )
    {
        super ();
        _code = code;
        _data = data;
    }
    
    public int getCode ()
    {
        return _code;
    }
    
    public void setCode ( int code )
    {
        _code = code;
    }
    
    public String getData ()
    {
        return _data;
    }
    
    public void setData ( String data )
    {
        _data = data;
    }
}
