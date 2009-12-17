package org.openscada.ae.server;

import java.util.Date;

import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.UnknownQueryException;
import org.openscada.core.InvalidSessionException;

public interface Service extends org.openscada.core.server.Service
{
    // Event methods - online

    public void subscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException;

    // Event methods - offline

    public Query createQuery ( Session session, String queryType, String queryData, QueryListener listener ) throws InvalidSessionException;

    // Condition methods

    public void subscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException;

    public void acknowledge ( Session session, String conditionId, Date aknTimestamp ) throws InvalidSessionException;
}
