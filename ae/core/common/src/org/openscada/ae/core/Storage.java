package org.openscada.ae.core;

import java.util.Properties;

import org.openscada.core.InvalidSessionException;

public interface Storage
{
    public Session createSession ( Properties properties );
    public void closeSession ( Session session ) throws InvalidSessionException;
    
    void subscribe ( Session session, String queryID, Listener listener ) throws InvalidSessionException;
    void unsubscribe ( Session session, String queryID ) throws InvalidSessionException;
    Event[] read ( Session session, String queryID ) throws InvalidSessionException;

    QueryDescription[] getQueries ( Session session ) throws InvalidSessionException;
    
    public void submitEvent ( AttributedIdentifier event );
}
