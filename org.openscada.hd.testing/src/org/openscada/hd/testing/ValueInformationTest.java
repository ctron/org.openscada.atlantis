package org.openscada.hd.testing;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.hd.ValueInformation;

public class ValueInformationTest
{
    @Test
    public void equals1 ()
    {
        final Calendar start = Calendar.getInstance ();
        start.set ( 2000, 1, 1 );
        final Calendar end = (Calendar)start.clone ();
        end.add ( Calendar.MINUTE, 1 );

        final ValueInformation vi1 = new ValueInformation ( start, end, 1.0, 0.0, 1 );
        final ValueInformation vi2 = new ValueInformation ( start, end, 1.0, 0.0, 1 );

        Assert.assertEquals ( "Basic equality", vi1, vi2 );
    }

    /**
     * Test if the calendar can be changed without changing the VI instance
     */
    @Test
    public void equals2 ()
    {
        final Calendar start = Calendar.getInstance ();
        start.set ( 2000, 1, 1 );
        final Calendar end = (Calendar)start.clone ();
        end.add ( Calendar.MINUTE, 1 );

        final ValueInformation vi1 = new ValueInformation ( start, end, 1.0, 0.0, 1 );

        start.set ( 2001, 1, 1 );

        final ValueInformation vi2 = new ValueInformation ( start, end, 1.0, 0.0, 1 );

        Assert.assertFalse ( "Should not be equal", vi1.equals ( vi2 ) );
    }
}
