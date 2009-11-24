package org.openscada.da.server.exec2;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public class StatusCodes
{
    public final static StatusCode TRIGGER_RUNNING = new StatusCode ( "OSEX", "TRIGGER", 0x00001, SeverityLevel.ERROR );
}
