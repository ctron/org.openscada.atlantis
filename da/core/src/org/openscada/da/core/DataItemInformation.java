package org.openscada.da.core;

import java.util.EnumSet;

public interface DataItemInformation
{
    public EnumSet<IODirection> getIODirection ();
    public String getName ();
}
