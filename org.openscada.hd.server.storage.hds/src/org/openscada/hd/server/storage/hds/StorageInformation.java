package org.openscada.hd.server.storage.hds;

import java.io.File;

import org.openscada.utils.lang.Immutable;

@Immutable
public class StorageInformation
{
    private final String id;

    private final File file;

    private final StorageConfiguration configuration;

    public StorageInformation ( final String id, final File file, final StorageConfiguration configuration )
    {
        this.id = id;
        this.file = file;
        this.configuration = configuration;
    }

    public File getFile ()
    {
        return this.file;
    }

    public String getId ()
    {
        return this.id;
    }

    public StorageConfiguration getConfiguration ()
    {
        return this.configuration;
    }
}
