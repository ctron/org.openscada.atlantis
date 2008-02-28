package org.openscada.da.server.opc;

import java.net.UnknownHostException;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;

public abstract class OPCBaseFolder implements org.openscada.da.server.browser.common.Folder
{
    private static Logger _log = Logger.getLogger ( OPCBaseFolder.class );
    
    private Thread _readThread = null;

    protected abstract void fill () throws IllegalArgumentException, UnknownHostException, JIException;

    protected FolderCommon _folder;
    private boolean _needUpdate = true;
    protected OPCItemManager _itemManager;

    public OPCBaseFolder ( OPCItemManager itemManager )
    {
        super ();
        _itemManager = itemManager;
        _folder = new FolderCommon ();
    }

    public void added ()
    {
        _folder.added ();
        checkFill ();
    }

    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        return new Entry[0];
    }

    public void removed ()
    {
        _folder.removed ();
    }

    public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        _folder.subscribe ( path, listener, tag );
    }

    public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        _folder.unsubscribe ( path, tag );
    }

    protected synchronized void checkFill ()
    {
        if ( _needUpdate )
        {
            _needUpdate = false;
            triggerFill ();
        }
    }

    protected synchronized void triggerFill ()
    {
        if ( _readThread == null )
        {
            _readThread = new Thread ( new Runnable () {

                public void run ()
                {
                    try
                    {
                        fill ();
                    }
                    catch ( Throwable e )
                    {
                        _log.error ( "Failed to browser", e );
                    }
                    finally
                    {
                        _readThread = null;
                    }
                }
            }, "OPCFolderFiller" );
            _readThread.setDaemon ( true );
            _readThread.start ();
        }
    }

}