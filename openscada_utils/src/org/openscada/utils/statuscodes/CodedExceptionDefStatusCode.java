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

public abstract class CodedExceptionDefStatusCode extends Exception implements CodedExceptionBase

{

    private static final long serialVersionUID = 2893453354487364387L;

    private StatusCode statusCode;

    public CodedExceptionDefStatusCode ()
    {
        super ();
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final String message )
    {
        super ( message );
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final Throwable cause )
    {
        super ( cause );
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final String message, final Throwable cause )
    {
        super ( message, cause );
        setStatusCode ( generateStatusCode () );
    }

    /**
     * the implementation of setStatusCode must provide the statuscode you want your exception to have.
     * either get it from the local statusCode file (StatusCodes) or create a new statusCode (new StatusCode()).
     */
    protected abstract StatusCode generateStatusCode ();

    private void setStatusCode ( final StatusCode status )
    {
        this.statusCode = status;
    }

    public StatusCode getStatus ()
    {
        return this.statusCode;
    }

    @Override
    public String getMessage ()
    {
        final String message = this.statusCode + ": " + super.getMessage ();
        return message;
    }

    public String getOriginalMessage ()
    {
        return super.getMessage ();
    }

}
