package org.openscada.da.server.opc2.browser;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class BrowseResultEntry
{
    private String entryName;

    private String itemId;

    private EnumSet<IODirection> ioDirections;

    public String getEntryName ()
    {
        return entryName;
    }

    public void setEntryName ( String entryName )
    {
        this.entryName = entryName;
    }

    public String getItemId ()
    {
        return itemId;
    }

    public void setItemId ( String itemId )
    {
        this.itemId = itemId;
    }

    public EnumSet<IODirection> getIoDirections ()
    {
        return ioDirections;
    }

    public void setIoDirections ( EnumSet<IODirection> ioDirections )
    {
        this.ioDirections = ioDirections;
    }
}
