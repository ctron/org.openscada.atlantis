package org.openscada.da.core.browser;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public interface DataItemEntry extends Entry
{
    String getId ();
    EnumSet<IODirection> getIODirections ();
}
