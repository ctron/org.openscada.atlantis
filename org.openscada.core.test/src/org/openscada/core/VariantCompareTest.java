package org.openscada.core;

import org.junit.Assert;
import org.junit.Test;

public class VariantCompareTest
{
    @Test
    public void testEqual ()
    {
        checkCompare ( 0, 0, 0 );
        checkCompare ( "0", 0, 0 );
        checkCompare ( 0, "0", 0 );
        checkCompare ( "0", "0", 0 );

        checkCompare ( 0L, 0L, 0 );
        checkCompare ( "0", 0L, 0 );
        checkCompare ( 0L, "0", 0 );

        checkCompare ( true, true, 0 );
        checkCompare ( "true", true, 0 );
        checkCompare ( true, "true", 0 );
        checkCompare ( "true", "true", 0 );

        checkCompare ( "true", "true", 0 );
    }

    private void checkCompare ( final Object o1, final Object o2, final int expected )
    {
        Assert.assertEquals ( expected, Variant.valueOf ( o1 ).compareTo ( Variant.valueOf ( o2 ) ) );
    }
}
