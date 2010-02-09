package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import org.junit.Assert;
import org.junit.Test;

public class LevelHelperTest
{

    protected void test ( final boolean expected, final double value, final double limit, final boolean lowerOk, final boolean includedOk )
    {
        final boolean f = LevelHelper.isFailure ( value, limit, lowerOk, includedOk );
        Assert.assertEquals ( expected, f );
    }

    @Test
    public void test1 ()
    {
        test ( false, 0, 10, true, true );
        test ( true, 20, 10, true, true );
    }

    @Test
    public void test2 ()
    {
        test ( true, 0, 10, false, true );
        test ( false, 20, 10, false, true );
    }

    @Test
    public void test3 ()
    {
        test ( false, 10, 10, false, true );
        test ( true, 10, 10, false, false );
    }
}
