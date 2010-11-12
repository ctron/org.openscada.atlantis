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

package org.openscada.da.server.common.chain.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;

public class PropertyFileChainStorageService implements ChainStorageService
{
    private static Logger log = Logger.getLogger ( PropertyFileChainStorageService.class );

    private final File storageRoot;

    public PropertyFileChainStorageService ( final File storageRoot )
    {
        this.storageRoot = storageRoot;
        if ( !this.storageRoot.isDirectory () )
        {
            throw new RuntimeException ( String.format ( "The provided storage root %s is not a directory", storageRoot ) );
        }
    }

    protected File getItemFile ( final String itemId )
    {
        String itemFileName = itemId;

        try
        {
            itemFileName = URLEncoder.encode ( itemId, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            log.warn ( "Failed to convert to filename", e );
        }

        return new File ( this.storageRoot, itemFileName );
    }

    protected Properties loadProperties ( final File itemFile ) throws IOException
    {
        final Properties p = new Properties ();

        final FileInputStream stream = new FileInputStream ( itemFile );
        try
        {
            p.load ( stream );
        }
        finally
        {
            stream.close ();
        }

        return p;
    }

    public Map<String, Variant> loadValues ( final String itemId, final Set<String> valueNames )
    {
        final File itemFile = getItemFile ( itemId );
        if ( !itemFile.exists () )
        {
            return new HashMap<String, Variant> ();
        }

        Properties p = new Properties ();
        try
        {
            p = loadProperties ( itemFile );

            final Map<String, Variant> result = new HashMap<String, Variant> ();
            final VariantEditor ed = new VariantEditor ();

            // convert needed items
            for ( final String value : valueNames )
            {
                final String data = p.getProperty ( value, null );
                if ( data != null )
                {
                    try
                    {
                        // convert using property editor
                        ed.setAsText ( data );
                        result.put ( value, (Variant)ed.getValue () );
                    }
                    catch ( final Throwable e )
                    {
                        log.warn ( String.format ( "Failed to convert '%s' for item '%s'", value, itemId ), e );
                    }
                }
            }

            return result;
        }
        catch ( final Throwable e )
        {
            log.warn ( String.format ( "Failed to load properties from file '%s'", itemFile ), e );
            return new HashMap<String, Variant> ();
        }
    }

    public void storeValues ( final String itemId, final Map<String, Variant> values )
    {
        final File file = getItemFile ( itemId );

        try
        {
            Properties p = new Properties ();
            try
            {
                p = loadProperties ( file );
            }
            catch ( final Throwable e )
            {
                log.debug ( "Unable to find initial properties", e );
            }

            final VariantEditor ed = new VariantEditor ();

            // convert values
            for ( final Map.Entry<String, Variant> entry : values.entrySet () )
            {
                ed.setValue ( entry.getValue () );
                p.setProperty ( entry.getKey (), ed.getAsText () );
            }
            final FileOutputStream stream = new FileOutputStream ( file );
            try
            {
                p.store ( stream, "" );
            }
            finally
            {
                stream.close ();
            }
        }
        catch ( final IOException e )
        {
            log.error ( "Failed to store values", e );
        }
    }

    public void dispose ()
    {
    }

    public void init ()
    {
    }

}
