package org.openscada.da.server.jdbc.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.jdbc.Connection;

public class QueryProcessor
{
    private final String sql;

    private final Connection connection;

    private String[] fields;

    public QueryProcessor ( final Connection connection, final String sql, final String...fields )
    {
        this.connection = connection;
        this.sql = sql;
        this.fields = fields;
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
        java.sql.Connection connection = this.connection.getConnection ();

        Map<String, Object> resultVars = new HashMap<String, Object> ();
        
        try
        {
            PreparedStatement stmt = connection.prepareStatement ( this.sql );
            try
            {
                ResultSet result = stmt.executeQuery ();
                if ( result.next () )
                {
                    for ( String field : this.fields )
                    {
                        Object value = getField ( result, field );
                        if ( value != null )
                        {
                            resultVars.put ( field, value );
                        }
                    }
                }
                result.close ();
            }
            finally
            {
                stmt.close ();
            }
        }
        finally
        {
            connection.close ();
        }

        return null;
    }

    private Object getField ( ResultSet result, String field )
    {
        try
        {
            return result.getObject ( field );
        }
        catch ( SQLException e )
        {
            return null;
        }
    }

}
