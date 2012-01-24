package org.openscada.ae.server.storage.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.openscada.ae.Event;
import org.openscada.core.VariantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseStorageDao implements StorageDao
{
    private static final Logger logger = LoggerFactory.getLogger ( BaseStorageDao.class );

    private String schema = "";

    private int maxLength = 4000;

    private String instance = "default";

    private DataSource dataSource;

    public void setSchema ( final String schema )
    {
        this.schema = schema;
    }

    public String getSchema ()
    {
        return this.schema;
    }

    public void setMaxLength ( final int maxLength )
    {
        this.maxLength = maxLength;
    }

    public int getMaxLength ()
    {
        return this.maxLength;
    }

    public void setInstance ( final String instance )
    {
        this.instance = instance;
    }

    public String getInstance ()
    {
        return this.instance;
    }

    public void setDataSource ( final DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource ()
    {
        return this.dataSource;
    }

    public Connection createConnection () throws SQLException
    {
        final Connection connection = this.dataSource.getConnection ();
        connection.setAutoCommit ( false );
        return connection;
    }

    public void closeStatement ( final Statement statement )
    {
        try
        {
            if ( statement == null || statement.isClosed () )
            {
                return;
            }
            statement.close ();
        }
        catch ( final SQLException e )
        {
            logger.debug ( "Exception on closing statement", e );
        }
    }

    public void closeConnection ( final Connection connection )
    {
        try
        {
            if ( connection == null || connection.isClosed () )
            {
                return;
            }
            connection.close ();
        }
        catch ( final SQLException e )
        {
            logger.debug ( "Exception on closing statement", e );
        }
    }

    @Override
    public void updateComment ( final UUID id, final String comment ) throws Exception
    {
        final Connection con = createConnection ();
        try
        {

            final PreparedStatement stm1 = con.prepareStatement ( String.format ( getDeleteAttributesSql (), getSchema () ) );
            try
            {
                stm1.setString ( 1, id.toString () );
                stm1.setString ( 2, Event.Fields.COMMENT.getName () );
                stm1.addBatch ();
                stm1.execute ();

                final PreparedStatement stm2 = con.prepareStatement ( String.format ( getInsertAttributesSql (), getSchema () ) );
                try
                {
                    stm2.setString ( 1, id.toString () );
                    stm2.setString ( 2, Event.Fields.COMMENT.getName () );
                    stm2.setString ( 3, VariantType.STRING.name () );
                    stm2.setString ( 4, clip ( getMaxLength (), comment ) );
                    stm2.setLong ( 5, (Long)null );
                    stm2.setDouble ( 6, (Double)null );
                    stm2.addBatch ();
                    stm2.execute ();

                    con.commit ();
                }
                finally
                {
                    closeStatement ( stm2 );
                }
            }
            finally
            {
                closeStatement ( stm1 );
            }
        }
        finally
        {
            closeConnection ( con );
        }
    }

    protected String clip ( final int i, final String string )
    {
        if ( string == null )
        {
            return null;
        }
        if ( i < 1 || string.length () <= i )
        {
            return string;
        }
        return string.substring ( 0, i );
    }

    protected abstract String getDeleteAttributesSql ();

    protected abstract String getInsertAttributesSql ();
}
