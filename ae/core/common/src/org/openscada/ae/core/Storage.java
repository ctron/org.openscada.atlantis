package org.openscada.ae.core;

import java.util.Properties;

import org.openscada.core.InvalidSessionException;

public interface Storage
{
    public Session createSession ( Properties properties );
    public void closeSession ( Session session ) throws InvalidSessionException;
    
    void subscribe ( Session session, String queryID, Listener listener, int maxBatchSize, int archiveSet ) throws InvalidSessionException, NoSuchQueryException;
    void unsubscribe ( Session session, String queryID, Listener listener ) throws InvalidSessionException, NoSuchQueryException;
    Event[] read ( Session session, String queryID ) throws InvalidSessionException, NoSuchQueryException;

    QueryDescription[] getQueries ( Session session ) throws InvalidSessionException;
    
    public void submitEvent ( Properties properties, Event event ) throws Exception;
}
