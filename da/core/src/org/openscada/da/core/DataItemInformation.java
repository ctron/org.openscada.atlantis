package org.openscada.da.core;

import java.util.EnumSet;

/**
 * Data item information
 * <p>
 * Data items information objects must be equal on their name!
 * @author jens
 *
 */
public interface DataItemInformation
{
    public EnumSet<IODirection> getIODirection ();
    public String getName ();
}
