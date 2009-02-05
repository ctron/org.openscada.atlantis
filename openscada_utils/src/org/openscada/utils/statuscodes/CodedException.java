/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.statuscodes;

public class CodedException extends Exception implements CodedExceptionBase
{
    private static final long serialVersionUID = 2962144070439177464L;

    protected StatusCode status;

    public CodedException ( final StatusCode statusCode )
    {
        super ();
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final String message )
    {
        super ( message );
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final Throwable cause )
    {
        super ( cause );
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final String message, final Throwable cause )
    {
        super ( message, cause );
        this.status = statusCode;
    }

    public StatusCode getStatus ()
    {
        return this.status;
    }

    /**
     * overrides getMessage to produce a message bearing the assigned statusCode and the 
     * the default Message of this exception type
     */
    @Override
    public String getMessage ()
    {
        final String message = getStatus () + ": " + super.getMessage ();
        return message;
    }

    public String getOriginalMessage ()
    {
        return super.getMessage ();
    }
}
