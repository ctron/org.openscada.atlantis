/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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
