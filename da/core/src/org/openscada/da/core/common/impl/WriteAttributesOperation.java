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

package org.openscada.da.core.common.impl;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.jobqueue.RunnableCancelOperation;

public class WriteAttributesOperation extends RunnableCancelOperation
{

    private DataItem _item = null;
    private WriteAttributesOperationListener _listener = null;
    private Map<String, Variant> _attributes = null;
    
    public WriteAttributesOperation ( DataItem item, WriteAttributesOperationListener listener, Map<String, Variant> attributes )
    {
        _item = item;
        _listener = listener;
        _attributes = attributes;
    }
    
    public void run ()
    {
        Results result = _item.setAttributes ( _attributes );
        
        if ( !isCanceled () )
        {
            _listener.complete ( result );
        }
    }

}
