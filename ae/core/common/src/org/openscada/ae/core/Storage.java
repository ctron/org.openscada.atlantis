package org.openscada.ae.core;

import java.util.Properties;

import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;

public interface Storage extends Submission
{
    public Session createSession ( Properties properties ) throws UnableToCreateSessionException;
    public void closeSession ( Session session ) throws InvalidSessionException;
    
    void subscribe ( Session session, String queryID, Listener listener, int maxBatchSize, int archiveSet ) throws InvalidSessionException, NoSuchQueryException;
    void unsubscribe ( Session session, String queryID, Listener listener ) throws InvalidSessionException, NoSuchQueryException;
    Event[] read ( Session session, String queryID ) throws InvalidSessionException, NoSuchQueryException;

    long startList ( Session session, ListOperationListener listener ) throws InvalidSessionException;
    
    public void cancelOperation ( Session session, long id ) throws InvalidSessionException, CancellationNotSupportedException;
    public void thawOperation ( Session session, long id ) throws InvalidSessionException;
}
