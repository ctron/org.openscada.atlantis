package org.openscada.hd;

public interface Query
{
    public void close ();

    public void updateParameters ( QueryParameters parameters );
}
