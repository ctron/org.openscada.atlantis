package org.openscada.da.server.exec2.configuration;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public class StatusCodes
{
    public static final StatusCode CONFIGURATION_ERROR = new StatusCode ( "OSES", "CFG", 1, SeverityLevel.FATAL );
}
