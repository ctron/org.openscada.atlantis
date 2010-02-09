package org.openscada.ae.monitor.dataitem.monitor.internal.level;

public class LevelHelper
{
    public static boolean isFailure ( final double value, final double limit, final boolean lowerOk, final boolean includedOk )
    {
        final boolean f = value <= limit && lowerOk || value >= limit && !lowerOk;
        if ( !f )
        {
            return true;
        }

        return value == limit && !includedOk;
    }
}
