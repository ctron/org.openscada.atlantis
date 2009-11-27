package org.openscada.core.server.exporter;

import java.net.URI;

import org.openscada.utils.lang.Immutable;

@Immutable
public class ExporterInformation
{
    private final URI uri;

    private final String description;

    public ExporterInformation ( final URI uri, final String description )
    {
        this.uri = uri;
        this.description = description;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public URI getUri ()
    {
        return this.uri;
    }
}
