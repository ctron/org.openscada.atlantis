/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
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
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hds.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openscada.hds.Quantizer;

public class HDSApplication2
{
    public static void main ( final String[] args )
    {
        final Quantizer q = new Quantizer ( 1, TimeUnit.SECONDS );

        test ( q, 500 );
        test ( q, 1000 );
        test ( q, 1500 );
    }

    private static void test ( final Quantizer q, final int i )
    {
        final Date timestamp = new Date ( i );
        final Date start = q.getStart ( timestamp );
        System.out.println ( String.format ( "%tc -> %tc", timestamp, start ) );
    }
}
