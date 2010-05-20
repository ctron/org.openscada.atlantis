/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.common.SuspendableDataItem;

public class SuspendItem extends DataItemInputCommon implements SuspendableDataItem
{
    private static Logger _log = Logger.getLogger ( SuspendItem.class );

    public SuspendItem ( final String name )
    {
        super ( name );
    }

    public void suspend ()
    {
        _log.warn ( String.format ( "Item %1$s suspended", getInformation ().getName () ) );
        System.out.println ( String.format ( "Item %1$s suspended", getInformation ().getName () ) );
    }

    public void wakeup ()
    {
        _log.warn ( String.format ( "Item %1$s woken up", getInformation ().getName () ) );
        System.out.println ( String.format ( "Item %1$s woken up", getInformation ().getName () ) );
    }

}
