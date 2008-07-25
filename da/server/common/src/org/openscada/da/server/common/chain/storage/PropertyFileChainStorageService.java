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

    private File storageRoot;

    public PropertyFileChainStorageService ( File storageRoot )
    {
        this.storageRoot = storageRoot;
        if ( !this.storageRoot.isDirectory () )
        {
            throw new RuntimeException ( String.format ( "The provided storage root %s is not a directory", storageRoot ) );
        }
    }

    protected File getItemFile ( String itemId )
    {
        String itemFileName = itemId;

        try
        {
            itemFileName = URLEncoder.encode ( itemId, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            log.warn ( "Failed to convert to filename", e );
        }

        return new File ( this.storageRoot, itemFileName );
    }

    protected Properties loadProperties ( File itemFile )
    {
        Properties p = new Properties ();

        try
        {
            FileInputStream stream = new FileInputStream ( itemFile );
            try
            {
                p.load ( stream );
            }
            finally
            {
                stream.close ();
            }
        }
        catch ( Throwable e )
        {
            log.warn ( "Failed to load properties from: " + itemFile.getName (), e );
        }

        return p;
    }

    public Map<String, Variant> loadValues ( String itemId, Set<String> valueNames )
    {
        File itemFile = getItemFile ( itemId );
        if ( !itemFile.exists () )
        {
            return new HashMap<String, Variant> ();
        }

        Properties p = new Properties ();
        try
        {
            p = loadProperties ( itemFile );

            Map<String, Variant> result = new HashMap<String, Variant> ();
            VariantEditor ed = new VariantEditor ();

            // convert needed items
            for ( String value : valueNames )
            {
                String data = p.getProperty ( value, null );
                if ( data != null )
                {
                    try
                    {
                        // convert using property editor
                        ed.setAsText ( data );
                        result.put ( value, (Variant)ed.getValue () );
                    }
                    catch ( Throwable e )
                    {
                        log.warn ( String.format ( "Failed to convert '%s' for item '%s'", value, itemId ), e );
                    }
                }
            }

            return result;
        }
        catch ( Throwable e )
        {
            log.warn ( String.format ( "Failed to load properties from file '%s'", itemFile ), e );
            return new HashMap<String, Variant> ();
        }
    }

    public void storeValues ( String itemId, Map<String, Variant> values )
    {
        File file = getItemFile ( itemId );

        try
        {
            Properties p = loadProperties ( file );

            VariantEditor ed = new VariantEditor ();

            // convert values
            for ( Map.Entry<String, Variant> entry : values.entrySet () )
            {
                ed.setValue ( entry.getValue () );
                p.setProperty ( entry.getKey (), ed.getAsText () );
            }
            FileOutputStream stream = new FileOutputStream ( file );
            try
            {
                p.store ( stream, "" );
            }
            finally
            {
                stream.close ();
            }
        }
        catch ( IOException e )
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
