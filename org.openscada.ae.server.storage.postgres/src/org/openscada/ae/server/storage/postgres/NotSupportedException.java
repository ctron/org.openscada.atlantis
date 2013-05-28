/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.storage.postgres;

public class NotSupportedException extends Exception
{

    private static final long serialVersionUID = -530194790629785166L;

    public NotSupportedException ()
    {
        super ();
    }

    public NotSupportedException ( final String message, final Throwable cause )
    {
        super ( message, cause );
    }

    public NotSupportedException ( final String message )
    {
        super ( message );
    }

    public NotSupportedException ( final Throwable cause )
    {
        super ( cause );
    }
}
