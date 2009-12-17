package org.openscada.ae;

public interface QueryListener
{
    public void queryData ( Event[] events );

    public void queryStateChanged ( QueryState state );
}
