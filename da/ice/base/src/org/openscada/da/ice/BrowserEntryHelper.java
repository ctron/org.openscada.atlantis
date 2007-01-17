package org.openscada.da.ice;

import java.util.LinkedList;
import java.util.List;

import org.openscada.core.ice.AttributesHelper;

import OpenSCADA.DA.IODirection;

public class BrowserEntryHelper
{
    public static OpenSCADA.DA.Browser.Entry[] toIce ( org.openscada.da.core.browser.Entry [] entries )
    {
        OpenSCADA.DA.Browser.Entry[] iceEntries = new OpenSCADA.DA.Browser.Entry[entries.length];
        
        for ( int i = 0; i < entries.length; i++ )
        {
            if ( entries[i] instanceof org.openscada.da.core.browser.FolderEntry )
            {
                iceEntries[i] = new OpenSCADA.DA.Browser.FolderEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ) );
            }
            else if ( entries[i] instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                org.openscada.da.core.browser.DataItemEntry d = (org.openscada.da.core.browser.DataItemEntry)entries[i];
                List<IODirection> ioDir = new LinkedList<IODirection> ();
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.INPUT ) )
                    ioDir.add ( IODirection.INPUT );
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.OUTPUT ) )
                    ioDir.add ( IODirection.OUTPUT );
                iceEntries[i] = new OpenSCADA.DA.Browser.ItemEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ), d.getId (), ioDir.toArray ( new IODirection[0] ) );
            }
        }
        return iceEntries;
    }
    
    public static org.openscada.da.core.browser.Entry [] fromIce ( OpenSCADA.DA.Browser.Entry [] entries )
    {
        org.openscada.da.core.browser.Entry [] osEntries = new org.openscada.da.core.browser.Entry [ entries.length ];
        
        for ( int i = 0; i < entries.length; i++ )
        {
            if ( entries[i] instanceof OpenSCADA.DA.Browser.FolderEntry )
            {
                osEntries[i] = new org.openscada.da.ice.FolderEntry ( (OpenSCADA.DA.Browser.FolderEntry)entries[i] );
            }
            else if ( entries[i] instanceof OpenSCADA.DA.Browser.ItemEntry )
            {
                osEntries[i] = new org.openscada.da.ice.ItemEntry ( (OpenSCADA.DA.Browser.ItemEntry)entries[i] );
            }
        }
        
        return osEntries;
    }
}
