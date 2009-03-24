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

package org.openscada.ae.storage.test;

import java.util.Arrays;
import java.util.Collection;

import org.openscada.ae.core.QueryDescriptor;
import org.openscada.ae.storage.Session;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;

public class Storage extends StorageCommon
{

    public Collection<QueryDescriptor> listQueries ( final Session session ) throws InvalidSessionException
    {
        validateSession ( session );

        final QueryDescriptor desc = new QueryDescriptor ();
        desc.setId ( "QUERY1" );
        desc.getAttributes ().put ( "description", new Variant ( "Description for QUERY1" ) );

        return Arrays.asList ( desc );
    }
}
