/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.provider.jdbc;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.openscada.sec.utils.password.DigestValidator;
import org.openscada.sec.utils.password.HexCodec;
import org.openscada.sec.utils.password.PasswordValidator;
import org.openscada.sec.utils.password.PlainValidator;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.jdbc.DataSourceConnectionAccessor;
import org.openscada.utils.osgi.jdbc.DataSourceFactoryTracker;
import org.openscada.utils.osgi.jdbc.task.CommonConnectionTask;
import org.openscada.utils.osgi.jdbc.task.ConnectionContext;
import org.openscada.utils.osgi.jdbc.task.RowCallback;
import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;
import org.openscada.utils.str.StringHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcAuthenticationService implements AuthenticationService
{

    private static final StatusCode NO_ACCESSOR = new StatusCode ( "OSSEC", "JDBC", 0x0001, SeverityLevel.ERROR );

    private static final StatusCode INTERNAL_ERROR = new StatusCode ( "OSSEC", "JDBC", 0x0002, SeverityLevel.ERROR );

    private final static Logger logger = LoggerFactory.getLogger ( JdbcAuthenticationService.class );;

    private final String id;

    private final BundleContext context;

    private String driver;

    private DataSourceFactoryTracker tracker;

    private Properties connectionProperties;

    private DataSourceConnectionAccessor accessor;

    private final ReadWriteLock accessorLock = new ReentrantReadWriteLock ();

    private final Lock readLock = this.accessorLock.readLock ();

    private final Lock writeLock = this.accessorLock.writeLock ();

    public class PasswordCheckRowCallback implements RowCallback
    {
        private final String password;

        private boolean result;

        public PasswordCheckRowCallback ( final String password )
        {
            this.password = password;
        }

        public boolean isResult ()
        {
            return this.result;
        }

        @Override
        public void processRow ( final ResultSet resultSet ) throws SQLException
        {
            final String storedPassword = resultSet.getString ( "password" );

            if ( storedPassword == null || storedPassword.isEmpty () )
            {
                return;
            }

            if ( validatePassword ( this.password, storedPassword ) )
            {
                this.result = true;
            }
        }
    }

    private static enum PasswordType
    {
        PLAIN
        {
            @Override
            public PasswordValidator createValdiator ()
            {
                return new PlainValidator ( false );
            }
        },
        PLAIN_IGNORE_CASE
        {
            @Override
            public PasswordValidator createValdiator ()
            {
                return new PlainValidator ( true );
            }
        },
        MD5_HEX
        {
            @Override
            public PasswordValidator createValdiator () throws NoSuchAlgorithmException
            {
                return new DigestValidator ( "MD5", "UTF-8", new HexCodec () );
            }
        },
        SHA1_HEX
        {
            @Override
            public PasswordValidator createValdiator () throws NoSuchAlgorithmException
            {
                return new DigestValidator ( "SHA1", "UTF-8", new HexCodec () );
            }
        };

        public abstract PasswordValidator createValdiator () throws Exception;

    }

    /**
     * Mark this service as authoritative.
     */
    private boolean authoritative;

    private PasswordValidator passwordValidator;

    private String findUserSql;

    private String findRolesForUserSql;

    public JdbcAuthenticationService ( final BundleContext context, final String id )
    {
        this.context = context;
        this.id = id;
    }

    @Override
    public UserInformation authenticate ( final String username, final String password ) throws AuthenticationException
    {
        try
        {
            this.readLock.lock ();
            if ( this.accessor == null )
            {
                logger.info ( "We don't have any accessor" );
                return failure ( "No connection to database", NO_ACCESSOR );
            }

            try
            {
                return this.accessor.doWithConnection ( new CommonConnectionTask<UserInformation> () {
                    @Override
                    public UserInformation performTask ( final ConnectionContext connection ) throws Exception
                    {
                        return performAuthentication ( connection, username, password );
                    }
                } );
            }
            catch ( final Exception e )
            {
                if ( e.getCause () instanceof AuthenticationException )
                {
                    logger.info ( "Task throw exception. Rethrowing cause...", e );
                    throw (AuthenticationException)e.getCause ();
                }
                else
                {
                    logger.warn ( "Failed to perform login", e );
                    throw new AuthenticationException ( INTERNAL_ERROR, e );
                }
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    protected UserInformation performAuthentication ( final ConnectionContext connection, final String username, final String password ) throws AuthenticationException, SQLException
    {
        final PasswordCheckRowCallback callback = new PasswordCheckRowCallback ( password );
        connection.query ( callback, this.findUserSql, new MapBuilder<String, Object> ().put ( "USER_ID", username ).getMap () );

        if ( !callback.isResult () )
        {
            return failure ( "User not found or password invalid", StatusCodes.INVALID_USER_OR_PASSWORD );
        }

        final List<String> roles;

        if ( this.findRolesForUserSql != null && !this.findRolesForUserSql.isEmpty () )
        {
            roles = connection.queryForList ( String.class, this.findRolesForUserSql, new MapBuilder<String, Object> ().put ( "USER_ID", username ).getMap () );
        }
        else
        {
            roles = null;
        }

        logger.trace ( "Found roles for user: {}", roles );

        return new UserInformation ( username, password, roles );
    }

    protected boolean validatePassword ( final String providedPassword, final String storedPassword )
    {
        try
        {
            return this.passwordValidator.validatePassword ( providedPassword, storedPassword );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to validate password", e );
            return false;
        }
    }

    private UserInformation failure ( final String message, final StatusCode statusCode ) throws AuthenticationException
    {
        if ( this.authoritative )
        {
            throw new AuthenticationException ( statusCode, message );
        }

        logger.warn ( "Failed to authenticate non-authoritative with error: {}", statusCode );
        return null;
    }

    public void dispose ()
    {
        detach ();
    }

    public void update ( final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Updating configuration" );

        // detach first

        detach ();

        // setup

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.driver = cfg.getStringChecked ( "driver", "Need database driver name in 'driver'" );
        this.connectionProperties = new Properties ();
        this.connectionProperties.putAll ( cfg.getPrefixed ( "jdbc.properties." ) );
        final PasswordType passwordType = cfg.getEnumChecked ( "passwordType", PasswordType.class, String.format ( "Need 'passwordType' to be one of (%s)", StringHelper.join ( PasswordType.values (), ", " ) ) );
        this.passwordValidator = passwordType.createValdiator ();
        this.authoritative = cfg.getBoolean ( "authoritative", true );
        this.findUserSql = cfg.getStringChecked ( "findUserSql", "Need 'findUserSql' to be set" );
        this.findRolesForUserSql = cfg.getString ( "findRolesForUserSql" );

        // now attach

        attach ();
    }

    private void attach () throws InvalidSyntaxException
    {
        logger.debug ( "Creating data source tracker: {}", this.driver );
        this.tracker = new DataSourceFactoryTracker ( this.context, this.driver, new SingleServiceListener<DataSourceFactory> () {

            @Override
            public void serviceChange ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                setDataSource ( service );
            }
        } );
        this.tracker.open ();
    }

    private void detach ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    protected void setDataSource ( final DataSourceFactory service )
    {
        logger.debug ( "Setting data source: {}", service );
        try
        {
            this.writeLock.lock ();

            if ( this.accessor != null )
            {
                this.accessor.dispose ();
                this.accessor = null;
            }

            try
            {
                if ( service != null )
                {
                    this.accessor = new DataSourceConnectionAccessor ( service, this.connectionProperties );
                }
            }
            catch ( final SQLException e )
            {
                logger.error ( "Failed to create datasource for " + this.id, e );
            }
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }
}
