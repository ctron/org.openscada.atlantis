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

package org.openscada.hd;

import java.util.Map;
import java.util.Set;

/**
 * A query listener interface
 * 
 * <p>
 * The query listener is called by the {@link Query} to notify the listener
 * of internal changes in the query. In most cases the creator of the query
 * is interested in receiving the query results.
 * </p>
 * <p>
 * The {@link #updateState(QueryState)} method may be called at any time by
 * the query and is only for informational purposes. The query can switch the
 * status without request from the client back to {@link QueryState#LOADING}
 * for example although the client did not request a change in parameters.
 * The query should however update the status only if necessary and use the
 * state {@link QueryState#LOADING} as an indicator that data is being
 * fetched from the archive and that values still need to be updated. It
 * should use {@link QueryState#COMPLETE} as an indicator that all data was
 * fetched from the archive. Data may still be updated in state
 * {@link QueryState#COMPLETE}.
 * </p>
 * <p>
 * The the query is closed (and only then) the query must set the status to
 * {@link QueryState#DISCONNECTED}.
 * </p>
 * <p>
 * Before any data is transmitted the method {@link #updateParameters(QueryParameters, Set)}
 * must be called including the actual parameters (although they might be the same
 * as the requested parameters) and including all provided value types. Further calls
 * to {@link #updateData(int, Map, ValueInformation[])} must contain exactly these value
 * types. After {@link #updateParameters(QueryParameters, Set)} was called all previously known
 * data is considered invalid.
 * </p>
 * <p>
 * Calls to {@link #updateData(int, Map, ValueInformation[])} will provide new values. The
 * call allows to split up data in packets. Queries should try to provide data as early as
 * possible but still should group data in chunks to reduce update calls.
 * The values must contain exactly the value types as announces by the previous call to
 * {@link #updateParameters(QueryParameters, Set)}. All arrays sizes must have the same length
 * for each call and must not exceed the announces entry size.  
 * </p>
 * <p>
 * There must be no calls the listener after the query was closed and the final state
 * change to {@link QueryState#DISCONNECTED} was send.
 * </p>
 * 
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 * @since 0.14.0
 * @see Query
 */
public interface QueryListener
{
    /**
     * Updates the state of the query
     * @param state the new state
     */
    public void updateState ( QueryState state );

    /**
     * Reports a change in parameters
     * @param parameters the new parameters, must not be <code>null</code>
     * @param valueTypes the new value types to expect, must not be <code>null</code> or empty
     */
    public void updateParameters ( QueryParameters parameters, Set<String> valueTypes );

    /**
     * Reports data update
     * <p>
     * All arrays must have the same size. 
     * </p>
     * 
     * @param index index from which the data update starts, must be greater or equal to zero
     * and lower than the reported number of entries
     * @param values the values that are updates, must not be <code>null</code>
     * @param valueInformation the value information for the provided values. must not be <code>null</code>
     */
    public void updateData ( int index, Map<String, Value[]> values, ValueInformation[] valueInformation );
}
