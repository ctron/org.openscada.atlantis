package org.openscada.ae.server.storage.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import org.openscada.ae.server.storage.Storage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private static BundleContext context;

    private JdbcStorage jdbcStorage;

    private Connection connection;

    private ServiceRegistration jdbcStorageHandle;

    private int maxLength = 4000;

    static BundleContext getContext ()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;
        this.connection = createConnection ();
        jdbcStorage = createJdbcStorage ( connection );
        jdbcStorage.start ();

        Hashtable<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "JDBC implementation for org.openscada.ae.server.storage.Storage" );
        jdbcStorageHandle = context.registerService ( new String[] { JdbcStorage.class.getName (), Storage.class.getName () }, jdbcStorage, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext bundleContext ) throws Exception
    {
        if ( jdbcStorageHandle != null )
        {
            jdbcStorageHandle.unregister ();
            jdbcStorageHandle = null;
        }
        if ( jdbcStorage != null )
        {
            jdbcStorage.stop ();
            jdbcStorage = null;
        }
        if ( connection != null )
        {
            connection.close ();
            connection = null;
        }
        Activator.context = null;
    }

    private Connection createConnection () throws SQLException, ClassNotFoundException
    {
        String driver = System.getProperty ( "org.openscada.ae.server.storage.jdbc.driver", "" );
        String url = System.getProperty ( "org.openscada.ae.server.storage.jdbc.url", "" );
        String user = System.getProperty ( "org.openscada.ae.server.storage.jdbc.username", "" );
        String password = System.getProperty ( "org.openscada.ae.server.storage.jdbc.password", "" );

        Class.forName ( driver );
        Connection connection = DriverManager.getConnection ( url, user, password );
        connection.setAutoCommit ( false );
        return connection;
    }

    private JdbcStorage createJdbcStorage ( Connection connection )
    {
        JdbcStorage jdbcStorage = new JdbcStorage ();
        StorageDao storageDao;
        if ( "legacy".equals ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.instance", "" ) ) )
        {
            LegacyJdbcStorageDao jdbcStorageDao = new LegacyJdbcStorageDao ();
            jdbcStorageDao.setMaxLength ( Integer.getInteger ( "org.openscada.ae.server.storage.jdbc.maxlength", maxLength ) );
            if ( !System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema", "" ).trim ().isEmpty () )
            {
                jdbcStorageDao.setSchema ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema" ) + "." );
            }
            jdbcStorageDao.setConnection ( connection );
            storageDao = jdbcStorageDao;
        }
        else
        {
            JdbcStorageDao jdbcStorageDao = new JdbcStorageDao ();
            jdbcStorageDao.setInstance ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.instance", "default" ) );
            jdbcStorageDao.setMaxLength ( Integer.getInteger ( "org.openscada.ae.server.storage.jdbc.maxlength", maxLength ) );
            if ( !System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema", "" ).trim ().isEmpty () )
            {
                jdbcStorageDao.setSchema ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema" ) + "." );
            }
            jdbcStorageDao.setConnection ( connection );
            storageDao = jdbcStorageDao;
        }
        jdbcStorage.setJdbcStorageDao ( storageDao );
        return jdbcStorage;
    }
}
