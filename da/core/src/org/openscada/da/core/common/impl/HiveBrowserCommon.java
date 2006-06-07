package org.openscada.da.core.common.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.HiveBrowser;
import org.openscada.da.core.browser.Location;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.browser.common.FolderListener;
import org.openscada.utils.str.StringHelper;

public abstract class HiveBrowserCommon implements HiveBrowser, FolderListener, SessionListener
{
    private static Logger _log = Logger.getLogger ( HiveBrowserCommon.class );
    
    private HiveCommon _hive = null;
    
    private Map<Object, SessionCommon> _subscriberMap = new HashMap<Object, SessionCommon> ();
    
    public HiveBrowserCommon ( HiveCommon hive )
    {
        _hive = hive;
        _hive.addSessionListener ( this );
    }
   
    public Entry [] list ( Session session, Location location ) throws InvalidSessionException, NoSuchFolderException
    {
        _log.debug ( "List request for: " + location.toString () );
        
        _hive.validateSession ( session );
        
        if ( getRootFolder() == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }
        
        Stack<String> pathStack = new Stack<String> ();
        pathStack.addAll ( location.asList () );
        return getRootFolder ().list ( pathStack );
    }
    
    public void subscribe ( Session session, Location path ) throws NoSuchFolderException, InvalidSessionException
    {
        _hive.validateSession ( session );
        
        if ( getRootFolder() == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }
        
        Stack<String> pathStack = new Stack<String> ();
        pathStack.addAll ( path.asList () );
        
        synchronized ( _subscriberMap )
        {
            SessionCommon sessionCommon = (SessionCommon)session;
            Object tag = new Object ();
            sessionCommon.getData ().addPath ( tag, new Location ( path ) );
            _subscriberMap.put ( tag, sessionCommon );

            boolean success = false;
            try
            {
                getRootFolder ().subscribe ( pathStack, this, tag );
                success = true;
            }
            finally
            {
                if ( !success )
                {
                    sessionCommon.getData ().removePath ( new Location ( path ) );
                    _subscriberMap.remove ( tag );
                }
            }
        }
        
    }
    
    public void unsubscribe ( Session session, Location location ) throws NoSuchFolderException, InvalidSessionException
    {
        _hive.validateSession ( session );
        
        if ( getRootFolder() == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }
        
        unsubscribePath ( (SessionCommon)session, location );
    }
    
    private void unsubscribePath ( SessionCommon session, Location location ) throws NoSuchFolderException
    {
        Object tag = session.getData ().getTag ( new Location ( location ) );
        if ( tag != null )
        {
            session.getData ().removePath ( location );
            synchronized ( _subscriberMap )
            {
                _subscriberMap.remove ( tag );
            }
            
            Stack<String> pathStack = new Stack<String> ();
            pathStack.addAll ( location.asList () );
            
            getRootFolder ().unsubscribe ( pathStack, tag );
        }
    }
    
    public void changed ( Object tag, Collection<Entry> added, Collection<String> removed, boolean full )
    {
        synchronized ( _subscriberMap )
        {
            if ( _subscriberMap.containsKey ( tag ) )
            {
                SessionCommon session = _subscriberMap.get ( tag );
                Location location = session.getData ().getPaths ().get ( tag );
                if ( location != null )
                {
                    try
                    {
                        session.getFolderListener ().folderChanged ( location, added, removed, full );
                    }
                    catch ( Exception e )
                    {}
                }
                
            }
        }
    }
    
    public void create ( SessionCommon session )
    {
    }
    
    public void destroy ( SessionCommon session )
    {
        for ( Map.Entry<Object, Location> entry : session.getData ().getPaths ().entrySet () )
        {
            try
            {
                unsubscribePath ( session, entry.getValue() );
            }
            catch ( NoSuchFolderException e )
            {
            }
        }
        session.getData ().getItems ().clear ();
    }
    
    public abstract Folder getRootFolder ();
}
