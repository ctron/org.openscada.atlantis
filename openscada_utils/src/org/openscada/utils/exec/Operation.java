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
 * Base interface for operations (either sync or async based).
 * 
 * @param <R>
 *            The result type
 * @param <T>
 *            The argument type
 *            <p>
 *            The idea behind operations is that you have:
 *            <ul>
 *            <li>Synchronous operations</li>
 *            <li>Asynchronous operations</li>
 *            <li>Synchronous callers</li>
 *            <li>Asynchronous callers</li>
 *            <li>Callback callers</li>
 *            </ul>
 *            If you wish to:
 *            <ul>
 *            <li>Implement an operation that is synchronous you need to derive
 *            from {@link SyncBasedOperation}</li>
 *            <li>Implement an operation that s asynchronous you need to derive
 *            from {@link AsyncBasedOperation}</li>
 *            <li>Call an operation synchronously see {@link #execute}</li>
 *            <li>Call an operation asynchronously see
 *            {@link #startExecute(Object)}</li>
 *            <li>Call an operation an get notified by callback
 *            {@link #startExecute(OperationResultHandler handler, Object arg0)}</li>
 *            </ul>
 * @author jens
 */
public interface Operation<R, T>
{
    public R execute ( T arg0 ) throws Exception;

    public OperationResult<R> startExecute ( T arg0 );

    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 );
}
