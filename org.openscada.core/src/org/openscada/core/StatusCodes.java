package org.openscada.core;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public interface StatusCodes
{
    public static final StatusCode INVALID_SESSION = new StatusCode ( "OS", "CORE", 0x00001, SeverityLevel.ERROR );

    public static final StatusCode UNABLE_TO_CREATE_SESSION = new StatusCode ( "OS", "CORE", 0x00002, SeverityLevel.ERROR );
}
