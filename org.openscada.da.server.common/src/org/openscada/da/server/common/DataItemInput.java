/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common;

import java.util.EnumSet;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public abstract class DataItemInput extends DataItemBase
{

    public DataItemInput ( final String name )
    {
        super ( new DataItemInformationBase ( name, EnumSet.of ( IODirection.INPUT ) ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new InvalidOperationException () );
    }

}
