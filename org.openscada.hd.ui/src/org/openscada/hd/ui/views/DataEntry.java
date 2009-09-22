package org.openscada.hd.ui.views;

import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class DataEntry
{
    private final ValueInformation info;

    private final Value[] values;

    public DataEntry ( final ValueInformation info, final Value[] values )
    {
        super ();
        this.info = info;
        this.values = values;
    }

    public ValueInformation getInfo ()
    {
        return this.info;
    }

    public Value[] getValues ()
    {
        return this.values;
    }
}
