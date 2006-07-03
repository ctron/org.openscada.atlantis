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

package org.openscada.utils.exec.test;

import org.openscada.utils.exec.OperationResultHandler;

class TestOperationHandler<R> implements OperationResultHandler<R> 
{
    R _result = null;
    Exception _exception = null;
    boolean _failure = false;
    boolean _success = false;
    
    public void failure ( Exception e )
    {
        _result = null;
        _exception = e;
        _success = false;
        _failure = true;
    }

    public void success ( R result )
    {
        _result = result;
        _exception = null;
        _success = true;
        _failure = false;
    }

    public Exception getException ()
    {
        return _exception;
    }

    public boolean isFailure ()
    {
        return _failure;
    }

    public R getResult ()
    {
        return _result;
    }

    public boolean isSuccess ()
    {
        return _success;
    }
    
}