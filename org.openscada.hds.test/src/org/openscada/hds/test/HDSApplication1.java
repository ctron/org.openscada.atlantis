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

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.openscada.hds.DataFileAccessor;
import org.openscada.hds.DataFileAccessorImpl;
import org.openscada.hds.ValueVisitor;

public class HDSApplication1
{
    public static void main ( final String[] args ) throws Exception
    {
        final Calendar start = Calendar.getInstance ();
        start.set ( Calendar.MILLISECOND, 0 );
        start.set ( Calendar.SECOND, 0 );
        start.set ( Calendar.MINUTE, 0 );
        start.set ( Calendar.HOUR_OF_DAY, 0 );

        final Calendar end = (Calendar)start.clone ();
        end.add ( Calendar.DAY_OF_MONTH, 1 );

        final File path = new File ( "testdata" );
        path.mkdir ();

        final File file = new File ( path, "file1.hds" );

        file.delete ();

        final DataFileAccessor writer = DataFileAccessorImpl.create ( file, start.getTime (), end.getTime () );

        for ( int i = 0; i < 20; i++ )
        {
            writer.insertValue ( i, new Date (), false, false, false );
        }

        writer.visit ( new ValueVisitor () {

            @Override
            public boolean value ( final double value, final Date date, final boolean error, final boolean manual )
            {
                System.out.println ( String.format ( "Value: %s, Timestamp: %tc, Error: %s, Manual: %s", value, date, error, manual ) );
                return true;
            }
        } );

        writer.dispose ();
        file.delete ();
    }
}
