/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.exec;

/**
 * Implements an operation that is based on a asynchronous operation.
 * 
 * @author jens
 * @param <R>
 *            The result type
 * @param <T>
 *            The argument type
 */

public abstract class AsyncBasedOperation<R, T> implements Operation<R, T>
{

    public R execute ( T arg0 ) throws Exception
    {
        OperationResult<R> result = startExecute ( arg0 );

        result.complete ();

        if ( result.isSuccess () )
        {
            return result.get ();
        }
        else
        {
            throw result.getException ();
        }
    }

    protected abstract void startExecute ( OperationResult<R> or, T arg0 );

    public OperationResult<R> startExecute ( final T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ();

        startExecute ( or, arg0 );

        return or;
    }

    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ( handler );

        startExecute ( or, arg0 );

        return or;
    }

}
