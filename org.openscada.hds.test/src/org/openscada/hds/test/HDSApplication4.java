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

import org.openscada.hd.server.storage.common.RunningAverage;

public class HDSApplication4
{
    public static void main ( final String[] args )
    {
        RunningAverage avg = new RunningAverage ();

        avg.next ( 0.0, 0 );
        avg.step ( 100 );
        avg.next ( 1.0001, 200 );
        avg.step ( 200 );
        avg.next ( 2.0001, 300 );
        avg.step ( 300 );
        avg.step ( 400 );
        avg.next ( 4.0001, 500 );
        System.out.println ( avg.getAverage ( 600 ) );
        System.out.println ( avg.getDeviation ( 600 ) );

        avg = new RunningAverage ();
        avg.step ( 100 );
        avg.next ( 1.0, 200 );
        System.out.println ( avg.getAverage ( 300 ) );
        System.out.println ( avg.getDeviation ( 300 ) );

        avg = new RunningAverage ();
        avg.step ( 100 );
        avg.next ( 1.0, 200 );
        avg.next ( 2.0, 300 );
        avg.next ( 4.0, 500 );
        System.out.println ( avg.getAverage ( 600 ) );
        System.out.println ( avg.getDeviation ( 600 ) );

        avg = new RunningAverage ();
        avg.step ( 100 );
        avg.next ( 1.0, 100 );
        avg.next ( 1.0, 200 );
        avg.next ( 1.0, 300 );
        System.out.println ( avg.getAverage ( 400 ) );
        System.out.println ( avg.getDeviation ( 400 ) );
    }
}
