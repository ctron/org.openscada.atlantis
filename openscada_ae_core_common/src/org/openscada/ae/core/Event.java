package org.openscada.ae.core;

public class Event
{
    private String id;

    private String source;

    private String type;

    public String getId ()
    {
        return this.id;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getSource ()
    {
        return this.source;
    }

    public void setSource ( final String source )
    {
        this.source = source;
    }

    public String getType ()
    {
        return this.type;
    }

    public void setType ( final String type )
    {
        this.type = type;
    }

}
