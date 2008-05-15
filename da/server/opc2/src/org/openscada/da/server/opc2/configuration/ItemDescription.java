package org.openscada.da.server.opc2.configuration;

public class ItemDescription
{
    private String id;
    private String description;
    private String accessPath;

    public String getId ()
    {
        return id;
    }
    
    public void setId ( String id )
    {
        this.id = id;
    }
    
    public String getDescription ()
    {
        return description;
    }
    
    public void setDescription ( String description )
    {
        this.description = description;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final ItemDescription other = (ItemDescription)obj;
        if ( id == null )
        {
            if ( other.id != null )
                return false;
        }
        else if ( !id.equals ( other.id ) )
            return false;
        return true;
    }

    public String getAccessPath ()
    {
        return accessPath;
    }

    public void setAccessPath ( String accessPath )
    {
        this.accessPath = accessPath;
    }
}
