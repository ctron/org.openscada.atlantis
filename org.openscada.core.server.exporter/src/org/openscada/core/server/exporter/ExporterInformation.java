package org.openscada.core.server.exporter;

import org.openscada.core.ConnectionInformation;
import org.openscada.utils.lang.Immutable;

@Immutable
public class ExporterInformation
{
    private final ConnectionInformation connectionInformation;

    private final String description;

    public ExporterInformation ( final ConnectionInformation connectionInformation, final String description )
    {
        this.connectionInformation = connectionInformation;
        this.description = description;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }
}
