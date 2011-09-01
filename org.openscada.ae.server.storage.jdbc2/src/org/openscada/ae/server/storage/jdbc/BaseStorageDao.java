package org.openscada.ae.server.storage.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseStorageDao
{
    private static final Logger logger = LoggerFactory.getLogger ( BaseStorageDao.class );

    private ConnectionCreator connectionCreator;

    private String schema = "";

    private int maxLength = 4000;

    private String instance = "default";

    public void setConnectionCreator ( final ConnectionCreator connectionCreator )
    {
        this.connectionCreator = connectionCreator;
    }

    public ConnectionCreator getConnectionCreator ()
    {
        return connectionCreator;
    }

    public void setSchema ( final String schema )
    {
        this.schema = schema;
    }

    public String getSchema ()
    {
        return schema;
    }

    public void setMaxLength ( final int maxLength )
    {
        this.maxLength = maxLength;
    }

    public int getMaxLength ()
    {
        return maxLength;
    }

    public void setInstance ( final String instance )
    {
        this.instance = instance;
    }

    public String getInstance ()
    {
        return instance;
    }

    public void closeStatement ( Statement statement )
    {
        try
        {
            if ( statement == null || statement.isClosed () )
            {
                return;
            }
            statement.close ();
        }
        catch ( SQLException e )
        {
            logger.debug ( "Exception on closing statement", e );
        }
    }

    public void closeConnection ( Connection connection )
    {
        try
        {
            if ( connection == null || connection.isClosed () )
            {
                return;
            }
            connection.close ();
        }
        catch ( SQLException e )
        {
            logger.debug ( "Exception on closing statement", e );
        }
    }
}
