package org.openscada.da.core.common.impl;

import java.util.Arrays;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.HiveBrowser;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.utils.str.StringHelper;

public abstract class HiveBrowserCommon implements HiveBrowser
{
    private static Logger _log = Logger.getLogger ( HiveBrowserCommon.class );
    
    private HiveCommon _hive = null;
    
    public HiveBrowserCommon ( HiveCommon hive )
    {
        _hive = hive;
    }
   
    public Entry [] list ( Session session, String path [] ) throws InvalidSessionException, InvalidItemException, NoSuchFolderException
    {
        _log.debug ( "List request for: " + StringHelper.join ( path, "/" ) );
        
        _hive.validateSession ( session );
        
        if ( getRootFolder() == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }
        
        Stack<String> pathStack = new Stack<String> ();
        pathStack.addAll ( Arrays.asList ( path ) );
        return getRootFolder ().list ( pathStack );
    }
    
    public void subscribe ( Session session, String[] path ) throws InvalidSessionException, InvalidItemException
    {
        _hive.validateSession ( session );
        
        SessionCommon sessionCommon = (SessionCommon)session;
        sessionCommon.getData ().addPath ( path );
    }
    
    public void unsubscribe ( Session session, String[] path ) throws InvalidSessionException, InvalidItemException
    {
        _hive.validateSession ( session );
        
        SessionCommon sessionCommon = (SessionCommon)session;
        sessionCommon.getData ().removePath ( path );
    }
    
    public abstract Folder getRootFolder ();
}
