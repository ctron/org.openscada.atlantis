package org.openscada.ae;


public interface QueryListener
{
    public void queryOpened ( Query query, long expectedCount );

    public void queryFailed ();

    public void queryData ( Event[] events );

    public void queryClosed ();
}
