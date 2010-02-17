/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.ice;

import org.openscada.da.client.WriteAttributeOperationCallback;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_writeAttributes;
import OpenSCADA.DA.WriteAttributesResultEntry;

public class AsyncWriteAttributesOperation extends AMI_Hive_writeAttributes
{
    private WriteAttributeOperationCallback _callback;
    
    public AsyncWriteAttributesOperation ( WriteAttributeOperationCallback callback )
    {
        super ();
        _callback = callback;
    }

    @Override
    public void ice_exception ( LocalException ex )
    {
        _callback.error ( ex );
    }

    @Override
    public void ice_exception ( UserException ex )
    {
        _callback.failed ( ex.getMessage () );
    }

    @Override
    public void ice_response ( WriteAttributesResultEntry[] result )
    {
        _callback.complete ( ConnectionHelper.fromIce ( result ) );
    }
}
