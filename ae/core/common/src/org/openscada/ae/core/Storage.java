package org.openscada.ae.core;

import java.util.Properties;

public interface Storage
{
    public Session createSession ( Properties properties );
    public void closeSession ( Session session );
    
    void subscribe ( Session session, String queryID, Listener listener );
    void unsubscribe ( Session session, String queryID );
    Event[] read ( Session session, String queryID );

    QueryDescription[] getQueries ( Session session );
    
    public void submitEvent ( AttributedIdentifier event );
}
