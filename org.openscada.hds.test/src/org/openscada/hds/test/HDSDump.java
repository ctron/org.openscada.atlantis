/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.Date;

import org.openscada.hds.DataFileAccessorImpl;
import org.openscada.hds.ValueVisitor;

public class HDSDump
{
    public static void main ( final String[] args ) throws Exception
    {
        final String fileName = args[0];
        final File file = new File ( fileName );

        final DataFileAccessorImpl accessor = new DataFileAccessorImpl ( file );
        accessor.visit ( new ValueVisitor () {

            @Override
            public boolean value ( final double value, final Date date, final boolean error, final boolean manual )
            {
                System.out.println ( String.format ( "Timestamp: %1$tF %1$tT.%1$tL, Error: %2$s, Manual: %3$s, Value: %4$s", date, error, manual, value ) );
                return true;
            }
        } );

        accessor.dispose ();
    }
}
