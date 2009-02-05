package org.openscada.io.lcl.data;

public class Request
{
    private String _command ="";
    private String _data = "";
    
    public Request ()
    {
        super ();
    }
    
    public Request ( String command, String data )
    {
        super ();
        _command = command;
        _data = data;
    }
    
    public String getCommand ()
    {
        return _command;
    }
    
    public void setCommand ( String command )
    {
        _command = command.toUpperCase ();
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
