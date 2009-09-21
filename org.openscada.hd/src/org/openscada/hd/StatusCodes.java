package org.openscada.hd;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public interface StatusCodes
{
    public static final StatusCode INVALID_ITEM = new StatusCode ( "OS", "HD", 0x000000001, SeverityLevel.ERROR );
}
