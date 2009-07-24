package org.openscada.da.server.simulation.scriptomatic.configuration;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

class StatusCodes
{
    public final static StatusCode GENERIC_ERROR = new StatusCode ( "OSSIM", "CFG", 1, SeverityLevel.ERROR );
}
