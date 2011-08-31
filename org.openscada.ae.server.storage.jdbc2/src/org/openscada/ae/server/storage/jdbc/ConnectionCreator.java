package org.openscada.ae.server.storage.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionCreator
{
    public Connection createConnection () throws SQLException
    {
        final String url = System.getProperty ( "org.openscada.ae.server.storage.jdbc.url", "" );
        final String user = System.getProperty ( "org.openscada.ae.server.storage.jdbc.username", "" );
        final String password = System.getProperty ( "org.openscada.ae.server.storage.jdbc.password", "" );

        final Connection connection = DriverManager.getConnection ( url, user, password );
        connection.setAutoCommit ( false );
        return connection;
    }

    public void init () throws ClassNotFoundException
    {
        final String driver = System.getProperty ( "org.openscada.ae.server.storage.jdbc.driver", "" );
        Class.forName ( driver );
    }

}
