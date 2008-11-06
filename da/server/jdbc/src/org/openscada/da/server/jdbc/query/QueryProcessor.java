package org.openscada.da.server.jdbc.query;

import java.util.Map;

public class QueryProcessor
{
    private final String uri;

    private final String sql;

    public QueryProcessor ( final String uri, final String sql )
    {
        this.uri = uri;
        this.sql = sql;
    }

    public void activate ()
    {

    }

    public void deactivate ()
    {

    }

    /**
     * Perform the query
     * <p>
     * The method may only be called after {@link #activate()} and before {@link #deactivate()}.
     * Otherwise the behaviour is undefined.
     *  
     * @return the data retrieved but never <code>null</code>
     * @throws Exception in case anything went wrong
     */
    public Map<String, Object> doQuery () throws Exception
    {
        return null;
    }

}
