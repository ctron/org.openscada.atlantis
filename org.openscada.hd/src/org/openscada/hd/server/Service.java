/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.hd.server;

import org.openscada.core.InvalidSessionException;
import org.openscada.hd.InvalidItemException;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;

/**
 * This interface specifies the operations provided by the server
 * @author Jens Reimann
 * @since 0.14.0
 *
 */
public interface Service extends org.openscada.core.server.Service
{
    public Query createQuery ( Session session, String itemId, QueryParameters parameters, QueryListener listener, boolean updateData ) throws InvalidSessionException, InvalidItemException;
}
