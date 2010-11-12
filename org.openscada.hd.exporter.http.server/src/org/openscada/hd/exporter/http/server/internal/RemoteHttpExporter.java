/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.exporter.http.server.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;

public class RemoteHttpExporter implements HttpExporter
{
    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
        return null;
    }

    public List<String> getItems ()
    {
        return new ArrayList<String> ();
    }

    public List<String> getSeries ( final String itemId )
    {
        return new ArrayList<String> ();
    }
}
