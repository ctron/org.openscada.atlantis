package org.openscada.da.core.server.browser;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public class StatusCodes
{
    private final static String module = "OSDA";

    private final static String subModule = "BRWS";

    public final static StatusCode NO_SUCH_FOLDER = new StatusCode ( module, subModule, 0x00000001, SeverityLevel.ERROR );
}
