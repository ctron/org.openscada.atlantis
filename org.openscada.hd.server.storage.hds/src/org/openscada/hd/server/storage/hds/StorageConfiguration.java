package org.openscada.hd.server.storage.hds;

import org.openscada.utils.lang.Immutable;

@Immutable
public class StorageConfiguration
{
    private final long timeSlice;

    private final int count;

    public StorageConfiguration ( final long timeSlice, final int count )
    {
        super ();
        this.timeSlice = timeSlice;
        this.count = count;
    }

    public int getCount ()
    {
        return this.count;
    }

    public long getTimeSlice ()
    {
        return this.timeSlice;
    }
}
