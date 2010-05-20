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

package org.openscada.da.client.ice;

import org.openscada.da.client.WriteAttributeOperationCallback;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_writeAttributes;
import OpenSCADA.DA.WriteAttributesResultEntry;

public class AsyncWriteAttributesOperation extends AMI_Hive_writeAttributes
{
    private final WriteAttributeOperationCallback _callback;

    public AsyncWriteAttributesOperation ( final WriteAttributeOperationCallback callback )
    {
        super ();
        this._callback = callback;
    }

    @Override
    public void ice_exception ( final LocalException ex )
    {
        this._callback.error ( ex );
    }

    @Override
    public void ice_exception ( final UserException ex )
    {
        this._callback.failed ( ex.getMessage () );
    }

    @Override
    public void ice_response ( final WriteAttributesResultEntry[] result )
    {
        this._callback.complete ( ConnectionHelper.fromIce ( result ) );
    }
}
