/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.spring.tools.csv;

import org.openscada.core.Variant;

public class ItemEntry
{
    private String _id;
    private boolean _readable = false;
    private boolean _writable = false;
    private String _description = "";
    private Variant _initialValue = new Variant ();

    public String getId ()
    {
        return _id;
    }

    public void setId ( String id )
    {
        _id = id;
    }

    public boolean isReadable ()
    {
        return _readable;
    }

    public void setReadable ( boolean readable )
    {
        _readable = readable;
    }

    public boolean isWritable ()
    {
        return _writable;
    }

    public void setWritable ( boolean writeable )
    {
        _writable = writeable;
    }

    public String getDescription ()
    {
        return _description;
    }

    public void setDescription ( String description )
    {
        _description = description;
    }

    public Variant getInitialValue ()
    {
        return _initialValue;
    }

    public void setInitialValue ( Variant initialValue )
    {
        _initialValue = initialValue;
    }
}
