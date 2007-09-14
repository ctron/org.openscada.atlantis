package org.openscada.da.server.spring.tools.csv;

public class ItemEntry
{
    private String _id;
    private boolean _readable = false;
    private boolean _writeable = false;
    private String _description = "";

    public String getId ()
    {
        return _id;
    }

    public void setId ( String id )
    {
        _id = id;
    }

    public boolean isReadable ()
    {
        return _readable;
    }

    public void setReadable ( boolean readable )
    {
        _readable = readable;
    }

    public boolean isWriteable ()
    {
        return _writeable;
    }

    public void setWriteable ( boolean writeable )
    {
        _writeable = writeable;
    }

    public String getDescription ()
    {
        return _description;
    }

    public void setDescription ( String description )
    {
        _description = description;
    }
}
