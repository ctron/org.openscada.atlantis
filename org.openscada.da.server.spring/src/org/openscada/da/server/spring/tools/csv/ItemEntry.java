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
        return this._id;
    }

    public void setId ( final String id )
    {
        this._id = id;
    }

    public boolean isReadable ()
    {
        return this._readable;
    }

    public void setReadable ( final boolean readable )
    {
        this._readable = readable;
    }

    public boolean isWritable ()
    {
        return this._writable;
    }

    public void setWritable ( final boolean writeable )
    {
        this._writable = writeable;
    }

    public String getDescription ()
    {
        return this._description;
    }

    public void setDescription ( final String description )
    {
        this._description = description;
    }

    public Variant getInitialValue ()
    {
        return this._initialValue;
    }

    public void setInitialValue ( final Variant initialValue )
    {
        this._initialValue = initialValue;
    }
}
