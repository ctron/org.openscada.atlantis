/**
 * 
 */
package org.openscada.net.da.handler;

import org.openscada.da.core.browser.DataItemEntry;

public class DataItemEntryCommon implements DataItemEntry
{
    private String _name = "";
    private String _id = "";
    
    public DataItemEntryCommon ( String name, String id )
    {
        _name = name;
        _id = id;
    }
    
    public String getId ()
    {
        return _id;
    }

    public String getName ()
    {
       return _name;
    }
}