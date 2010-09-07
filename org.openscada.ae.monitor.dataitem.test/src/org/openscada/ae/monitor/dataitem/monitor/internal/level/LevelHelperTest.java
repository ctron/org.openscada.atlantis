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

        test ( false, 10, 10, true, true );
        test ( true, 10, 10, true, false );
    }
}
