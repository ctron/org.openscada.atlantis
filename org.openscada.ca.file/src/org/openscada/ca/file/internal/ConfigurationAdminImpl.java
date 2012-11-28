/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ca.file.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.common.ConfigurationImpl;
import org.openscada.sec.UserInformation;
import org.openscada.utils.str.StringReplacer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class ConfigurationAdminImpl extends AbstractConfigurationAdministrator
{
    private static final String URI_CHARSET = "UTF-8";

    private final static class DataFilenameFilter implements FilenameFilter
    {
        @Override
        public boolean accept ( final File dir, final String name )
        {
            if ( ".meta".equals ( name ) )
            {
                return false;
            }
            return true;
        }
    }

    private static final String META_FILE = ".meta";

    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdminImpl.class );

    private static final String STORE = "openscadaConfigStore";

    private final BundleContext context;

    private final File root;

    private final Interner<String> stringInterner;

    public ConfigurationAdminImpl ( final BundleContext context, final Interner<String> stringInterner ) throws InvalidSyntaxException
    {
        super ( context );
        this.stringInterner = stringInterner;
        this.context = context;
        this.root = initRoot ();
    }

    protected String intern ( final String string )
    {
        return this.stringInterner.intern ( string );
    }

    protected File getRootFile ()
    {
        final String rootDir = getRootFileName ();

        if ( rootDir == null || rootDir.length () == 0 )
        {
            return this.context.getDataFile ( STORE );
        }
        else
        {
            return new File ( rootDir );
        }
    }

    private String getRootFileName ()
    {
        return StringReplacer.replace ( System.getProperty ( "org.openscada.ca.file.root", null ), System.getProperties () );
    }

    private File initRoot ()
    {
        final File file = getRootFile ();
        if ( file != null )
        {
            if ( !file.exists () )
            {
                logger.info ( "Storage root does not exist: " + file.getName () );
                file.mkdir ();
            }
            if ( file.isDirectory () )
            {
                return file;
            }
            else
            {
                logger.warn ( "File exists but is not a directory: " + file.getName () );
            }
        }
        else
        {
            logger.warn ( "No file system support" );
        }
        return null;
    }

    @Override
    public synchronized void start () throws Exception
    {
        super.start ();
        performInitialLoad ();
    }

    protected void performInitialLoad ()
    {
        logger.info ( "Loading initial set from: {}", this.root );

        if ( this.root == null )
        {
            logger.warn ( "No root found" );
            return;
        }

        for ( final String pathName : this.root.list () )
        {
            try
            {
                final File path = new File ( this.root, pathName );
                if ( path.isDirectory () )
                {
                    logger.debug ( "Checking for path: " + path.getName () );
                    final String factoryId = detectFactory ( path );
                    if ( factoryId != null )
                    {
                        logger.debug ( String.format ( "Path %s is a possible factory (%s). Adding...", path.getName (), factoryId ) );
                        performLoadFactory ( factoryId );
                    }
                }
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to load factory: " + pathName, e );
            }
        }
    }

    private String detectFactory ( final File path )
    {
        final File meta = new File ( path, META_FILE );
        final Properties p = new Properties ();
        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream ( meta );
            p.load ( stream );
        }
        catch ( final Exception e )
        {
            return null;
        }
        finally
        {
            if ( stream != null )
            {
                try
                {
                    stream.close ();
                }
                catch ( final IOException e )
                {
                    logger.warn ( "Failed to close stream", e );
                }
            }
        }
        return p.getProperty ( "id" );
    }

    protected void performLoadFactory ( final String factoryId ) throws Exception
    {
        if ( this.root == null )
        {
            logger.warn ( "No root found" );
            return;
        }

        final File path = getFactoryPath ( factoryId );
        loadAll ( path, factoryId );
    }

    private void createStore ( final File factoryRoot, final String factoryId )
    {
        if ( !factoryRoot.mkdir () )
        {
            logger.warn ( "Failed to create store: " + factoryRoot );
            return;
        }
        final File meta = new File ( factoryRoot, META_FILE );
        final Properties p = new Properties ();
        p.put ( "id", factoryId );
        FileOutputStream stream = null;
        try
        {
            stream = new FileOutputStream ( meta );

            logger.debug ( "Creating new store: {}", factoryRoot );
            p.store ( stream, "" );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to initialize store: " + factoryRoot );
        }
        finally
        {
            if ( stream != null )
            {
                try
                {
                    stream.close ();
                }
                catch ( final IOException e )
                {
                    logger.warn ( "Failed to close stream", e );
                }
            }
        }
    }

    private void loadAll ( final File configurationRoot, final String factoryId ) throws UnsupportedEncodingException
    {
        logger.info ( "Loading from: {}", configurationRoot.getName () );

        final List<ConfigurationImpl> configurations = new LinkedList<ConfigurationImpl> ();

        for ( final File file : configurationRoot.listFiles ( new DataFilenameFilter () ) )
        {
            logger.info ( "Loading file: {}", file.getName () );
            final String id = idFromFile ( file );
            final ConfigurationImpl cfg = loadConfiguration ( factoryId, id, file );

            if ( cfg != null )
            {
                configurations.add ( cfg );
            }
        }

        addStoredFactory ( factoryId, configurations.toArray ( new ConfigurationImpl[configurations.size ()] ) );
    }

    private ConfigurationImpl loadConfiguration ( final String factoryId, final String configurationId, final File file )
    {
        try
        {
            final Properties p = new Properties ();

            final FileInputStream stream = new FileInputStream ( file );
            try
            {
                p.load ( stream );
            }
            finally
            {
                stream.close ();
            }

            final Map<String, String> result = new HashMap<String, String> ();
            for ( final Entry<Object, Object> entry : p.entrySet () )
            {
                result.put ( intern ( entry.getKey ().toString () ), intern ( entry.getValue ().toString () ) );
            }

            return new ConfigurationImpl ( configurationId, factoryId, result );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to load" );
            return null;
        }
    }

    private String getPath ( final String factoryId ) throws UnsupportedEncodingException
    {
        return encode ( factoryId );
    }

    private String encode ( final String path ) throws UnsupportedEncodingException
    {
        return URLEncoder.encode ( path, URI_CHARSET );
    }

    private String idFromFile ( final File file ) throws UnsupportedEncodingException
    {
        final String name = file.getName ();
        return URLDecoder.decode ( name, URI_CHARSET );
    }

    @Override
    protected void performPurge ( final UserInformation userInformation, final String factoryId, final PurgeFuture future ) throws Exception
    {
        logger.info ( "Request to delete: {}", factoryId );

        if ( this.root == null )
        {
            logger.warn ( "Unable to store : no root" );
            return;
        }
        final File path = getFactoryPath ( factoryId );

        for ( final File file : path.listFiles ( new DataFilenameFilter () ) )
        {
            logger.info ( "Delete file: " + file.getName () );
            final String id = idFromFile ( file );

            final ConfigurationFuture subFuture = new ConfigurationFuture ();
            changeConfiguration ( userInformation, factoryId, id, null, subFuture );

            future.addChild ( subFuture );

            file.delete ();
        }

        final File metaFile = new File ( path, ".meta" );
        metaFile.delete ();

        logger.debug ( "Delete factory root: {}", path );
        path.delete ();

        future.setComplete ();
    }

    @Override
    protected void performStoreConfiguration ( final UserInformation userInformation, final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet, final ConfigurationFuture future ) throws FileNotFoundException, IOException
    {
        if ( this.root == null )
        {
            future.setError ( new RuntimeException ( "No root to store" ).fillInStackTrace () );
            logger.warn ( "Unable to store : no root" );
            return;
        }

        final File path = getFactoryPath ( factoryId );
        final File file = new File ( path, encode ( configurationId ) );

        logger.info ( String.format ( "Storing %s to %s", configurationId, file ) );

        final Map<String, String> newProperties = new HashMap<String, String> ();

        // if this is differential, load in old data first
        if ( !fullSet )
        {
            final ConfigurationImpl oldConfig = loadConfiguration ( factoryId, configurationId, file );
            if ( oldConfig != null )
            {
                newProperties.putAll ( oldConfig.getData () );
            }
        }

        // merge in changes
        for ( final Map.Entry<String, String> entry : properties.entrySet () )
        {
            final String key = entry.getKey ();
            final String value = entry.getValue ();
            if ( value != null )
            {
                newProperties.put ( intern ( key ), intern ( value ) );
            }
            else
            {
                newProperties.remove ( key );
            }
        }

        // convert to properties and store
        final Properties p = new Properties ();
        p.putAll ( newProperties );

        final FileOutputStream stream = new FileOutputStream ( file );
        try
        {
            logger.debug ( "Storing {}/{} -> {}", new Object[] { factoryId, configurationId, newProperties } );
            p.store ( stream, "" );
        }
        finally
        {
            stream.close ();
        }

        // notify the abstract service from our content change
        changeConfiguration ( userInformation, factoryId, configurationId, newProperties, future );
    }

    private File getFactoryPath ( final String factoryId ) throws UnsupportedEncodingException
    {
        final File path = new File ( this.root, getPath ( factoryId ) );
        if ( !path.exists () )
        {
            logger.info ( String.format ( "Store for factory (%s) does not exist", factoryId ) );
            createStore ( path, factoryId );
        }
        return path;
    }

    @Override
    protected void performDeleteConfiguration ( final UserInformation userInformation, final String factoryId, final String configurationId, final ConfigurationFuture future ) throws Exception
    {
        final File path = getFactoryPath ( factoryId );

        final File file = new File ( path, encode ( configurationId ) );

        logger.info ( "Deleting {}", configurationId );

        if ( !file.delete () )
        {
            logger.info ( "Failed to delete: {}", file );
        }

        // notify the abstract service from our content change
        changeConfiguration ( userInformation, factoryId, configurationId, null, future );
    }
}
