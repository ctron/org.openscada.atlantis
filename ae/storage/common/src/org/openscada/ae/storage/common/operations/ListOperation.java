/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.storage.common.operations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.core.ListOperationListener;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Storage;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.core.Variant;
import org.openscada.utils.jobqueue.RunnableCancelOperation;

public class ListOperation extends RunnableCancelOperation
{
    private StorageCommon _storage = null;
    private ListOperationListener _listener = null;
    
    public ListOperation ( StorageCommon storage, ListOperationListener listener )
    {
        _listener = listener;
        _storage = storage;
    }
    
    public void run ()
    {
        try
        {
            QueryDescription[] queries = _storage.list ();
            synchronized ( this )
            {
                if ( !isCanceled () )
                {
                    _listener.complete ( queries );
                }
            }
        }
        catch ( Exception e )
        {
            synchronized ( this )
            {
                if ( !isCanceled () )
                {
                    _listener.failed ( e );
                }
            }
        }
        
        
    }

}
