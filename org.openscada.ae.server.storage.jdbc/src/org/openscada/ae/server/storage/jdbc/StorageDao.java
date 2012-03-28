/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.storage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.utils.filter.Filter;

public interface StorageDao
{

    public void storeEvent ( Event event ) throws Exception;

    public void updateComment ( UUID id, String comment ) throws Exception;

    public Event loadEvent ( UUID id ) throws SQLException;

    public ResultSet queryEvents ( Filter filter ) throws SQLException, NotSupportedException;

    public boolean toEventList ( ResultSet rs, Collection<Event> events, boolean isBeforeFirst, long count ) throws SQLException;

    public void dispose ();

}